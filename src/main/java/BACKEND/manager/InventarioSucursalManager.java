
package BACKEND.manager;

import BACKEND.utils.ConnectionsPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleTypes;

public class InventarioSucursalManager {

    public List<Map<String, Object>> cargarInventario(
            int idSucursal) throws Exception {

        List<Map<String, Object>> inventario =
                new ArrayList<>();

        String sql =
                "{call PKG_INVENTARIO_SUCURSAL.CARGAR_INVENTARIO(?, ?)}";

        ConnectionsPool pool = new ConnectionsPool();

        try (Connection conn = pool.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idSucursal);
            stmt.registerOutParameter(
                    2, OracleTypes.CURSOR
            );

            stmt.execute();

            try (ResultSet rs =
                    (ResultSet) stmt.getObject(2)) {

                while (rs.next()) {

                    Map<String, Object> producto =
                            new HashMap<>();

                    producto.put(
                            "ID_PRODUCTO",
                            rs.getInt("ID_PRODUCTO")
                    );

                    producto.put(
                            "NOMBRE",
                            rs.getString("NOMBRE")
                    );

                    producto.put(
                            "CODIGO",
                            rs.getString("CODIGO")
                    );

                    producto.put(
                            "UNIDAD_MEDIDA",
                            rs.getString("UNIDAD_MEDIDA")
                    );

                    producto.put(
                            "EXISTENCIA",
                            rs.getBigDecimal("EXISTENCIA")
                    );

                    producto.put(
                            "FECHA_ACTUALIZACION",
                            rs.getTimestamp("FECHA_ACTUALIZACION")
                    );

                    inventario.add(producto);
                }
            }
        }

        return inventario;
    }
}
