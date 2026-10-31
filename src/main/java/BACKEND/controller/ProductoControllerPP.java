/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BACKEND.controller;

import BACKEND.manager.ProductosManagerPP;
import BACKEND.utils.ConnectionsPool;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import oracle.jdbc.OracleTypes;

@Path("Productos")
public class ProductoControllerPP {

    ProductosManagerPP manager = new ProductosManagerPP();

    @GET
    @Path("CargarProductos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> CargarProductos() throws Exception {

        return manager.CargarProductos();
    }
    
    @GET
@Path("GetProducto")
@Produces(MediaType.APPLICATION_JSON)
public List<Map<String, Object>> GetProducto(
        @javax.ws.rs.QueryParam("id_producto") String id_producto) throws Exception {

    return manager.GetProducto(id_producto);
}




@POST
@Path("InsProducto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response InsProducto(Map<String, Object> producto) {

    try {

        Map<String, Object> resultado = manager.InsProducto(
                producto.get("nombre").toString(),
                producto.get("codigo").toString(),
                producto.get("tipo_producto").toString(),
                producto.get("unidad_medida").toString(),
                producto.get("precio").toString(),
                producto.get("existencia").toString(),
                producto.get("existencia_minima").toString()
        );

        boolean exito = Boolean.TRUE.equals(
                resultado.get("EXITO")
        );

        if (exito) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();

    } catch (Exception e) {

        e.printStackTrace();

        Map<String, Object> error = new java.util.HashMap<>();

        error.put("EXITO", false);
        error.put(
                "MENSAJE",
                "No se pudo registrar el producto."
        );
        error.put("ID_PRODUCTO", null);

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        )
        .entity(error)
        .build();
    }
}






@javax.ws.rs.PUT
@Path("UpdProducto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public String UpdProducto(Map<String, Object> producto) throws Exception {

    return manager.UpdProducto(
        producto.get("id_producto").toString(),
        producto.get("nombre").toString(),
        producto.get("codigo").toString(),
        producto.get("tipo_producto").toString(),
        producto.get("unidad_medida").toString(),
        producto.get("precio").toString(),
        producto.get("existencia").toString(),
        producto.get("existencia_minima").toString(),
        producto.get("estado").toString()
    );
}
    


@GET
@Path("/GetEstadisticas")
@Produces(MediaType.APPLICATION_JSON)
public Response GetEstadisticas() {

    try {

        ProductosManagerPP manager =
                new ProductosManagerPP();

        Map<String, Object> estadisticas =
                manager.GetEstadisticas();

        return Response.ok(estadisticas).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        )
        .entity("{\"MENSAJE\":\"Error al obtener estadísticas.\"}")
        .build();
    }
}


/* =========================================
   GRÁFICA 1: TOP 5 EXISTENCIAS
========================================= */

@GET
@Path("/GetTopExistencias")
@Produces(MediaType.APPLICATION_JSON)
public Response GetTopExistencias() {

    try {

        ProductosManagerPP manager =
                new ProductosManagerPP();

        return Response.ok(
                manager.GetTopExistencias()
        ).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        )
        .entity("Error al obtener existencias.")
        .build();
    }
}


/* =========================================
   GRÁFICA 2: DISTRIBUCIÓN POR TIPO
========================================= */

@GET
@Path("/GetDistribucionTipos")
@Produces(MediaType.APPLICATION_JSON)
public Response GetDistribucionTipos() {

    try {

        ProductosManagerPP manager =
                new ProductosManagerPP();

        return Response.ok(
                manager.GetDistribucionTipos()
        ).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
        )
        .entity("Error al obtener distribución.")
        .build();
    }
}


/* =========================================
   INGRESAR EXISTENCIAS DE INSUMOS
========================================= */

@POST
@Path("IngresarExistencias")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response IngresarExistencias(Map<String, Object> datos) {

    Map<String, Object> resultado = new HashMap<>();

    try {

        if (datos == null
                || datos.get("ID_PRODUCTO") == null
                || datos.get("CANTIDAD") == null) {

            resultado.put("EXITO", false);
            resultado.put("MENSAJE", "Debe indicar el insumo y la cantidad.");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(resultado).build();
        }

        resultado = manager.IngresarExistencias(
                datos.get("ID_PRODUCTO").toString(),
                datos.get("CANTIDAD").toString()
        );

        if (Boolean.TRUE.equals(resultado.get("EXITO"))) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado).build();

    } catch (Exception e) {

        e.printStackTrace();

        resultado.put("EXITO", false);
        resultado.put("MENSAJE", "Error al ingresar existencias.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(resultado).build();
    }
}
}