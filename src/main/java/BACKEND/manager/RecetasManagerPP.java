/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

public class RecetasManagerPP {

    String SCHEMA = new Config().getDBSchema();

    public List<Map<String, Object>> CargarRecetas() throws Exception {

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
                "call " + SCHEMA + ".PKG_RECETAS.CARGAR_RECETAS(?)");

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

    public List<Map<String, Object>> GetReceta(
            String id_receta) throws Exception {

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
                "call " + SCHEMA + ".PKG_RECETAS.GET_RECETA(?,?)");

        call.setString("P_ID_RECETA", id_receta);
        call.registerOutParameter("P_CURSOR", OracleTypes.CURSOR);
        call.execute();

        ResultSet rs = (ResultSet) call.getObject("P_CURSOR");
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

    public List<Map<String, Object>> CargarDetallesReceta(
            String id_receta) throws Exception {

        ConnectionsPool c = new ConnectionsPool();
        Connection conn = c.conectar();

        CallableStatement call = conn.prepareCall(
                "call " + SCHEMA
                + ".PKG_RECETAS.CARGAR_DETALLES_RECETA(?,?)");

        call.setString("P_ID_RECETA", id_receta);
        call.registerOutParameter("P_CURSOR", OracleTypes.CURSOR);
        call.execute();

        ResultSet rs = (ResultSet) call.getObject("P_CURSOR");
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
    
    
public String InsReceta(
        String id_producto,
        String nombre,
        String rendimiento) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA + ".PKG_RECETAS.INS_RECETA(?,?,?,?,?)");

    call.setString("P_ID_PRODUCTO", id_producto);
    call.setString("P_NOMBRE", nombre);
    call.setString("P_RENDIMIENTO", rendimiento);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

public String UpdReceta(
        String id_receta,
        String id_producto,
        String nombre,
        String rendimiento,
        String estado) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA + ".PKG_RECETAS.UPD_RECETA(?,?,?,?,?,?,?)");

    call.setString("P_ID_RECETA", id_receta);
    call.setString("P_ID_PRODUCTO", id_producto);
    call.setString("P_NOMBRE", nombre);
    call.setString("P_RENDIMIENTO", rendimiento);
    call.setString("P_ESTADO", estado);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

public String InsDetalleReceta(
        String id_receta,
        String id_producto,
        String cantidad) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA
            + ".PKG_RECETAS.INS_DETALLE_RECETA(?,?,?,?,?)");

    call.setString("P_ID_RECETA", id_receta);
    call.setString("P_ID_PRODUCTO", id_producto);
    call.setString("P_CANTIDAD", cantidad);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

public String UpdDetalleReceta(
        String id_detalle_receta,
        String id_producto,
        String cantidad) throws Exception {

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    CallableStatement call = conn.prepareCall(
            "call " + SCHEMA
            + ".PKG_RECETAS.UPD_DETALLE_RECETA(?,?,?,?,?)");

    call.setString("P_ID_DETALLE_RECETA", id_detalle_receta);
    call.setString("P_ID_PRODUCTO", id_producto);
    call.setString("P_CANTIDAD", cantidad);

    call.registerOutParameter("P_ID_SALIDA", java.sql.Types.NUMERIC);
    call.registerOutParameter("P_MENSAJE", java.sql.Types.VARCHAR);

    call.execute();

    String mensaje = call.getString("P_MENSAJE");

    call.close();
    conn.close();

    return mensaje;
}

public Map<String, Object> InsRecetaCompleta(
        int idProducto,
        String nombre,
        BigDecimal rendimiento,
        List<Map<String, Object>> ingredientes
) throws Exception {

    Map<String, Object> resultado = new HashMap<>();

    if (idProducto <= 0
            || nombre == null
            || nombre.trim().isEmpty()
            || rendimiento == null
            || rendimiento.signum() <= 0
            || ingredientes == null
            || ingredientes.isEmpty()) {

        throw new Exception(
            "Debes indicar un producto, nombre, "
            + "rendimiento e ingredientes válidos."
        );
    }

    ConnectionsPool pool = new ConnectionsPool();

    try (Connection conn = pool.conectar()) {

        conn.setAutoCommit(false);

        try {

            // 1. Registrar la receta principal
            BigDecimal idReceta;

            String sqlReceta =
                "{call " + SCHEMA
                + ".PKG_RECETAS.INS_RECETA(?,?,?,?,?)}";

            try (CallableStatement call =
                    conn.prepareCall(sqlReceta)) {

                call.setInt(1, idProducto);
                call.setString(2, nombre.trim());
                call.setBigDecimal(3, rendimiento);

                call.registerOutParameter(4, Types.NUMERIC);
                call.registerOutParameter(5, Types.VARCHAR);

                call.execute();

                idReceta = call.getBigDecimal(4);
                String mensaje = call.getString(5);

                if (idReceta == null
                        || idReceta.signum() <= 0) {

                    throw new Exception(
                        mensaje != null
                            ? mensaje
                            : "No se pudo registrar la receta."
                    );
                }
            }

            // 2. Registrar los ingredientes
            Set<Integer> ingredientesUsados =
                new HashSet<>();

            for (Map<String, Object> ingrediente : ingredientes) {

                int idIngrediente = Integer.parseInt(
                    ingrediente.get("id_producto").toString()
                );

                BigDecimal cantidad = new BigDecimal(
                    ingrediente.get("cantidad").toString()
                );

                if (idIngrediente <= 0
                        || cantidad.signum() <= 0) {

                    throw new Exception(
                        "El ingrediente y su cantidad "
                        + "deben ser válidos."
                    );
                }

                if (!ingredientesUsados.add(idIngrediente)) {

                    throw new Exception(
                        "No puedes repetir un ingrediente "
                        + "en la misma receta."
                    );
                }

                String sqlDetalle =
                    "{call " + SCHEMA
                    + ".PKG_RECETAS.INS_DETALLE_RECETA(?,?,?,?,?)}";

                try (CallableStatement call =
                        conn.prepareCall(sqlDetalle)) {

                    call.setBigDecimal(1, idReceta);
                    call.setInt(2, idIngrediente);
                    call.setBigDecimal(3, cantidad);

                    call.registerOutParameter(4, Types.NUMERIC);
                    call.registerOutParameter(5, Types.VARCHAR);

                    call.execute();

                    BigDecimal idDetalle =
                        call.getBigDecimal(4);

                    String mensaje = call.getString(5);

                    if (idDetalle == null
                            || idDetalle.signum() <= 0) {

                        throw new Exception(
                            mensaje != null
                                ? mensaje
                                : "No se pudo registrar el ingrediente."
                        );
                    }
                }
            }

            // 3. Confirmar toda la receta
            conn.commit();

            resultado.put("EXITO", true);
            resultado.put(
                "MENSAJE",
                "Receta e ingredientes guardados correctamente."
            );
            resultado.put("ID_RECETA", idReceta);

        } catch (Exception e) {

            // Deshacer todos los registros de esta operación
            try {
                conn.rollback();
            } catch (Exception rollbackError) {
                e.addSuppressed(rollbackError);
            }

            throw e;
        }
    }

    return resultado;
}


public Map<String, Object> UpdRecetaCompleta(
        int idReceta,
        String nombre,
        BigDecimal rendimiento,
        List<Map<String, Object>> ingredientes
) throws Exception {

    Map<String, Object> resultado = new HashMap<>();

    if (idReceta <= 0
            || nombre == null
            || nombre.trim().isEmpty()
            || rendimiento == null
            || rendimiento.signum() <= 0
            || ingredientes == null
            || ingredientes.isEmpty()) {

        throw new Exception(
            "Debes indicar una receta, nombre, "
            + "rendimiento e ingredientes válidos."
        );
    }

    // Validar todos los ingredientes antes de modificar la BD.
    Set<Integer> ingredientesUsados = new HashSet<>();

    for (Map<String, Object> ingrediente : ingredientes) {

        if (ingrediente == null
                || ingrediente.get("id_producto") == null
                || ingrediente.get("cantidad") == null) {
            throw new Exception("Hay ingredientes incompletos.");
        }

        int idIngrediente = Integer.parseInt(
            ingrediente.get("id_producto").toString()
        );

        BigDecimal cantidad = new BigDecimal(
            ingrediente.get("cantidad").toString()
        );

        if (idIngrediente <= 0 || cantidad.signum() <= 0) {
            throw new Exception(
                "El ingrediente y su cantidad deben ser válidos."
            );
        }

        if (!ingredientesUsados.add(idIngrediente)) {
            throw new Exception(
                "No puedes repetir ingredientes en una receta."
            );
        }
    }

    ConnectionsPool pool = new ConnectionsPool();

    try (Connection conn = pool.conectar()) {

        conn.setAutoCommit(false);

        try {

            // 1. Obtener y bloquear la receta.
            int idProducto;
            int estado;

            String sqlConsulta =
                "SELECT ID_PRODUCTO, ESTADO "
                + "FROM " + SCHEMA + ".RECETA "
                + "WHERE ID_RECETA = ? FOR UPDATE";

            try (PreparedStatement ps =
                    conn.prepareStatement(sqlConsulta)) {

                ps.setInt(1, idReceta);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new Exception(
                            "No se encontró la receta indicada."
                        );
                    }

                    idProducto = rs.getInt("ID_PRODUCTO");
                    estado = rs.getInt("ESTADO");
                }
            }

            // 2. Obtener los detalles actuales.
            Map<Integer, Integer> detallesActuales =
                new HashMap<>();

            String sqlDetalles =
                "SELECT ID_DETALLE_RECETA, ID_PRODUCTO "
                + "FROM " + SCHEMA + ".DETALLE_RECETA "
                + "WHERE ID_RECETA = ?";

            try (PreparedStatement ps =
                    conn.prepareStatement(sqlDetalles)) {

                ps.setInt(1, idReceta);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        detallesActuales.put(
                            rs.getInt("ID_PRODUCTO"),
                            rs.getInt("ID_DETALLE_RECETA")
                        );
                    }
                }
            }

            // 3. Actualizar los datos principales.
            String sqlReceta =
                "{call " + SCHEMA
                + ".PKG_RECETAS.UPD_RECETA(?,?,?,?,?,?,?)}";

            try (CallableStatement call =
                    conn.prepareCall(sqlReceta)) {

                call.setInt(1, idReceta);
                call.setInt(2, idProducto);
                call.setString(3, nombre.trim());
                call.setBigDecimal(4, rendimiento);
                call.setInt(5, estado);

                call.registerOutParameter(6, Types.NUMERIC);
                call.registerOutParameter(7, Types.VARCHAR);

                call.execute();

                BigDecimal idSalida = call.getBigDecimal(6);
                String mensaje = call.getString(7);

                if (idSalida == null
                        || idSalida.signum() <= 0) {
                    throw new Exception(
                        mensaje != null
                            ? mensaje
                            : "No se pudo actualizar la receta."
                    );
                }
            }

            // 4. Quitar ingredientes que ya no están.
            for (Map.Entry<Integer, Integer> detalle
                    : detallesActuales.entrySet()) {

                int idIngrediente = detalle.getKey();

                if (!ingredientesUsados.contains(idIngrediente)) {

                    String sqlEliminar =
                        "{call " + SCHEMA
                        + ".PKG_RECETAS.DEL_DETALLE_RECETA(?,?,?)}";

                    try (CallableStatement call =
                            conn.prepareCall(sqlEliminar)) {

                        call.setInt(1, detalle.getValue());
                        call.registerOutParameter(2, Types.NUMERIC);
                        call.registerOutParameter(3, Types.VARCHAR);

                        call.execute();

                        BigDecimal idSalida = call.getBigDecimal(2);

                        if (idSalida == null
                                || idSalida.signum() <= 0) {
                            throw new Exception(call.getString(3));
                        }
                    }
                }
            }

            // 5. Actualizar o agregar ingredientes.
            for (Map<String, Object> ingrediente : ingredientes) {

                int idIngrediente = Integer.parseInt(
                    ingrediente.get("id_producto").toString()
                );

                BigDecimal cantidad = new BigDecimal(
                    ingrediente.get("cantidad").toString()
                );

                if (detallesActuales.containsKey(idIngrediente)) {

                    // Actualizar ingrediente existente.
                    String sqlActualizar =
                        "{call " + SCHEMA
                        + ".PKG_RECETAS.UPD_DETALLE_RECETA(?,?,?,?,?)}";

                    try (CallableStatement call =
                            conn.prepareCall(sqlActualizar)) {

                        call.setInt(
                            1,
                            detallesActuales.get(idIngrediente)
                        );
                        call.setInt(2, idIngrediente);
                        call.setBigDecimal(3, cantidad);
                        call.registerOutParameter(4, Types.NUMERIC);
                        call.registerOutParameter(5, Types.VARCHAR);

                        call.execute();

                        BigDecimal idSalida = call.getBigDecimal(4);

                        if (idSalida == null
                                || idSalida.signum() <= 0) {
                            throw new Exception(call.getString(5));
                        }
                    }

                } else {

                    // Agregar ingrediente nuevo.
                    String sqlInsertar =
                        "{call " + SCHEMA
                        + ".PKG_RECETAS.INS_DETALLE_RECETA(?,?,?,?,?)}";

                    try (CallableStatement call =
                            conn.prepareCall(sqlInsertar)) {

                        call.setInt(1, idReceta);
                        call.setInt(2, idIngrediente);
                        call.setBigDecimal(3, cantidad);
                        call.registerOutParameter(4, Types.NUMERIC);
                        call.registerOutParameter(5, Types.VARCHAR);

                        call.execute();

                        BigDecimal idSalida = call.getBigDecimal(4);

                        if (idSalida == null
                                || idSalida.signum() <= 0) {
                            throw new Exception(call.getString(5));
                        }
                    }
                }
            }

            // 6. Confirmar todos los cambios.
            conn.commit();

            resultado.put("EXITO", true);
            resultado.put("ID_RECETA", idReceta);
            resultado.put(
                "MENSAJE",
                "Receta e ingredientes actualizados correctamente."
            );

        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (Exception rollbackError) {
                e.addSuppressed(rollbackError);
            }

            throw e;
        }
    }

    return resultado;
}



}
