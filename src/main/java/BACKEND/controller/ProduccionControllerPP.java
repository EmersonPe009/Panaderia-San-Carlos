
package BACKEND.controller;

import BACKEND.manager.ProduccionManagerPP;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.util.Map;
import java.util.HashMap;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

@Path("Produccion")
@Produces(MediaType.APPLICATION_JSON)
public class ProduccionControllerPP {

    private final ProduccionManagerPP manager =
            new ProduccionManagerPP();
@Context
private HttpServletRequest request;


private HttpSession sesion() {
    return request.getSession(false);
}

private String rol() {
    HttpSession s = sesion();

    return s == null
            ? ""
            : String.valueOf(s.getAttribute("ROL"));
}

private boolean autenticado() {
    HttpSession s = sesion();

    return s != null
            && s.getAttribute("ID_USUARIO") != null;
}

private boolean puedeGenerarProduccion() {
    String rolUsuario = rol();

    return "ADMINISTRADOR".equalsIgnoreCase(rolUsuario)
            || "PRODUCCION".equalsIgnoreCase(rolUsuario);
}

private Response sinSesion() {
    return Response.status(401)
            .entity(mensaje("Debes iniciar sesión."))
            .build();
}

private Response accesoDenegado() {
    return Response.status(403)
            .entity(mensaje(
                    "No tienes permiso para generar producciones."
            ))
            .build();
}



    private Response error(Exception e) {

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("EXITO", false);
        resultado.put("MENSAJE", e.getMessage());

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        ).entity(resultado).build();
    }

    @GET
    @Path("CargarProducciones")
    public Response cargarProducciones() {

        try {
            return Response.ok(
                    manager.CargarProducciones()
            ).build();

        } catch (Exception e) {
            return error(e);
        }
    }
    
    
    
@GET
@Path("CargarDemandaProduccion")
public Response cargarDemandaProduccion() {

    try {

        return Response.ok(
                manager.CargarDemandaProduccion()
        ).build();

    } catch (Exception e) {
        return error(e);
    }
}


    @GET
    @Path("GetProduccion")
    public Response getProduccion(
            @QueryParam("id_produccion") Integer id) {

        try {
            if (id == null || id <= 0) {
                return Response.status(400)
                        .entity(mensaje("ID de producción inválido."))
                        .build();
            }

            return Response.ok(
                    manager.GetProduccion(id)
            ).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    @GET
    @Path("CargarDetallesProduccion")
    public Response cargarDetalles(
            @QueryParam("id_produccion") Integer id) {

        try {
            if (id == null || id <= 0) {
                return Response.status(400)
                        .entity(mensaje("ID de producción inválido."))
                        .build();
            }

            return Response.ok(
                    manager.CargarDetallesProduccion(id)
            ).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    @POST
    @Path("InsProduccionCompleta")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertarProduccion(String json) {

        try {
            Map<String, Object> resultado =
                    manager.InsProduccionCompleta(json);

            return Response.status(
                    Boolean.TRUE.equals(resultado.get("EXITO"))
                            ? 200 : 400
            ).entity(resultado).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    @PUT
    @Path("FinalizarProduccion")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response finalizarProduccion(
            Map<String, Object> datos) {

        try {

            if (datos == null ||
                    datos.get("id_produccion") == null) {

                return Response.status(400)
                        .entity(mensaje(
                                "Debes indicar el ID de producción."
                        ))
                        .build();
            }

            int id = Integer.parseInt(
                    datos.get("id_produccion").toString()
            );

            Map<String, Object> resultado =
                    manager.FinalizarProduccion(id);

            return Response.status(
                    Boolean.TRUE.equals(resultado.get("EXITO"))
                            ? 200 : 400
            ).entity(resultado).build();

        } catch (NumberFormatException e) {
            return Response.status(400)
                    .entity(mensaje("ID de producción inválido."))
                    .build();

        } catch (Exception e) {
            return error(e);
        }
    }

    private Map<String, Object> mensaje(String texto) {

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("EXITO", false);
        resultado.put("MENSAJE", texto);

        return resultado;
    }
    
    
    
    
@PUT
@Path("IniciarProduccion")
@Consumes(MediaType.APPLICATION_JSON)
public Response iniciarProduccion(
        Map<String, Object> datos) {

    try {

        if (datos == null ||
                datos.get("id_produccion") == null) {

            return Response.status(400)
                    .entity(mensaje(
                            "Debes indicar el ID de producción."
                    ))
                    .build();
        }

        int id = Integer.parseInt(
                datos.get("id_produccion").toString()
        );

        Map<String, Object> resultado =
                manager.IniciarProduccion(id);

        return Response.status(
                Boolean.TRUE.equals(resultado.get("EXITO"))
                        ? 200 : 400
        ).entity(resultado).build();

    } catch (IllegalArgumentException e) {

        return Response.status(400)
                .entity(mensaje(e.getMessage()))
                .build();

    } catch (Exception e) {
        return error(e);
    }
}



@POST
@Path("GenerarProduccionConsolidada")
@Consumes(MediaType.APPLICATION_JSON)
public Response generarProduccionConsolidada(String json) {

    // Verificar que el usuario tenga sesión.
    if (!autenticado()) {
        return sinSesion();
    }

    // Permitir únicamente ADMINISTRADOR y PRODUCCION.
    if (!puedeGenerarProduccion()) {
        return accesoDenegado();
    }

    try {

        if (json == null || json.trim().isEmpty()) {
            return Response.status(400)
                    .entity(mensaje(
                            "Debes enviar la fecha y el turno."
                    ))
                    .build();
        }

        com.google.gson.JsonObject datos =
                new com.google.gson.JsonParser()
                        .parse(json)
                        .getAsJsonObject();

        if (!datos.has("fecha")
                || !datos.has("turno")
                || datos.get("fecha").isJsonNull()
                || datos.get("turno").isJsonNull()) {

            return Response.status(400)
                    .entity(mensaje(
                            "Debes indicar la fecha y el turno."
                    ))
                    .build();
        }

        String fecha = datos.get("fecha").getAsString();
        String turno = datos.get("turno").getAsString();

        Map<String, Object> resultado =
                manager.GenerarProduccionConsolidada(
                        fecha,
                        turno
                );

        boolean exito =
                Boolean.TRUE.equals(resultado.get("exito"));

        // Unificar respuesta con los demás endpoints.
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("EXITO", exito);
        respuesta.put(
                "MENSAJE",
                resultado.get("mensaje")
        );
        respuesta.put(
                "ID_PRODUCCION",
                resultado.get("id_produccion")
        );

        return Response.status(
                exito ? 200 : 409
        ).entity(respuesta).build();

    } catch (IllegalArgumentException e) {

        return Response.status(400)
                .entity(mensaje(e.getMessage()))
                .build();

    } catch (Exception e) {

        return error(e);
    }
}

}
