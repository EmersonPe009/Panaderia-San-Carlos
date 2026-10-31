
package BACKEND;

import BACKEND.manager.UsuariosManagerPP;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Usuarios")
public class UsuariosControllerPP {

    private final UsuariosManagerPP manager =
            new UsuariosManagerPP();

    @POST
    @Path("/Login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            Map<String, String> datos,
            @Context HttpServletRequest request) {

        if (datos == null) {
            return Response.status(400)
                    .entity("{\"MENSAJE\":\"Datos inválidos.\"}")
                    .build();
        }

        String usuario = datos.get("usuario");
        String contrasena = datos.get("contrasena");

        if (usuario == null || contrasena == null
                || usuario.trim().isEmpty()
                || contrasena.isEmpty()) {

            return Response.status(400)
                    .entity("{\"MENSAJE\":\"Ingresa usuario y contraseña.\"}")
                    .build();
        }

        try {

            Map<String, Object> resultado =
                    manager.ValidarLogin(usuario, contrasena);

            boolean autenticado =
                    Boolean.TRUE.equals(resultado.get("AUTENTICADO"));

            if (!autenticado) {

                return Response.status(401)
                        .entity(resultado)
                        .build();
            }

            // Invalidar una sesión anterior, si existe
            HttpSession anterior = request.getSession(false);

            if (anterior != null) {
                anterior.invalidate();
            }

            // Crear sesión para el usuario autenticado
            HttpSession sesion = request.getSession(true);

            sesion.setAttribute("ID_USUARIO",
                    resultado.get("ID_USUARIO"));

            sesion.setAttribute("USUARIO",
                    resultado.get("USUARIO"));

            sesion.setAttribute("NOMBRE_COMPLETO",
                    resultado.get("NOMBRE_COMPLETO"));

            sesion.setAttribute("ROL",
                    resultado.get("ROL"));
            
            
            sesion.setAttribute("ID_ROL",
        resultado.get("ID_ROL"));

sesion.setAttribute("ID_SUCURSAL",
        resultado.get("ID_SUCURSAL"));

sesion.setAttribute("NOMBRE_SUCURSAL",
        resultado.get("NOMBRE_SUCURSAL"));

            sesion.setMaxInactiveInterval(30 * 60);

            // Nunca guardar la contraseña en sesión
            // ni devolver el hash al navegador

            return Response.ok(resultado).build();

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, String> error =
                    new HashMap<String, String>();

            error.put("MENSAJE",
                    "Error interno al iniciar sesión.");

            return Response.status(500)
                    .entity(error)
                    .build();
        }
    }
}
