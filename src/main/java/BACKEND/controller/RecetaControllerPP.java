/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package BACKEND.controller;

import BACKEND.manager.RecetasManagerPP;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.core.Response;

@Path("Recetas")
public class RecetaControllerPP {

    RecetasManagerPP manager = new RecetasManagerPP();

    @GET
    @Path("CargarRecetas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> CargarRecetas()
            throws Exception {

        return manager.CargarRecetas();
    }

    @GET
    @Path("GetReceta")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> GetReceta(
            @QueryParam("id_receta") String id_receta)
            throws Exception {

        return manager.GetReceta(id_receta);
    }

    @GET
    @Path("CargarDetallesReceta")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> CargarDetallesReceta(
            @QueryParam("id_receta") String id_receta)
            throws Exception {

        return manager.CargarDetallesReceta(id_receta);
    }

    @POST
    @Path("InsReceta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String InsReceta(Map<String, Object> receta)
            throws Exception {

        return manager.InsReceta(
            receta.get("id_producto").toString(),
            receta.get("nombre").toString(),
            receta.get("rendimiento").toString()
        );
    }

    @PUT
    @Path("UpdReceta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String UpdReceta(Map<String, Object> receta)
            throws Exception {

        return manager.UpdReceta(
            receta.get("id_receta").toString(),
            receta.get("id_producto").toString(),
            receta.get("nombre").toString(),
            receta.get("rendimiento").toString(),
            receta.get("estado").toString()
        );
    }

    @POST
    @Path("InsDetalleReceta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String InsDetalleReceta(Map<String, Object> detalle)
            throws Exception {

        return manager.InsDetalleReceta(
            detalle.get("id_receta").toString(),
            detalle.get("id_producto").toString(),
            detalle.get("cantidad").toString()
        );
    }

    @PUT
    @Path("UpdDetalleReceta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String UpdDetalleReceta(Map<String, Object> detalle)
            throws Exception {

        return manager.UpdDetalleReceta(
            detalle.get("id_detalle_receta").toString(),
            detalle.get("id_producto").toString(),
            detalle.get("cantidad").toString()
        );
    }
    
    
    
@POST
@Path("InsRecetaCompleta")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response InsRecetaCompleta(Map<String, Object> receta) {

    try {

        // Obtener los datos principales
        int idProducto = Integer.parseInt(
            receta.get("id_producto").toString()
        );

        String nombre = receta.get("nombre").toString();

        BigDecimal rendimiento = new BigDecimal(
            receta.get("rendimiento").toString()
        );

        // Obtener la lista de ingredientes
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ingredientes =
            (List<Map<String, Object>>) receta.get("ingredientes");

        // Guardar receta e ingredientes en una transacción
        Map<String, Object> resultado =
            manager.InsRecetaCompleta(
                idProducto,
                nombre,
                rendimiento,
                ingredientes
            );

        return Response.ok(resultado).build();

    } catch (IllegalArgumentException | NullPointerException e) {

        Map<String, Object> error = new java.util.HashMap<>();

        error.put("EXITO", false);
        error.put("MENSAJE", "Los datos de la receta no son válidos.");
        error.put("ID_RECETA", null);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();

    } catch (Exception e) {

        e.printStackTrace();

        Map<String, Object> error = new java.util.HashMap<>();

        error.put("EXITO", false);
        error.put("MENSAJE", e.getMessage());
        error.put("ID_RECETA", null);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .build();
    }
}


@PUT
@Path("UpdRecetaCompleta")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Map<String, Object> UpdRecetaCompleta(
        Map<String, Object> datos
) {

    Map<String, Object> respuesta = new HashMap<>();

    try {

        if (datos == null) {
            throw new Exception(
                "No se recibieron datos para actualizar la receta."
            );
        }

        // 1. Obtener los datos principales.
        Object idRecetaObj = datos.get("id_receta");
        Object nombreObj = datos.get("nombre");
        Object rendimientoObj = datos.get("rendimiento");
        Object ingredientesObj = datos.get("ingredientes");

        if (idRecetaObj == null
                || nombreObj == null
                || rendimientoObj == null
                || ingredientesObj == null) {

            throw new Exception(
                "Debes enviar el ID de receta, nombre, "
                + "rendimiento e ingredientes."
            );
        }

        int idReceta = Integer.parseInt(
            idRecetaObj.toString()
        );

        String nombre = nombreObj.toString().trim();

        BigDecimal rendimiento = new BigDecimal(
            rendimientoObj.toString()
        );

        // 2. Obtener la lista de ingredientes.
        if (!(ingredientesObj instanceof List)) {
            throw new Exception(
                "El campo ingredientes debe ser una lista."
            );
        }

        List<?> listaRecibida = (List<?>) ingredientesObj;

        List<Map<String, Object>> ingredientes =
            new ArrayList<>();

        for (Object item : listaRecibida) {

            if (!(item instanceof Map)) {
                throw new Exception(
                    "Uno de los ingredientes tiene un formato incorrecto."
                );
            }

            Map<?, ?> ingredienteRecibido =
                (Map<?, ?>) item;

            Map<String, Object> ingrediente =
                new HashMap<>();

            ingrediente.put(
                "id_producto",
                ingredienteRecibido.get("id_producto")
            );

            ingrediente.put(
                "cantidad",
                ingredienteRecibido.get("cantidad")
            );

            ingredientes.add(ingrediente);
        }

        // 3. Actualizar la receta completa.
        respuesta = manager.UpdRecetaCompleta(
            idReceta,
            nombre,
            rendimiento,
            ingredientes
        );

    } catch (Exception e) {

        respuesta.put("EXITO", false);
        respuesta.put(
            "MENSAJE",
            e.getMessage() != null
                ? e.getMessage()
                : "Ocurrió un error al actualizar la receta."
        );
    }

    return respuesta;
}


}
