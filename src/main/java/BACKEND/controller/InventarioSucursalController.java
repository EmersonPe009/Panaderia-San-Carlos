
package BACKEND.controller;
import BACKEND.manager.InventarioSucursalManager;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import java.math.BigDecimal;
import java.util.HashMap;
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

@Path("/InventarioSucursal")
public class InventarioSucursalController {

    private final InventarioSucursalManager manager =
            new InventarioSucursalManager();

    @GET
    @Path("/MiInventario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response miInventario(
            @Context HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null ||
                sesion.getAttribute("ID_USUARIO") == null) {

            return Response.status(401)
                    .entity("{\"MENSAJE\":\"Sesion no iniciada\"}")
                    .build();
        }

        Object idSucursalObj =
                sesion.getAttribute("ID_SUCURSAL");
        
Object rol = sesion.getAttribute("ROL");

if (!"SUCURSAL".equalsIgnoreCase(String.valueOf(rol))) {
    return Response.status(403)
            .entity("{\"MENSAJE\":\"Acceso no autorizado\"}")
            .build();
}

        
        if (!(idSucursalObj instanceof Number)) {

            return Response.status(403)
                    .entity("{\"MENSAJE\":\"Usuario sin sucursal asignada\"}")
                    .build();
        }

        int idSucursal =
                ((Number) idSucursalObj).intValue();

        if (idSucursal <= 0) {
            return Response.status(403)
                    .entity("{\"MENSAJE\":\"Sucursal no valida\"}")
                    .build();
        }

        try {

            List<Map<String, Object>> productos =
                    manager.cargarInventario(idSucursal);

            Map<String, Object> respuesta =
                    new HashMap<>();

            respuesta.put("ID_SUCURSAL", idSucursal);

            respuesta.put(
                    "NOMBRE_SUCURSAL",
                    sesion.getAttribute("NOMBRE_SUCURSAL")
            );

            respuesta.put("PRODUCTOS", productos);

            return Response.ok(respuesta).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity("{\"MENSAJE\":\"Error al cargar inventario\"}")
                    .build();
        }
    }


@POST
@Path("/ActualizarResto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response actualizarResto(
        Map<String, Object> datos,
        @Context HttpServletRequest request) {

    HttpSession sesion = request.getSession(false);

    if (sesion == null ||
            sesion.getAttribute("ID_USUARIO") == null) {
        return Response.status(401)
                .entity("{\"MENSAJE\":\"Sesion no iniciada\"}")
                .build();
    }

    if (!"SUCURSAL".equalsIgnoreCase(
            String.valueOf(sesion.getAttribute("ROL")))) {
        return Response.status(403)
                .entity("{\"MENSAJE\":\"Acceso no autorizado\"}")
                .build();
    }

    Object idUsuarioObj = sesion.getAttribute("ID_USUARIO");
    Object idSucursalObj = sesion.getAttribute("ID_SUCURSAL");

    if (!(idUsuarioObj instanceof Number) ||
            !(idSucursalObj instanceof Number)) {
        return Response.status(403)
                .entity("{\"MENSAJE\":\"Sesion invalida\"}")
                .build();
    }

    if (datos == null) {
        return Response.status(400)
                .entity("{\"MENSAJE\":\"Faltan datos\"}")
                .build();
    }

    int idUsuario = ((Number) idUsuarioObj).intValue();
    int idSucursal = ((Number) idSucursalObj).intValue();

    int idProducto;
    BigDecimal nuevaExistencia;

    try {
        idProducto = Integer.parseInt(
                String.valueOf(datos.get("id_producto"))
        );

        nuevaExistencia = new BigDecimal(
                String.valueOf(datos.get("nueva_existencia"))
        );

        if (idProducto <= 0 ||
                nuevaExistencia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

    } catch (Exception e) {
        return Response.status(400)
                .entity("{\"MENSAJE\":\"Producto o cantidad invalida\"}")
                .build();
    }

    try {
        String mensaje = manager.actualizarResto(
                idSucursal,
                idProducto,
                idUsuario,
                nuevaExistencia
        );

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("EXITO", true);
        respuesta.put("MENSAJE", mensaje);

        return Response.ok(respuesta).build();

    } catch (Exception e) {
        e.printStackTrace();

        Map<String, Object> error = new HashMap<>();
        error.put("EXITO", false);
        error.put("MENSAJE", "No se pudo actualizar el RESTO.");

        return Response.status(400)
                .entity(error)
                .build();
    }
}

}
