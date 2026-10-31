
package BACKEND.controller;

import BACKEND.manager.EstadisticasManagerPP;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Estadisticas")
@Produces(MediaType.APPLICATION_JSON)
public class EstadisticasControllerPP {

    private final EstadisticasManagerPP manager =
            new EstadisticasManagerPP();

    @Context
    private HttpServletRequest request;

    // =========================================
    // VALIDAR SESIÓN DE ADMINISTRADOR
    // =========================================
    private Response validarAcceso() {

        HttpSession sesion =
                request.getSession(false);

        if (sesion == null
                || sesion.getAttribute("ID_USUARIO") == null) {

            return Response.status(
                    Response.Status.UNAUTHORIZED
            ).entity(
                    "{\"MENSAJE\":\"Debes iniciar sesión.\"}"
            ).build();
        }

        String rol = String.valueOf(
                sesion.getAttribute("ROL")
        );

        if (!"ADMINISTRADOR".equalsIgnoreCase(rol)) {

            return Response.status(
                    Response.Status.FORBIDDEN
            ).entity(
                    "{\"MENSAJE\":\"Acceso no autorizado.\"}"
            ).build();
        }

        return null;
    }

    // =========================================
    // RESUMEN GENERAL
    // =========================================
    @GET
    @Path("ResumenGeneral")
    public Response ResumenGeneral() {

        Response acceso = validarAcceso();

        if (acceso != null) {
            return acceso;
        }

        try {

            List<Map<String, Object>> datos =
                    manager.ResumenGeneral();

            return Response.ok(datos).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.serverError()
                    .entity("{\"MENSAJE\":\"Error al consultar el resumen general.\"}")
                    .build();
        }
    }

    // =========================================
    // RANKING DE SUCURSALES
    // =========================================
    @GET
    @Path("RankingSucursales")
    public Response RankingSucursales() {

        Response acceso = validarAcceso();

        if (acceso != null) {
            return acceso;
        }

        try {

            List<Map<String, Object>> datos =
                    manager.RankingSucursales();

            return Response.ok(datos).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.serverError()
                    .entity("{\"MENSAJE\":\"Error al consultar el ranking de sucursales.\"}")
                    .build();
        }
    }

    // =========================================
    // INVENTARIO BAJO
    // =========================================
    @GET
    @Path("InventarioBajo")
    public Response InventarioBajo() {

        Response acceso = validarAcceso();

        if (acceso != null) {
            return acceso;
        }

        try {

            List<Map<String, Object>> datos =
                    manager.InventarioBajo();

            return Response.ok(datos).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.serverError()
                    .entity("{\"MENSAJE\":\"Error al consultar el inventario.\"}")
                    .build();
        }
    }
    
    // =========================================
// RESUMEN DE VENTAS REALES
// =========================================
@GET
@Path("ResumenVentas")
public Response ResumenVentas() {

    try {

        return Response.ok(
                manager.ResumenVentas()
        ).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        ).entity(
                "{\"MENSAJE\":\"Error al cargar resumen de ventas.\"}"
        ).build();
    }
}
}
