
package BACKEND.controller;
import BACKEND.manager.InventarioSucursalManager;

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
}
