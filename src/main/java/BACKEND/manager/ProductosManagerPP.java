/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BACKEND.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import oracle.jdbc.OracleTypes;

public class ProductosManagerPP {

    String SCHEMA = new Config().getDBSchema();

    public List<Map<String, Object>> CargarProductos() throws Exception {

        List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        // Prepare a PL/SQL call
        CallableStatement call =
                conn.prepareCall("call " + SCHEMA + ".PKG_PRODUCTOS.CARGAR_PRODUCTOS(?)");

        // Parametros
        call.registerOutParameter("P_CURSOR", OracleTypes.CURSOR);

        call.execute();

        ResultSet rset = (ResultSet) call.getObject("P_CURSOR");

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
    
        public List<Map<String, Object>> GetProducto(String id_producto) throws Exception {

    List<Map<String, Object>> salida = new ArrayList<Map<String, Object>>();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    // Prepare a PL/SQL call
    CallableStatement call =
            conn.prepareCall("call " + SCHEMA + ".PKG_PRODUCTOS.GET_PRODUCTO(?,?)");

    // Parametros
    call.setString("P_ID_PRODUCTO", id_producto);
    call.registerOutParameter("P_CURSOR", OracleTypes.CURSOR);

    call.execute();

    ResultSet rset = (ResultSet) call.getObject("P_CURSOR");

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
        
 
public Map<String, Object> InsProducto(
        String nombre,
        String codigo,
        String tipo_producto,
        String unidad_medida,
        String precio,
        String existencia,
        String existencia_minima) throws Exception {

    Map<String, Object> resultado = new HashMap<>();

    ConnectionsPool c = new ConnectionsPool();

    try (Connection conn = c.conectar();
         CallableStatement call = conn.prepareCall(
             "call " + SCHEMA
             + ".PKG_PRODUCTOS.INS_PRODUCTO(?,?,?,?,?,?,?,?,?)"
         )) {

        // Parámetros de entrada
        call.setString("P_NOMBRE", nombre);
        call.setString("P_CODIGO", codigo);
        call.setString("P_TIPO_PRODUCTO", tipo_producto);
        call.setString("P_UNIDAD_MEDIDA", unidad_medida);
        call.setString("P_PRECIO", precio);
        call.setString("P_EXISTENCIA", existencia);
        call.setString("P_EXISTENCIA_MINIMA", existencia_minima);

        // Parámetros de salida
        call.registerOutParameter(
            "P_ID_SALIDA",
            java.sql.Types.NUMERIC
        );

        call.registerOutParameter(
            "P_MENSAJE",
            java.sql.Types.VARCHAR
        );

        call.execute();

        // Recuperar los resultados de Oracle
        java.math.BigDecimal idProducto =
            call.getBigDecimal("P_ID_SALIDA");

        String mensaje =
            call.getString("P_MENSAJE");

        boolean exito =
            idProducto != null &&
            idProducto.signum() > 0 &&
            mensaje != null &&
            mensaje.toLowerCase().contains(
                "ingresado correctamente"
            );

        resultado.put("EXITO", exito);
        resultado.put("MENSAJE", mensaje);
        resultado.put("ID_PRODUCTO", idProducto);
    }

    return resultado;
}



public String UpdProducto(
        String id_producto,
        String nombre,
        String codigo,
        String tipo_producto,
        String unidad_medida,
        String precio,
        String existencia,
        String existencia_minima,
        String estado) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call =
            conn.prepareCall(
                    "call " + SCHEMA + ".PKG_PRODUCTOS.UPD_PRODUCTO(?,?,?,?,?,?,?,?,?,?,?)");

    // Parametros de entrada
    call.setString("P_ID_PRODUCTO", id_producto);
    call.setString("P_NOMBRE", nombre);
    call.setString("P_CODIGO", codigo);
    call.setString("P_TIPO_PRODUCTO", tipo_producto);
    call.setString("P_UNIDAD_MEDIDA", unidad_medida);
    call.setString("P_PRECIO", precio);
    call.setString("P_EXISTENCIA", existencia);
    call.setString("P_EXISTENCIA_MINIMA", existencia_minima);
    call.setString("P_ESTADO", estado);

    // Parametros de salida
    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}








public Map<String, Object> GetEstadisticas()
        throws Exception {

    Map<String, Object> estadisticas = new HashMap<>();

    ConnectionsPool c = new ConnectionsPool();

    try (
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
            "{call " + SCHEMA
            + ".PKG_PRODUCTOS.GET_ESTADISTICAS(?)}"
        )
    ) {

        call.registerOutParameter(1, OracleTypes.CURSOR);

        call.execute();

        try (
            ResultSet rset =
                (ResultSet) call.getObject(1)
        ) {

            if (rset.next()) {

                estadisticas.put(
                    "TOTAL_PRODUCTOS",
                    rset.getInt("TOTAL_PRODUCTOS")
                );

                estadisticas.put(
                    "CON_EXISTENCIA",
                    rset.getInt("CON_EXISTENCIA")
                );

                estadisticas.put(
                    "EXISTENCIA_BAJA",
                    rset.getInt("EXISTENCIA_BAJA")
                );

                estadisticas.put(
                    "VALOR_INVENTARIO",
                    rset.getBigDecimal("VALOR_INVENTARIO")
                );

            }
        }
    }

    return estadisticas;
}


public List<Map<String, Object>> GetTopExistencias()
        throws Exception {

    List<Map<String, Object>> productos = new ArrayList<>();

    ConnectionsPool c = new ConnectionsPool();

    try (Connection conn = c.conectar();
         CallableStatement call = conn.prepareCall(
             "{call " + SCHEMA
             + ".PKG_PRODUCTOS.GET_TOP_EXISTENCIAS(?)}"
         )) {

        call.registerOutParameter(1, OracleTypes.CURSOR);
        call.execute();

        try (ResultSet rs = (ResultSet) call.getObject(1)) {

            while (rs.next()) {

                Map<String, Object> producto = new HashMap<>();

                producto.put(
                    "ID_PRODUCTO",
                    rs.getInt("ID_PRODUCTO")
                );

                producto.put(
                    "NOMBRE",
                    rs.getString("NOMBRE")
                );

                producto.put(
                    "EXISTENCIA",
                    rs.getBigDecimal("EXISTENCIA")
                );

                productos.add(producto);
            }
        }
    }

    return productos;
}



public List<Map<String, Object>> GetDistribucionTipos()
        throws Exception {

    List<Map<String, Object>> distribucion = new ArrayList<>();

    ConnectionsPool c = new ConnectionsPool();

    try (Connection conn = c.conectar();
         CallableStatement call = conn.prepareCall(
             "{call " + SCHEMA
             + ".PKG_PRODUCTOS.GET_DISTRIBUCION_TIPOS(?)}"
         )) {

        call.registerOutParameter(1, OracleTypes.CURSOR);
        call.execute();

        try (ResultSet rs = (ResultSet) call.getObject(1)) {

            while (rs.next()) {

                Map<String, Object> tipo = new HashMap<>();

                tipo.put(
                    "TIPO",
                    rs.getString("TIPO")
                );

                tipo.put(
                    "CANTIDAD",
                    rs.getInt("CANTIDAD")
                );

                distribucion.add(tipo);
            }
        }
    }

    return distribucion;
}

public Map<String, Object> IngresarExistencias(
        String id_producto,
        String cantidad) throws Exception {

    Map<String, Object> resultado = new HashMap<>();

    ConnectionsPool c = new ConnectionsPool();

    try (Connection conn = c.conectar();
         CallableStatement call = conn.prepareCall(
             "{call " + SCHEMA
             + ".PKG_PRODUCTOS.INGRESAR_EXISTENCIAS(?,?,?,?)}"
         )) {

        // Parámetros de entrada
        call.setString(1, id_producto);
        call.setString(2, cantidad);

        // Parámetros de salida
        call.registerOutParameter(
            3, java.sql.Types.NUMERIC
        );

        call.registerOutParameter(
            4, java.sql.Types.VARCHAR
        );

        // Ejecutar procedimiento Oracle
        call.execute();

        java.math.BigDecimal idSalida =
                call.getBigDecimal(3);

        String mensaje = call.getString(4);

        boolean exito =
                idSalida != null
                && idSalida.signum() > 0
                && mensaje != null
                && mensaje.equalsIgnoreCase(
                    "Existencias ingresadas correctamente"
                );

        resultado.put("EXITO", exito);
        resultado.put("MENSAJE", mensaje);
        resultado.put("ID_PRODUCTO", idSalida);

    }

    return resultado;
}

}