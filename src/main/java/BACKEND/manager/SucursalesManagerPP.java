
package BACKEND.manager;

import BACKEND.utils.Config;
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
import oracle.jdbc.OracleTypes;

public class SucursalesManagerPP {

    String SCHEMA = new Config().getDBSchema();

    public List<Map<String, Object>> CargarSucursales() throws Exception {

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
                "call " + SCHEMA + ".PKG_SUCURSALES.CARGAR_SUCURSALES(?)");

        call.registerOutParameter(1, OracleTypes.CURSOR);
        call.execute();

        ResultSet rs = (ResultSet) call.getObject(1);
        List<Map<String, Object>> lista = new ArrayList<>();

        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> item = new HashMap<>();

            for (int i = 1; i <= columnas; i++) {
                item.put(
                    meta.getColumnLabel(i),
                    Objects.toString(rs.getObject(i), null)
                );
            }

            lista.add(item);
        }

        rs.close();
        call.close();
        conn.close();

        return lista;
    }

    public List<Map<String, Object>> GetSucursal(
            String id_sucursal) throws Exception {

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
                "call " + SCHEMA + ".PKG_SUCURSALES.GET_SUCURSAL(?,?)");

        call.setString(1, id_sucursal);
        call.registerOutParameter(2, OracleTypes.CURSOR);
        call.execute();

        ResultSet rs = (ResultSet) call.getObject(2);
        List<Map<String, Object>> lista = new ArrayList<>();

        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> item = new HashMap<>();

            for (int i = 1; i <= columnas; i++) {
                item.put(
                    meta.getColumnLabel(i),
                    Objects.toString(rs.getObject(i), null)
                );
            }

            lista.add(item);
        }

        rs.close();
        call.close();
        conn.close();

        return lista;
    }
    
    
    
public String InsSucursal(
        String nombre,
        String direccion,
        String telefono) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA + ".PKG_SUCURSALES.INS_SUCURSAL(?,?,?,?,?)");

    call.setString("P_NOMBRE", nombre);
    call.setString("P_DIRECCION", direccion);
    call.setString("P_TELEFONO", telefono);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

public String UpdSucursal(
        String id_sucursal,
        String nombre,
        String direccion,
        String telefono,
        String estado) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA + ".PKG_SUCURSALES.UPD_SUCURSAL(?,?,?,?,?,?,?)");

    call.setString("P_ID_SUCURSAL", id_sucursal);
    call.setString("P_NOMBRE", nombre);
    call.setString("P_DIRECCION", direccion);
    call.setString("P_TELEFONO", telefono);
    call.setString("P_ESTADO", estado);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

}
