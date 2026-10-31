/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BACKEND.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import BACKEND.manager.FacturaManager;
import BACKEND.model.Factura;
import BACKEND.utils.jsonResult;

import java.util.List;

import javax.ws.rs.GET;

import BACKEND.model.Cliente;
import BACKEND.model.Producto;

@Path("Factura")
public class FacturaController {

    FacturaManager manager = new FacturaManager();

    @POST
    @Path("/AgregarFactura")
    @Consumes("application/json")
    @Produces("application/json")
    public Response AgregarFactura(@Context HttpServletRequest req, Factura item) {
        try {
            //item.IP = req.getRemoteAddr();
System.out.println(req.getRemoteAddr());
            return Response.ok(manager.AgregarFactura(item)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(
                new jsonResult(-1, "Error", e.getMessage())
            ).build();
        }
    }
    
    
    @GET
@Path("/CargarClientes")
@Produces("application/json")
public Response CargarClientes() {

    try {

        List<Cliente> lista =
            manager.CargarClientes();

        return Response.ok(lista).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.ok(
            new jsonResult(-1, "ERROR", e.getMessage())
        ).build();
    }
}



@GET
@Path("/CargarProductos")
@Produces("application/json")
public Response CargarProductos() {

    try {

        List<Producto> lista =
            manager.CargarProductos();

        return Response.ok(lista).build();

    } catch (Exception e) {

        e.printStackTrace();

        return Response.ok(
            new jsonResult(-1, "ERROR", e.getMessage())
        ).build();
    }
}
    
    
}
