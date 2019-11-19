import clases.Usuario;
import puentedb.PuenteUser;
import spark.Session;
import spark.Spark;

public class Filter {
    public static void applyFilters(){
        Spark.before((request, response) -> {
            Usuario user = request.session().attribute("usuario");
            if(request.cookie("USER") != null && user == null){ //If the user is not logged, try to get the cookie to set a session
                long userUID = Long.parseLong(request.cookie("USER"));
                user = PuenteUser.getInstance().getUser(userUID);
                Session session = request.session(true);
                session.attribute("usuario", user);
            }
        });
        Spark.before("/crearArticulo", (request, response) -> {
            Usuario user = request.session().attribute("usuario");
            if(user == null){
                response.redirect("/login");
            }
        });
        Spark.before("/postearComentario", (request, response) -> {
            Usuario user = request.session().attribute("usuario");
            if(user == null){
                response.redirect("/login");
            }
        });
    }
}
