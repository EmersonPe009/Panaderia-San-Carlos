
package BACKEND.manager;

import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import oracle.jdbc.OracleTypes;

public class EstadisticasManagerPP {

    private final String SCHEMA =
            new Config().getDBSchema();

    // =========================================
    // EJECUTAR CONSULTAS DE ESTADÍSTICAS
    // =========================================
    private List<Map<String, Object>> ejecutarConsulta(
            String procedimiento) throws Exception {

        List<Map<String, Object>> lista =
                new ArrayList<>();

        String sql = "{call " + SCHEMA
                + ".PKG_ESTADISTICAS."
                + procedimiento + "(?)}";

        try (Connection conn =
                    new ConnectionsPool().conectar();

             CallableStatement call =
                    conn.prepareCall(sql)) {

            call.registerOutParameter(
                    1, OracleTypes.CURSOR
            );

            call.execute();

            try (ResultSet rs =
                    (ResultSet) call.getObject(1)) {

                ResultSetMetaData meta =
                        rs.getMetaData();

                while (rs.next()) {

                    Map<String, Object> fila =
                            new LinkedHashMap<>();

                    for (int i = 1;
                            i <= meta.getColumnCount();
                            i++) {

                        fila.put(
                                meta.getColumnLabel(i),
                                Objects.toString(
                                        rs.getObject(i),
                                        null
                                )
                        );
                    }

                    lista.add(fila);
                }
            }
        }

        return lista;
    }

    // =========================================
    // RESUMEN GENERAL
    // =========================================
    public List<Map<String, Object>> ResumenGeneral()
            throws Exception {

        return ejecutarConsulta(
                "RESUMEN_GENERAL"
        );
    }

    // =========================================
    // RANKING DE SUCURSALES
    // =========================================
    public List<Map<String, Object>> RankingSucursales()
            throws Exception {

        return ejecutarConsulta(
                "RANKING_SUCURSALES"
        );
    }

    // =========================================
    // INVENTARIO BAJO
    // =========================================
    public List<Map<String, Object>> InventarioBajo()
            throws Exception {

        return ejecutarConsulta(
                "INVENTARIO_BAJO"
        );
    }
    
 // =========================================
// RESUMEN DE VENTAS REALES
// =========================================
public List<Map<String, Object>> ResumenVentas()
        throws Exception {

    return ejecutarConsulta("RESUMEN_VENTAS");
}
}
