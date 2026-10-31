package BACKEND.manager;

import BACKEND.model.DetalleFactura;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import BACKEND.model.Ejemplo;
import BACKEND.model.Factura;
import BACKEND.model.Municipio;
import BACKEND.model.Sucursal;
import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import BACKEND.utils.jsonResult;
import oracle.jdbc.OracleTypes;

public class EjemploManager {
	String SCHEMA = new Config().getDBSchema();
	
	public List<Map<String, Object>> ObtenerCatalogoEjemplo() throws Exception{
        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();
       
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
               
         // Prepare a PL/SQL call
        CallableStatement call =
        conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLOS.OBTENER_DEPARTAMENTOS(?)");
        
        // Parametros
        call.registerOutParameter ("p_cursor", OracleTypes.CURSOR);
        call.execute ();
        ResultSet rset = (ResultSet)call.getObject ("p_cursor");
        
        // Dump the cursor
        ResultSetMetaData meta = rset.getMetaData();
   
	    while (rset.next()) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= meta.getColumnCount(); i++) {
	        String key = meta.getColumnName(i).toString();
	        String value = Objects.toString(rset.getString(key), "");
	        map.put(key, value);
	    }     
	    salida.add(map);
	    }
    // Close all the resources
    rset.close();
    call.close();
    conn.close();
   
    return salida;
	}
	
        public List<Map<String, Object>> ObtenerSucursales() throws Exception{
        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();
       
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
               
         // Prepare a PL/SQL call
        CallableStatement call =
        conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLOS.OBTENER_SUCURSALES(?)");
        
        // Parametros
        call.registerOutParameter ("p_cursor", OracleTypes.CURSOR);
        call.execute ();
        ResultSet rset = (ResultSet)call.getObject ("p_cursor");
        
        // Dump the cursor
        ResultSetMetaData meta = rset.getMetaData();
   
	    while (rset.next()) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= meta.getColumnCount(); i++) {
	        String key = meta.getColumnName(i).toString();
	        String value = Objects.toString(rset.getString(key), "");
	        map.put(key, value);
	    }     
	    salida.add(map);
	    }
    // Close all the resources
    rset.close();
    call.close();
    conn.close();
   
    return salida;
	}
        
	public jsonResult AgregarEjemplo(Ejemplo item) throws Exception{
		jsonResult salida = new jsonResult();
		
		ConnectionsPool c=new ConnectionsPool();
		Connection conn=c.conectar();
		 // Prepare a PL/SQL call
	    CallableStatement call =
	    conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLO.PROC_INS_TT_EJEMPLO_TRANSACCIO(?,?,?,?,?)");

	    // Parametros
	    call.setString("p_id_ejemplo_catalogo", item.ID_EJEMPLO_TRANSACCIONAL);
	    call.setString("p_observaciones", item.OBSERVACIONES);
	    call.setString("p_id_estado", item.ID_ESTADO);
	    call.registerOutParameter ("p_id_salida", OracleTypes.NUMBER);
	    call.registerOutParameter ("p_msj", OracleTypes.VARCHAR);
	    call.execute ();
	  
	    int  id = call.getInt("p_id_salida");
	    String  msj = call.getString("p_msj");
	    salida.id=id;
	    salida.msj=msj;
	    if(salida.id!=-1){
	    	salida.result="OK";
	    }
	    
	    // Close all the resources
	    call.close();
	    conn.close();
	    
	    return salida;
	}
	
        
        public jsonResult AgregarSucursal(Sucursal item) throws Exception{
		jsonResult salida = new jsonResult();
		
		ConnectionsPool c=new ConnectionsPool();
		Connection conn=c.conectar();
		 // Prepare a PL/SQL call
	    CallableStatement call =
	    conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLOs.AGREGAR_SUCURSAL(?,?,?,?,?,?)");

	    // Parametros
	    call.setString("p_id_sucursal", item.ID_SUCURSAL);
	    call.setString("p_nombre", item.NOMBRE);
	    call.setString("p_direccion", item.DIRECCION);
             call.setString("p_estado", item.ESTADO);
	    call.registerOutParameter ("p_id_salida", OracleTypes.NUMBER);
	    call.registerOutParameter ("p_msj", OracleTypes.VARCHAR);
	    call.execute ();
	  
	    int  id = call.getInt("p_id_salida");
	    String  msj = call.getString("p_msj");
	    salida.id=id;
	    salida.msj=msj;
	    if(salida.id!=-1){
	    	salida.result="OK";
	    }
	    
	    // Close all the resources
	    call.close();
	    conn.close();
	    
	    return salida;
	}
	
	/*public jsonResult AgregarEjemploTransaccional(Departamento item) throws Exception {
        jsonResult salida = new jsonResult();
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
        conn.setAutoCommit(false);
        
        try {
			
        // Prepare a PL/SQL call
        CallableStatement call = conn
                       .prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.AGREGAR_DEPTO(?,?,?)");
        // Parametros
        call.setString("p_nombre", item.NOMBRE);
        call.registerOutParameter("p_id_salida", OracleTypes.NUMBER);
        call.registerOutParameter("p_msj", OracleTypes.VARCHAR);
        call.execute();
       
        int id = call.getInt("p_id_salida");
        salida.id = id;
        salida.result = "ERROR";
        salida.msj = call.getString("p_msj");
        if (salida.id != -1) {
                salida.result = "OK";
        }
        // Close all the resources
        call.close();
               
        for (Municipio p:item.MUNICIPIOS)
        {
        	jsonResult elemento_salida = new jsonResult();
        	p.ID_UBICACION_GEOGRARAFICA_PADRE = String.valueOf(id);
        	elemento_salida = AgregarDetalleTransaccional(p,conn);
        	if (elemento_salida.id == -1) 
                {
        		throw new Exception(elemento_salida.msj); 
        	}
        }       
        
        conn.commit();
        conn.close();
        

		} catch (Exception e) {
			//Manejo de excepción
			conn.rollback();
	        conn.close();
	        salida.id = -1;
	        salida.msj = e.getMessage();
	        salida.result = "ERROR";
		}       
        return salida;
		}*/
		

public jsonResult AgregarDetalleTransaccional(Municipio item, Connection conn) throws Exception {
    jsonResult salida = new jsonResult();
    
    try {
		
    // Prepare a PL/SQL call
    CallableStatement call = conn
                   .prepareCall("call " + SCHEMA + ".PKG_EJEMPLOS.AGREGAR_MUNICIPIO(?,?,?,?)");
    // Parametros
    call.setString("p_id_depto", item.ID_UBICACION_GEOGRARAFICA_PADRE);
    call.setString("p_nombre", item.NOMBRE);
    call.registerOutParameter("p_id_salida", OracleTypes.NUMBER);
    call.registerOutParameter("p_msj", OracleTypes.VARCHAR);
    call.execute();
   
    int id = call.getInt("p_id_salida");
    salida.id = id;
    salida.result = "ERROR";
    salida.msj = call.getString("p_msj");
    if (salida.id != -1) {
            salida.result = "OK";
    }else {
    	throw new Exception(salida.msj); 
    }
    
    // Close all the resources
    call.close();
    
	} catch (Exception e) {
		// Manejo de excepción
		salida.id = -1;
		salida.msj = e.getMessage();
	}
    return salida;
	}


	public Map<String, Object> ObtenerEjemplo(String id_ejemplo) throws Exception{
        Map<String, Object> salida = new LinkedHashMap<String, Object>();
       
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
               
         // Prepare a PL/SQL call
        CallableStatement call =
        conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLO.PROC_OBTENER_EJEMPLO(?,?)");
        
        // Parametros
        call.setString("p_id_ejemplo_transaccional", id_ejemplo);
        call.registerOutParameter ("p_cur_dataset", OracleTypes.CURSOR);
        call.execute ();
        ResultSet rset = (ResultSet)call.getObject ("p_cur_dataset");
        
        // Dump the cursor
        ResultSetMetaData meta = rset.getMetaData();
   
	    while (rset.next()) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= meta.getColumnCount(); i++) {
	        String key = meta.getColumnName(i).toString();
	        String value = Objects.toString(rset.getString(key), "");
	        map.put(key, value);
	    }
	    salida=map;
	    }
    // Close all the resources
    rset.close();
    call.close();
    conn.close();
   
    return salida;
	}
        
     
       public List<Map<String, Object>> ObtenerMunicipiosDepto(String id_ubicacion_geografica) throws Exception{
       List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();
       
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
               
         // Prepare a PL/SQL call
        CallableStatement call =
        conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLOS.OBTENER_MUNICIPIOS_DEPTO(?,?)");
        
        // Parametros
        call.setString("p_id_ubicacion_geografica", id_ubicacion_geografica);
        call.registerOutParameter ("p_cursor", OracleTypes.CURSOR);
        call.execute ();
        ResultSet rset = (ResultSet)call.getObject ("p_cursor");
        
        // Dump the cursor
         ResultSetMetaData meta = rset.getMetaData();
   
	    while (rset.next()) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= meta.getColumnCount(); i++) {
	        String key = meta.getColumnName(i).toString();
	        String value = Objects.toString(rset.getString(key), "");
	        map.put(key, value);
	    }     
	    salida.add(map);
	    }
    // Close all the resources
    rset.close();
    call.close();
    conn.close();
   
    return salida;
	} 
        /*public Map<String, Object> ObtenerMunicipiosDepto(String id_ubicacion_geografica ,Connection conn) throws Exception{
        Map<String, Object> salida = new LinkedHashMap<String, Object>();
       
        // Prepare a PL/SQL call
        CallableStatement call = conn.prepareCall ("call "+SCHEMA+".PKG_EJEMPLO.PROC_OBTENER_MAESTRO_DETALLE(?,?)");
        
        // Parametros
        call.setString("p_id_escrito", id_escrito);
        call.registerOutParameter ("p_cur_dataset", OracleTypes.CURSOR);
        call.execute ();
        ResultSet rset = (ResultSet)call.getObject ("p_cur_dataset");
        
        // Dump the cursor
        ResultSetMetaData meta = rset.getMetaData();
   
	    while (rset.next()) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (int i = 1; i <= meta.getColumnCount(); i++) {
	        String key = meta.getColumnName(i).toString();
	        String value = Objects.toString(rset.getString(key), "");                
	        map.put(key, value);
                map.put("DETALLE", ArchivosManager.ObtenerDetalle("1",(String) map.get("ID_maestro"), conn));
	    }
	    salida=map;
	    }
    // Close all the resources
    call.close();
    return salida;
	} 
	*/
       
       
       public jsonResult AgregarFacturaDetalle(Factura item) throws Exception {
        jsonResult salida = new jsonResult();
        ConnectionsPool c=new ConnectionsPool();
        Connection conn=c.conectar();
        conn.setAutoCommit(false);
        
        try {
			
        // Prepare a PL/SQL call
        CallableStatement call = conn
                       .prepareCall("call " + SCHEMA + ".PKG_FACTURA.INS_Factura(?,?,?)");
        // Parametros
        call.setString("ID_CLIENTE", item.ID_CLIENTE);
        call.registerOutParameter("ID_SALIDA", OracleTypes.NUMBER);
        call.registerOutParameter("MENSAJE", OracleTypes.VARCHAR);
        call.execute();
       
        int id = call.getInt("ID_SALIDA");
        salida.id = id;
        salida.result = "ERROR";
        salida.msj = call.getString("MENSAJE");
        if (salida.id != -1) //Voy a insertar el detalle
        {
            for (DetalleFactura p:item.DETALLE)
        {
        	jsonResult elemento_salida = new jsonResult();
        	p.ID_FACTURA= String.valueOf(id);
        	elemento_salida = AgregarDetalleTransaccional(p,conn);
        	if (elemento_salida.id == -1) 
                {
        		throw new Exception(elemento_salida.msj); 
        	}
        } 
                salida.result = "OK";
        }
        // Close all the resources
        call.close();
               
      /*  for (Municipio p:item.MUNICIPIOS)
        {
        	jsonResult elemento_salida = new jsonResult();
        	p.ID_UBICACION_GEOGRARAFICA_PADRE = String.valueOf(id);
        	elemento_salida = AgregarDetalleTransaccional(p,conn);
        	if (elemento_salida.id == -1) 
                {
        		throw new Exception(elemento_salida.msj); 
        	}
        }       
        */
        conn.commit();
        conn.close();
        

		} catch (Exception e) {
			//Manejo de excepción
			conn.rollback();
	        conn.close();
	        salida.id = -1;
	        salida.msj = e.getMessage();
	        salida.result = "ERROR";
		}       
        return salida;
		}
       
       
       public jsonResult AgregarDetalleTransaccional(DetalleFactura item, Connection conn) throws Exception {
    jsonResult salida = new jsonResult();
    
    try {
		
    // Prepare a PL/SQL call
    CallableStatement call = conn
                   .prepareCall("call " + SCHEMA + ".PKG_FACTURA.INS_DETALLE_FACTURA(?,?,?,?,?,?)");
    // Parametros
    call.setString("ID_FACTURA", item.ID_FACTURA);
    call.setString("ID_PRODUCTO", item.ID_PRODUCTO);
    call.setString("CANTIDAD", item.CANTIDAD);
    call.setString("SUBTOTAL", item.SUBTOTAL);
    call.registerOutParameter("ID_SALIDA", OracleTypes.NUMBER);
    call.registerOutParameter("MENSAJE", OracleTypes.VARCHAR);
    call.execute();
   
    int id = call.getInt("ID_SALIDA");
    salida.id = id;
    salida.result = "ERROR";
    salida.msj = call.getString("MENSAJE");
    if (salida.id != -1) {
            salida.result = "OK";
    }else {
    	throw new Exception(salida.msj); 
    }
    
    // Close all the resources
    call.close();
    
	} catch (Exception e) {
		// Manejo de excepción
		salida.id = -1;
		salida.msj = e.getMessage();
	}
    return salida;
	}
       
       
       
public Map<String, Object> ObtenerFacturaDetalle(
        Factura item) throws Exception {

    Map<String, Object> salida =
        new LinkedHashMap<String, Object>();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    conn.setAutoCommit(false);

    try {

        Map<String, Object> encabezado =
            ObtenerFactura(item, conn);

        if (encabezado.isEmpty()) {

            throw new Exception(
                "No se pudo recuperar ningun encabezado de factura"
            );
        }

        List<Map<String, Object>> detalle =
            ObtenerDetalleFactura(item, conn);

        salida.put("encabezado", encabezado);
        salida.put("detalle", detalle);

        conn.commit();

    } catch (Exception e) {

        conn.rollback();
        throw e;

    } finally {

        conn.close();
    }

    return salida;
}

public Map<String, Object> ObtenerFactura(
        Factura item,
        Connection conn) throws Exception {

    Map<String, Object> salida = new LinkedHashMap<String, Object>();

    CallableStatement call = conn.prepareCall(
        "call " + SCHEMA +
        ".PKG_FACTURA.GET_ENCABEZADO_FACTURA(?,?,?)"
    );

    // Parametros
    call.setString("p_id_cliente", item.ID_CLIENTE);
    call.setString("p_id_factura", item.ID_FACTURA);

    call.registerOutParameter(
        "p_cur_dataset",
        OracleTypes.CURSOR
    );

    call.execute();

    ResultSet rset =
        (ResultSet) call.getObject("p_cur_dataset");

    ResultSetMetaData meta = rset.getMetaData();

    while (rset.next()) {

        Map<String, Object> map =
            new LinkedHashMap<String, Object>();

        for (int i = 1;
             i <= meta.getColumnCount();
             i++) {

            String key = meta.getColumnName(i);

            String value = Objects.toString(
                rset.getString(key),
                ""
            );

            map.put(key, value);
        }

        salida = map;
    }

    rset.close();
    call.close();

    return salida;
}


public List<Map<String, Object>> ObtenerDetalleFactura(
        Factura item,
        Connection conn) throws Exception {

    List<Map<String, Object>> salida =
        new ArrayList<Map<String, Object>>();

    CallableStatement call = conn.prepareCall(
        "call " + SCHEMA +
        ".PKG_FACTURA.GET_DETALLE_FACTURA(?,?)"
    );

    call.setString(
        "p_id_factura",
        item.ID_FACTURA
    );

    call.registerOutParameter(
        "p_cur_dataset",
        OracleTypes.CURSOR
    );

    call.execute();

    ResultSet rset =
        (ResultSet) call.getObject("p_cur_dataset");

    ResultSetMetaData meta =
        rset.getMetaData();

    while (rset.next()) {

        Map<String, Object> map =
            new LinkedHashMap<String, Object>();

        for (int i = 1;
             i <= meta.getColumnCount();
             i++) {

            String key = meta.getColumnName(i);

            String value = Objects.toString(
                rset.getString(key),
                ""
            );

            map.put(key, value);
        }

        salida.add(map);
    }

    rset.close();
    call.close();

    return salida;
}
















public Map<String, Object> ObtenerOrden(
        Factura item,
        Connection conn) throws Exception {

    Map<String, Object> salida = new LinkedHashMap<String, Object>();

    CallableStatement call = conn.prepareCall(
        "call " + SCHEMA +
        ".PKG_FACTURA.GET_ENCABEZADO_ORDEN_PAR(?,?,?)"
    );

    // Parametros
    call.setString("p_id_cliente", item.ID_CLIENTE);
    call.setString("p_id_factura", item.ID_FACTURA);

    call.registerOutParameter(
        "p_cur_dataset",
        OracleTypes.CURSOR
    );

    call.execute();

    ResultSet rset =
        (ResultSet) call.getObject("p_cur_dataset");

    ResultSetMetaData meta = rset.getMetaData();

    while (rset.next()) {

        Map<String, Object> map =
            new LinkedHashMap<String, Object>();

        for (int i = 1;
             i <= meta.getColumnCount();
             i++) {

            String key = meta.getColumnName(i);

            String value = Objects.toString(
                rset.getString(key),
                ""
            );

            map.put(key, value);
        }

        salida = map;
    }

    rset.close();
    call.close();

    return salida;
}



public Map<String, Object> ObtenerOrdenDetalle(
        Factura item) throws Exception {

    Map<String, Object> salida =
        new LinkedHashMap<String, Object>();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    conn.setAutoCommit(false);

    try {

        Map<String, Object> encabezado =
            ObtenerOrden(item, conn);

        if (encabezado.isEmpty()) {

            throw new Exception(
                "No se pudo recuperar ningun encabezado de factura"
            );
        }

        List<Map<String, Object>> detalle =
            ObtenerDetalleFactura(item, conn);

        salida.put("encabezado", encabezado);
        salida.put("detalle", detalle);

        conn.commit();

    } catch (Exception e) {

        conn.rollback();
        throw e;

    } finally {

        conn.close();
    }

    return salida;
}



}








