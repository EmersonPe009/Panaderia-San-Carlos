
package BACKEND.manager;

import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import oracle.jdbc.OracleTypes;

public class ProduccionManagerPP {

    private final String SCHEMA = new Config().getDBSchema();

    private List<Map<String, Object>> ejecutarConsulta(
            String procedimiento,
            Integer id) throws Exception {

        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = new ConnectionsPool().conectar()) {

            String sql = id == null
                    ? "{call " + SCHEMA + "." + procedimiento + "(?)}"
                    : "{call " + SCHEMA + "." + procedimiento + "(?,?)}";

            try (CallableStatement call = conn.prepareCall(sql)) {

                int posicionCursor;

                if (id == null) {
                    posicionCursor = 1;
                } else {
                    call.setInt(1, id);
                    posicionCursor = 2;
                }

                call.registerOutParameter(
                        posicionCursor,
                        OracleTypes.CURSOR
                );

                call.execute();

                try (ResultSet rs = (ResultSet)
                        call.getObject(posicionCursor)) {

                    ResultSetMetaData meta = rs.getMetaData();
                    int columnas = meta.getColumnCount();

                    while (rs.next()) {

                        Map<String, Object> fila = new HashMap<>();

                        for (int i = 1; i <= columnas; i++) {
                            fila.put(
                                    meta.getColumnLabel(i),
                                    Objects.toString(
                                            rs.getObject(i), null
                                    )
                            );
                        }

                        lista.add(fila);
                    }
                }
            }
        }

        return lista;
    }

    public List<Map<String, Object>> CargarProducciones()
            throws Exception {

        return ejecutarConsulta(
                "PKG_PRODUCCION.CARGAR_PRODUCCIONES",
                null
        );
    }
    
public List<Map<String, Object>> CargarDemandaProduccion()
        throws Exception {

    return ejecutarConsulta(
            "PKG_PRODUCCION.CARGAR_DEMANDA_PRODUCCION",
            null
    );
}


    public List<Map<String, Object>> GetProduccion(int id)
            throws Exception {

        return ejecutarConsulta(
                "PKG_PRODUCCION.GET_PRODUCCION",
                id
        );
    }

    public List<Map<String, Object>> CargarDetallesProduccion(int id)
            throws Exception {

        return ejecutarConsulta(
                "PKG_PRODUCCION.CARGAR_DETALLES_PRODUCCION",
                id
        );
    }

    public Map<String, Object> InsProduccionCompleta(String json)
            throws Exception {

        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes proporcionar los datos de producción."
            );
        }

        Map<String, Object> resultado = new HashMap<>();

        try (Connection conn = new ConnectionsPool().conectar();
             CallableStatement call = conn.prepareCall(
                     "{call " + SCHEMA
                     + ".PKG_PRODUCCION.INS_PRODUCCION_COMPLETA(?,?,?)}"
             )) {

            call.setString(1, json);
            call.registerOutParameter(2, Types.NUMERIC);
            call.registerOutParameter(3, Types.VARCHAR);

            call.execute();

            Object id = call.getObject(2);
            String mensaje = call.getString(3);

            boolean exito = id != null;

            resultado.put("EXITO", exito);
            resultado.put("ID_PRODUCCION", id);
            resultado.put("MENSAJE", mensaje);
        }

        return resultado;
    }

    public Map<String, Object> FinalizarProduccion(int id)
            throws Exception {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de producción no es válido."
            );
        }

        Map<String, Object> resultado = new HashMap<>();

        try (Connection conn = new ConnectionsPool().conectar();
             CallableStatement call = conn.prepareCall(
                     "{call " + SCHEMA
                     + ".PKG_PRODUCCION.FINALIZAR_PRODUCCION(?,?,?)}"
             )) {

            call.setInt(1, id);
            call.registerOutParameter(2, Types.NUMERIC);
            call.registerOutParameter(3, Types.VARCHAR);

            call.execute();

            Object idSalida = call.getObject(2);
            String mensaje = call.getString(3);

            resultado.put("EXITO", idSalida != null);
            resultado.put("ID_PRODUCCION", idSalida);
            resultado.put("MENSAJE", mensaje);
        }

        return resultado;
    }
    
    
    
public Map<String, Object> IniciarProduccion(int id)
        throws Exception {

    if (id <= 0) {
        throw new IllegalArgumentException(
                "ID de producción inválido."
        );
    }

    Map<String, Object> resultado = new HashMap<>();

    try (Connection conn = new ConnectionsPool().conectar()) {

        // Consultar la fecha y bloquear la producción.
        // El procedimiento de Oracle hará la actualización.
        java.sql.Timestamp fecha;
        String estado;

        try (java.sql.PreparedStatement ps =
                conn.prepareStatement(
                    "SELECT FECHA_PRODUCCION, ESTADO "
                    + "FROM " + SCHEMA + ".PRODUCCION "
                    + "WHERE ID_PRODUCCION = ?"
                )) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Producción no encontrada."
                    );
                }

                fecha = rs.getTimestamp("FECHA_PRODUCCION");
                estado = rs.getString("ESTADO");
            }
        }

        if (!"PENDIENTE".equals(estado)) {
            throw new IllegalArgumentException(
                    "Solo se pueden iniciar producciones pendientes."
            );
        }

        try (CallableStatement call = conn.prepareCall(
                "{call " + SCHEMA
                + ".PKG_PRODUCCION.UPD_PRODUCCION(?,?,?,?,?)}"
        )) {

            call.setInt(1, id);
            call.setTimestamp(2, fecha);
            call.setString(3, "PROCESANDO");

            call.registerOutParameter(4, Types.NUMERIC);
            call.registerOutParameter(5, Types.VARCHAR);

            call.execute();

            Object idSalida = call.getObject(4);

            resultado.put("EXITO", idSalida != null);
            resultado.put("ID_PRODUCCION", idSalida);
            resultado.put("MENSAJE", call.getString(5));
        }
    }

    return resultado;
}

public Map<String, Object> GenerarProduccionConsolidada(
        String fecha, String turno) throws Exception {

    Map<String, Object> resultado = new HashMap<>();

    if (fecha == null || !fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
        throw new IllegalArgumentException(
                "La fecha debe tener formato YYYY-MM-DD."
        );
    }

    if (turno == null ||
            (!turno.equalsIgnoreCase("MAÑANA")
             && !turno.equalsIgnoreCase("TARDE"))) {
        throw new IllegalArgumentException(
                "El turno debe ser MAÑANA o TARDE."
        );
    }

    String sql = "{call " + SCHEMA
            + ".PKG_PRODUCCION.GENERAR_PRODUCCION_CONSOLIDADA"
            + "(?,?,?,?)}";

    try (Connection conn = new ConnectionsPool().conectar()) {

        conn.setAutoCommit(false);

        try (CallableStatement call = conn.prepareCall(sql)) {

            call.setDate(
                    1,
                    java.sql.Date.valueOf(fecha)
            );

            call.setString(
                    2,
                    turno.toUpperCase(java.util.Locale.ROOT)
            );

            call.registerOutParameter(3, java.sql.Types.NUMERIC);
            call.registerOutParameter(4, java.sql.Types.VARCHAR);

            call.execute();

            int idProduccion = call.getInt(3);
            boolean tieneId = !call.wasNull();
            String mensaje = call.getString(4);

            if (tieneId) {
                conn.commit();

                resultado.put("exito", true);
                resultado.put("id_produccion", idProduccion);
            } else {
                conn.rollback();

                resultado.put("exito", false);
                resultado.put("id_produccion", null);
            }

            resultado.put("mensaje", mensaje);
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }

    return resultado;
}

}

