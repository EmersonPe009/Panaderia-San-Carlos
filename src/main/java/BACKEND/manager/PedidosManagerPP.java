
package BACKEND.manager;

import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import oracle.jdbc.OracleTypes;

public class PedidosManagerPP {

    private final String SCHEMA =
            new Config().getDBSchema();

    private List<Map<String, Object>> ejecutarConsulta(
            String procedimiento, Integer id) throws Exception {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "{call " + SCHEMA + ".PKG_PEDIDOS."
                + procedimiento
                + (id == null ? "(?)}" : "(?,?)}");

        try (Connection conn = new ConnectionsPool().conectar();
             CallableStatement call = conn.prepareCall(sql)) {

            int posicionCursor = 1;

            if (id != null) {
                call.setInt(1, id);
                posicionCursor = 2;
            }

            call.registerOutParameter(
                    posicionCursor, OracleTypes.CURSOR
            );

            call.execute();

            try (ResultSet rs =
                    (ResultSet) call.getObject(posicionCursor)) {

                ResultSetMetaData meta = rs.getMetaData();

                while (rs.next()) {

                    Map<String, Object> fila =
                            new LinkedHashMap<>();

                    for (int i = 1; i <= meta.getColumnCount(); i++) {
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

        return lista;
    }

    // ========================================
    // CONSULTAR TODOS LOS PEDIDOS
    // ========================================
    public List<Map<String, Object>> CargarPedidos()
            throws Exception {

        return ejecutarConsulta("CARGAR_PEDIDOS", null);
    }

    // ========================================
    // CONSULTAR PEDIDOS POR SUCURSAL
    // ========================================
    public List<Map<String, Object>> CargarPedidosSucursal(
            int idSucursal) throws Exception {

        List<Map<String, Object>> todos = CargarPedidos();
        List<Map<String, Object>> filtrados = new ArrayList<>();

        for (Map<String, Object> pedido : todos) {

            Object sucursal = pedido.get("ID_SUCURSAL");

            if (sucursal != null
                    && String.valueOf(idSucursal)
                            .equals(sucursal.toString())) {

                filtrados.add(pedido);
            }
        }

        return filtrados;
    }

    // ========================================
    // CONSULTAR PEDIDO POR ID
    // ========================================
    public List<Map<String, Object>> GetPedido(int id)
            throws Exception {

        return ejecutarConsulta("GET_PEDIDO", id);
    }

    // ========================================
    // VALIDAR PROPIEDAD DEL PEDIDO
    // ========================================
    public boolean PerteneceASucursal(
            int idPedido, int idSucursal) throws Exception {

        String sql = "SELECT COUNT(*) FROM "
                + SCHEMA
                + ".PEDIDO WHERE ID_PEDIDO = ? "
                + "AND ID_SUCURSAL = ?";

        try (Connection conn = new ConnectionsPool().conectar();
             java.sql.PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            ps.setInt(2, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ========================================
    // CONSULTAR DETALLES DEL PEDIDO
    // ========================================
    public List<Map<String, Object>> CargarDetallesPedido(int id)
            throws Exception {

        return ejecutarConsulta("CARGAR_DETALLES_PEDIDO", id);
    }

    // ========================================
    // REGISTRAR PEDIDO COMPLETO
    // ========================================
    public Map<String, Object> InsPedidoCompleto(String json)
            throws Exception {

        Map<String, Object> resultado = new LinkedHashMap<>();

        try (Connection conn = new ConnectionsPool().conectar();
             CallableStatement call = conn.prepareCall(
                "{call " + SCHEMA
                + ".PKG_PEDIDOS.INS_PEDIDO_COMPLETO(?,?,?)}"
             )) {

            call.setString(1, json);
            call.registerOutParameter(2, Types.NUMERIC);
            call.registerOutParameter(3, Types.VARCHAR);

            call.execute();

            Object idSalida = call.getObject(2);

            resultado.put("EXITO", idSalida != null);
            resultado.put("ID_PEDIDO", idSalida);
            resultado.put("MENSAJE", call.getString(3));
        }

        return resultado;
    }

    // ========================================
    // DESPACHAR PEDIDO
    // ========================================
    public Map<String, Object> DespacharPedido(int id)
            throws Exception {

        Map<String, Object> resultado = new LinkedHashMap<>();

        try (Connection conn = new ConnectionsPool().conectar();
             CallableStatement call = conn.prepareCall(
                "{call " + SCHEMA
                + ".PKG_PEDIDOS.DESPACHAR_PEDIDO(?,?,?)}"
             )) {

            call.setInt(1, id);
            call.registerOutParameter(2, Types.NUMERIC);
            call.registerOutParameter(3, Types.VARCHAR);

            call.execute();

            Object idSalida = call.getObject(2);

            resultado.put("EXITO", idSalida != null);
            resultado.put("ID_PEDIDO", idSalida);
            resultado.put("MENSAJE", call.getString(3));
        }

        return resultado;
    }
    
    
    // ========================================
    // REGISTRAR RESTO DE UN PRODUCTO
    // ========================================
    public Map<String, Object> RegistrarResto(
            int idDetallePedido,
            int cantidadResto) throws Exception {

        Map<String, Object> resultado =
                new LinkedHashMap<>();

        try (Connection conn =
                    new ConnectionsPool().conectar();

             CallableStatement call = conn.prepareCall(
                "{call " + SCHEMA
                + ".PKG_PEDIDOS.REGISTRAR_RESTO(?,?,?,?)}"
             )) {

            call.setInt(1, idDetallePedido);
            call.setInt(2, cantidadResto);

            call.registerOutParameter(
                    3, Types.NUMERIC
            );

            call.registerOutParameter(
                    4, Types.VARCHAR
            );

            call.execute();

            Object idSalida = call.getObject(3);

            resultado.put(
                    "EXITO",
                    idSalida != null
            );

            resultado.put(
                    "ID_DETALLE_PEDIDO",
                    idSalida
            );

            resultado.put(
                    "MENSAJE",
                    call.getString(4)
            );
        }

        return resultado;
    }

    // ========================================
    // OBTENER PEDIDO DE UN DETALLE
    // ========================================
    public Integer ObtenerPedidoDeDetalle(
            int idDetallePedido) throws Exception {

        String sql = "SELECT ID_PEDIDO FROM "
                + SCHEMA
                + ".DETALLE_PEDIDO "
                + "WHERE ID_DETALLE_PEDIDO = ?";

        try (Connection conn =
                    new ConnectionsPool().conectar();

             java.sql.PreparedStatement ps =
                    conn.prepareStatement(sql)) {

            ps.setInt(1, idDetallePedido);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("ID_PEDIDO");
                }
            }
        }

        return null;
    }

}
