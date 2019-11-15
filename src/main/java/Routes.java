import clases.Articulo;
import clases.Etiqueta;
import clases.Usuario;
import puentedb.PuenteArt;
import puentedb.PuenteUser;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Routes {
    public static void rutas(){
        Spark.get("/", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            System.out.println(PuenteArt.getInstance().cargarArticulos().get(0).getTitulo());
            atributos.put("posts", PuenteArt.getInstance().cargarArticulos());
            atributos.put("usuario", request.session(true).attribute("usuario"));
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"index.fml"));
        });

        Spark.get("/articulo/:idart", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            atributos.put("articulo", PuenteArt.getInstance().getArticulo(Long.parseLong(request.params("idArt"))));
            atributos.put("usuario", request.session(true).attribute("usuario"));
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"post.fml"));
        });

        Spark.get("/crearArticulo", (request, response) -> {
            Map<String,Object> atributos = new HashMap<>();
            return new FreeMarkerEngine().render(new ModelAndView(atributos,"createPost.fml"));
        });

        Spark.post("/crearArticulo", ((request, response) -> {
            String[] tags = request.queryParams("etiquetas").split(",");
            ArrayList<Etiqueta> etiquetas = new ArrayList<>();
            for (String tag: tags) {

                Etiqueta etiq = new Etiqueta(0,tag.trim());
            }
            Articulo art = new Articulo(0,request.queryParams("titulo"), request.queryParams("cuerpo"), PuenteUser.getInstance().getUser(Long.parseLong(request.session(true).attribute("id_usuario"))), new Date(new java.util.Date().getTime()), etiquetas);
            long id = PuenteArt.getInstance().createArticulo(art);
            response.redirect("/articulo"+ id);
            return null;
        }));

        Spark.get("/login", (request, response) -> {
            return new FreeMarkerEngine().render(new ModelAndView(null,"login.fml"));
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
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String nombre   = request.queryParams("nombre");
            boolean admin = false;
            boolean autor = request.queryParams("autor") != null;
            Usuario newUser = new Usuario(0,username, nombre, password,admin,autor);
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
