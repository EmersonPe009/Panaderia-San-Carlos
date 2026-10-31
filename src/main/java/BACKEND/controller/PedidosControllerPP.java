
package BACKEND.controller;

import BACKEND.manager.PedidosManagerPP;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Pedidos")
@Produces(MediaType.APPLICATION_JSON)
public class PedidosControllerPP {

    private final PedidosManagerPP manager =
            new PedidosManagerPP();

    @Context
    private HttpServletRequest request;

    private Map<String, Object> mensaje(String texto) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("EXITO", false);
        respuesta.put("MENSAJE", texto);
        return respuesta;
    }

    private Response error(Exception e) {
        return Response.status(500)
                .entity(mensaje("Error al procesar la solicitud."))
                .build();
    }

    private HttpSession sesion() {
        return request.getSession(false);
    }

    private String rol() {
        HttpSession s = sesion();
        return s == null ? "" :
                String.valueOf(s.getAttribute("ROL"));
    }

    private boolean administrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(rol());
    }

    private boolean empleadoSucursal() {
        return "SUCURSAL".equalsIgnoreCase(rol());
    }

    private Integer sucursalSesion() {

        HttpSession s = sesion();

        if (s == null) {
            return null;
        }

        Object valor = s.getAttribute("ID_SUCURSAL");

        if (valor == null) {
            return null;
        }

        try {
            return Integer.valueOf(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Response accesoDenegado() {
        return Response.status(403)
                .entity(mensaje("No tienes permiso para esta operación."))
                .build();
    }

    private Response sinSesion() {
        return Response.status(401)
                .entity(mensaje("Debes iniciar sesión."))
                .build();
    }

    private boolean autenticado() {
        HttpSession s = sesion();
        return s != null && s.getAttribute("ID_USUARIO") != null;
    }

    // ==========================================
    // LISTAR PEDIDOS
    // ==========================================
    @GET
    @Path("CargarPedidos")
    public Response cargarPedidos() {

        if (!autenticado()) {
            return sinSesion();
        }

        try {
            if (administrador()) {
                return Response.ok(
                        manager.CargarPedidos()
                ).build();
            }

            if (empleadoSucursal()) {

                Integer idSucursal = sucursalSesion();

                if (idSucursal == null) {
                    return accesoDenegado();
                }

                return Response.ok(
                        manager.CargarPedidosSucursal(idSucursal)
                ).build();
            }

            return accesoDenegado();

        } catch (Exception e) {
            return error(e);
        }
    }

    // ==========================================
    // CONSULTAR UN PEDIDO
    // ==========================================
    @GET
    @Path("GetPedido")
    public Response getPedido(
            @QueryParam("id_pedido") int id) {

        if (!autenticado()) {
            return sinSesion();
        }

        if (id <= 0) {
            return Response.status(400)
                    .entity(mensaje("ID de pedido inválido."))
                    .build();
        }

        try {

            if (empleadoSucursal()) {

                Integer idSucursal = sucursalSesion();

                if (idSucursal == null
                        || !manager.PerteneceASucursal(
                                id, idSucursal)) {

                    return accesoDenegado();
                }

            } else if (!administrador()) {
                return accesoDenegado();
            }

            return Response.ok(
                    manager.GetPedido(id)
            ).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    // ==========================================
    // CONSULTAR DETALLES
    // ==========================================
    @GET
    @Path("CargarDetallesPedido")
    public Response cargarDetallesPedido(
            @QueryParam("id_pedido") int id) {

        if (!autenticado()) {
            return sinSesion();
        }

        if (id <= 0) {
            return Response.status(400)
                    .entity(mensaje("ID de pedido inválido."))
                    .build();
        }

        try {

            if (empleadoSucursal()) {

                Integer idSucursal = sucursalSesion();

                if (idSucursal == null
                        || !manager.PerteneceASucursal(
                                id, idSucursal)) {

                    return accesoDenegado();
                }

            } else if (!administrador()) {
                return accesoDenegado();
            }

            return Response.ok(
                    manager.CargarDetallesPedido(id)
            ).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    // ==========================================
    // REGISTRAR PEDIDO COMPLETO
    // ==========================================
    @POST
    @Path("InsPedidoCompleto")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insPedidoCompleto(
            Map<String, Object> datos) {

        if (!autenticado()) {
            return sinSesion();
        }

        if (!administrador() && !empleadoSucursal()) {
            return accesoDenegado();
        }

        if (datos == null) {
            return Response.status(400)
                    .entity(mensaje("Debes enviar los datos del pedido."))
                    .build();
        }

        try {

            if (empleadoSucursal()) {

                Integer idSucursal = sucursalSesion();

                if (idSucursal == null) {
                    return accesoDenegado();
                }

                // Ignorar cualquier sucursal enviada
                // desde el navegador.
                datos.put("id_sucursal", idSucursal);

            } else {

                Object idSucursal = datos.get("id_sucursal");

                if (idSucursal == null) {
                    return Response.status(400)
                            .entity(mensaje("Debes seleccionar una sucursal."))
                            .build();
                }
            }

            // Jersey/Jackson debe serializar el mapa
            // para conservar el formato JSON del paquete.
            String json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(datos);

            Map<String, Object> resultado =
                    manager.InsPedidoCompleto(json);

            return Response.status(
                    Boolean.TRUE.equals(resultado.get("EXITO"))
                            ? 200 : 400
            ).entity(resultado).build();

        } catch (Exception e) {
            return error(e);
        }
    }

    // ==========================================
    // DESPACHAR PEDIDO
    // ==========================================
    @PUT
    @Path("DespacharPedido")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response despacharPedido(
            Map<String, Object> datos) {

        if (!autenticado()) {
            return sinSesion();
        }

        // Solo el administrador despacha.
        if (!administrador()) {
            return accesoDenegado();
        }

        try {

            if (datos == null || datos.get("id_pedido") == null) {
                return Response.status(400)
                        .entity(mensaje("Debes indicar el ID del pedido."))
                        .build();
            }

            int id = Integer.parseInt(
                    datos.get("id_pedido").toString()
            );

            if (id <= 0) {
                return Response.status(400)
                        .entity(mensaje("ID de pedido inválido."))
                        .build();
            }

            Map<String, Object> resultado =
                    manager.DespacharPedido(id);

            return Response.status(
                    Boolean.TRUE.equals(resultado.get("EXITO"))
                            ? 200 : 400
            ).entity(resultado).build();

        } catch (NumberFormatException e) {
            return Response.status(400)
                    .entity(mensaje("El ID debe ser numérico."))
                    .build();

        } catch (Exception e) {
            return error(e);
        }
    }
    
    // ==========================================
    // REGISTRAR RESTO DE PEDIDO
    // ==========================================
    @PUT
    @Path("RegistrarResto")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarResto(
            Map<String, Object> datos) {

        if (!autenticado()) {
            return sinSesion();
        }

        // Solamente empleados de sucursal.
        if (!empleadoSucursal()) {
            return accesoDenegado();
        }

        if (datos == null
                || datos.get("id_detalle_pedido") == null
                || datos.get("cantidad_resto") == null) {

            return Response.status(400)
                    .entity(mensaje(
                            "Debes indicar el detalle y la cantidad sobrante."
                    ))
                    .build();
        }

        try {

            int idDetalle = Integer.parseInt(
                    datos.get("id_detalle_pedido").toString()
            );

            int cantidadResto = Integer.parseInt(
                    datos.get("cantidad_resto").toString()
            );

            if (idDetalle <= 0 || cantidadResto < 0) {
                return Response.status(400)
                        .entity(mensaje(
                                "Los valores enviados no son válidos."
                        ))
                        .build();
            }

            Integer idSucursal = sucursalSesion();

            if (idSucursal == null) {
                return accesoDenegado();
            }

            // Comprobar a qué pedido pertenece el detalle.
            Integer idPedido =
                    manager.ObtenerPedidoDeDetalle(idDetalle);

            if (idPedido == null
                    || !manager.PerteneceASucursal(
                            idPedido, idSucursal)) {

                return accesoDenegado();
            }

            Map<String, Object> resultado =
                    manager.RegistrarResto(
                            idDetalle,
                            cantidadResto
                    );

            return Response.status(
                    Boolean.TRUE.equals(resultado.get("EXITO"))
                            ? 200 : 400
            ).entity(resultado).build();

        } catch (NumberFormatException e) {

            return Response.status(400)
                    .entity(mensaje(
                            "Los valores deben ser números enteros."
                    ))
                    .build();

        } catch (Exception e) {
            return error(e);
        }
    }

    
}
