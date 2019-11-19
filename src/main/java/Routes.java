import clases.Articulo;
import clases.Comentario;
import clases.Etiqueta;
import clases.Usuario;
import puentedb.*;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routes {
    public static void rutas(){
        Spark.get("/", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            //System.out.println(PuenteArt.getInstance().cargarArticulos().get(0).getAutor().getId());
            atributos.put("posts", PuenteArt.getInstance().cargarArticulos());
            atributos.put("usuario", request.session(true).attribute("usuario"));
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"index.fml"));
        });
        Spark.get("/articulo/:idart", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            atributos.put("articulo", PuenteArt.getInstance().getArticulo(Long.parseLong(request.params("idArt"))));
            atributos.put("usuario", request.session().attribute("usuario"));
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"post.fml"));
        });
        Spark.get("/crearArticulo", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            atributos.put("usuario", request.session().attribute("usuario"));
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"create.fml"));
        });
        Spark.get("/modArticulo/:idArt", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            List tagList = new ArrayList<>();
            Articulo toMod = PuenteArt.getInstance().getArticulo(Long.parseLong(request.params("idArt")));
            ArrayList<Etiqueta> etiqs = toMod.getListaEtiquetas();
            for (Etiqueta e:etiqs) {
                tagList.add(e.getEtiqueta());
            }
            String tagString = String.join(",",tagList);
            atributos.put("tetel",   toMod.getTitulo());
            atributos.put("cuerpo",  toMod.getCuerpo());
            atributos.put("tags",    tagString);
            atributos.put("idMod",   toMod.getId());
            atributos.put("usuario", toMod.getAutor());
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"create.fml"));
        });
        Spark.post("/modArticulo/:idArt", ((request, response) -> {
            String[] tags = request.queryParams("tags").split(",");
            System.out.println(request.queryParams("tags"));
            System.out.println(tags);
            System.out.println(request.queryParams());
            ArrayList<Etiqueta> etiquetas = new ArrayList<>();
            for (String tag: tags) {
                if(PuenteEtiqueta.getInstance().getEtiqueta(tag)!=null){
                    etiquetas.add(PuenteEtiqueta.getInstance().getEtiqueta(tag));
                }else{
                    long newEt = PuenteEtiqueta.getInstance().crearEtiqueta(new Etiqueta(0,tag));

                    etiquetas.add(PuenteEtiqueta.getInstance().getEtiqueta(newEt));
                }
            }
            Articulo art = new Articulo(Long.parseLong(request.params("idArt")),request.queryParams("titulo"), request.queryParams("cuerpo"), PuenteArt.getInstance().getArticulo(Long.parseLong(request.params("idArt"))).getAutor(), LocalDateTime.now(), etiquetas);
            PuenteArt.getInstance().modifyArticulo(art);
            response.redirect("/articulo/"+ request.params("idArt"));
            return null;
        }));
        Spark.post("/crearArticulo", ((request, response) -> {
            String[] tags = request.queryParams("tags").split(",");
            System.out.println(request.queryParams("tags"));
            System.out.println(tags);
            System.out.println(request.queryParams());
            ArrayList<Etiqueta> etiquetas = new ArrayList<>();
            for (String tag: tags) {
               if(PuenteEtiqueta.getInstance().getEtiqueta(tag)!=null){
                   etiquetas.add(PuenteEtiqueta.getInstance().getEtiqueta(tag));
               }else{
                   long newEt = PuenteEtiqueta.getInstance().crearEtiqueta(new Etiqueta(0,tag));

                   etiquetas.add(PuenteEtiqueta.getInstance().getEtiqueta(newEt));
               }
            }
            etiquetas.forEach(part -> System.out.println(part.getEtiqueta()));
            Articulo art = new Articulo(0,request.queryParams("titulo"), request.queryParams("cuerpo"), request.session().attribute("usuario"), LocalDateTime.now(), etiquetas);
            long id = PuenteArt.getInstance().createArticulo(art);
            response.redirect("/articulo/"+ id);
            return null;
        }));
        Spark.post("/postearComentario", (request, response) -> {
            System.out.println(request.queryParams("articuloId"));
            long idArt = Long.parseLong(request.queryParams("articuloId"));
            String comentario = request.queryParams("comment");
            Comentario newComment = new Comentario(0,comentario,request.session().attribute("usuario"),idArt,LocalDateTime.now());
            PuenteComentario.getInstance().crearComentario(newComment);
            response.redirect("/articulo/"+ idArt);
            return null;
        });
        Spark.post("/delArticulo/:idArt", (request, response) -> {
            long idArt = Long.parseLong(request.params("idArt"));
            PuenteArtEtiqueta.getInstance().borrarEtiquetaArt(idArt);
            PuenteArt.getInstance().deleteArticulo(idArt);
            response.redirect("/");
            return null;
        });
        Spark.post("/delComentario/:idComment",(request, response) -> {
            long idComment = Long.parseLong(request.params("idComment"));
            long idArt = PuenteComentario.getInstance().getComentario(idComment).getArticulo();
            PuenteComentario.getInstance().deleteComentario(idComment);
            response.redirect("/articulo/"+idArt);
            return null;
        });
        Spark.get("/login", (request, response) -> {
            return new FreeMarkerEngine().render(new ModelAndView(null,"login.fml"));
        });
        Spark.post("/logout", (request, response) -> {
            Session session = request.session(true);
            session.invalidate();
            response.removeCookie("USER");
            response.redirect("/");
            return null;
        });
        Spark.post("/login",(request, response) -> {
            String username = request.queryParams("usuario");
            String password = request.queryParams("contrasena");
            if(!PuenteUser.getInstance().validateCredentials(username,password)){
                Map<String,Object> atributos = new HashMap<>();
                atributos.put("mensaje", "Credenciales incorrectas, intentelo de nuevo");
                return new FreeMarkerEngine().render(new ModelAndView(atributos,"login.fml"));
            }else{
                Session session = request.session(true);
                Usuario user    = PuenteUser.getInstance().getUser(username);
                session.attribute("usuario", user);
                Boolean rememberMe = false;
                if(request.queryParams("rememberMe") != null) {
                    rememberMe = true;
                }
                if(rememberMe){
                    response.cookie("USER", Long.toString(user.getId()), 604800);
                }
                response.redirect("/");
            }
            return null;
        });
        Spark.get("/register", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"register.fml"));
        });
        Spark.post("/register", (request, response) -> {
            String username = request.queryParams("usuario");
            String password = request.queryParams("contrasena");
            String nombre   = request.queryParams("nombre");
            boolean admin = false;
            boolean autor = request.queryParams("autor") != null;
            MessageDigest md   = MessageDigest.getInstance("SHA-224");
            byte[] hashPassEnt = md.digest(password.getBytes(StandardCharsets.UTF_8));
            Usuario newUser = new Usuario(0,username, nombre, hashPassEnt,admin,autor);
            if(PuenteUser.getInstance().crearUsuario(newUser)){
                Map<String,Object> atributos = new HashMap<>();
                atributos.put("mensaje", "Registrado con exito, proceda a autenticarse");
                return new FreeMarkerEngine().render(new ModelAndView(atributos,"register.fml"));
            }else{
                Map<String,Object> atributos = new HashMap<>();
                atributos.put("mensaje", "algo salió mal, intentelo de nuevo");
                return new FreeMarkerEngine().render(new ModelAndView(atributos,"login.fml"));
            }
        });
    }
}
