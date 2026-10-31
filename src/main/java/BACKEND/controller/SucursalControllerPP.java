
package BACKEND.controller;

import BACKEND.manager.SucursalesManagerPP;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("Sucursales")
public class SucursalControllerPP {

    SucursalesManagerPP manager = new SucursalesManagerPP();

    @GET
    @Path("CargarSucursales")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> CargarSucursales()
            throws Exception {

        return manager.CargarSucursales();
    }

    @GET
    @Path("GetSucursal")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> GetSucursal(
            @QueryParam("id_sucursal") String id_sucursal)
            throws Exception {

        return manager.GetSucursal(id_sucursal);
    }
    
        
    @javax.ws.rs.POST
    @Path("InsSucursal")
    @javax.ws.rs.Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String InsSucursal(Map<String, Object> sucursal)
        throws Exception {

    return manager.InsSucursal(
        sucursal.get("nombre").toString(),
        sucursal.get("direccion").toString(),
        sucursal.get("telefono").toString()
    );
}

    @javax.ws.rs.PUT
    @Path("UpdSucursal")
    @javax.ws.rs.Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String UpdSucursal(Map<String, Object> sucursal)
        throws Exception {

    return manager.UpdSucursal(
        sucursal.get("id_sucursal").toString(),
        sucursal.get("nombre").toString(),
        sucursal.get("direccion").toString(),
        sucursal.get("telefono").toString(),
        sucursal.get("estado").toString()
    );
}

}
