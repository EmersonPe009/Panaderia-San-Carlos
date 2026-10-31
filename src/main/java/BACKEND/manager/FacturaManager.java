/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BACKEND.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import BACKEND.model.Cliente;
import BACKEND.model.Factura;
import BACKEND.model.Producto;

import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import BACKEND.utils.jsonResult;

import oracle.jdbc.OracleTypes;

public class FacturaManager {

    String SCHEMA = new Config().getDBSchema();

    public jsonResult AgregarFactura(Factura item) throws Exception {

        
        
        
    jsonResult salida = new jsonResult();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    conn.setAutoCommit(false);

    try {

        // =========================================
        // 1. INSERTAR CABECERA DE FACTURA
        // =========================================

        CallableStatement call =
            conn.prepareCall(
                "call " + SCHEMA +
                ".PKG_FACTURA.INS_FACTURA(?,?,?)"
            );

        call.setString("ID_CLIENTE", item.ID_CLIENTE);

        call.registerOutParameter(
            "ID_SALIDA",
            OracleTypes.NUMBER
        );

        call.registerOutParameter(
            "MENSAJE",
            OracleTypes.VARCHAR
        );

        call.execute();

        int idFactura = call.getInt("ID_SALIDA");
        String msj = call.getString("MENSAJE");

        call.close();

        // =========================================
        // 2. VERIFICAR SI LA FACTURA SE CREÓ
        // =========================================

        if (idFactura == -1) {

            conn.rollback();

            salida.id = -1;
            salida.result = "ERROR";
            salida.msj = msj;

            conn.close();

            return salida;
        }

        // =========================================
        // 3. INSERTAR LOS DETALLES
        // =========================================

        for (BACKEND.model.DetalleFactura detalle : item.DETALLE) {

            CallableStatement callDetalle =
                conn.prepareCall(
                    "call " + SCHEMA +
                    ".PKG_FACTURA.INS_DETALLE_FACTURA(?,?,?,?,?,?)"
                );

            callDetalle.setInt(
                "ID_FACTURA",
                idFactura
            );

            callDetalle.setString(
                "ID_PRODUCTO",
                detalle.ID_PRODUCTO
            );

            callDetalle.setString(
                "CANTIDAD",
                detalle.CANTIDAD
            );

            callDetalle.setString(
                "SUBTOTAL",
                detalle.SUBTOTAL
            );

            callDetalle.registerOutParameter(
                "ID_SALIDA",
                OracleTypes.NUMBER
            );

            callDetalle.registerOutParameter(
                "MENSAJE",
                OracleTypes.VARCHAR
            );

            callDetalle.execute();

            int idDetalle =
                callDetalle.getInt("ID_SALIDA");

            String msjDetalle =
                callDetalle.getString("MENSAJE");

            callDetalle.close();

            // =========================================
            // 4. SI UN DETALLE FALLA → ROLLBACK
            // =========================================

            if (idDetalle == -1) {

                throw new Exception(msjDetalle);
            }
        }

        // =========================================
        // 5. TODO CORRECTO → COMMIT
        // =========================================

        conn.commit();

        salida.id = idFactura;
        salida.result = "OK";
        salida.msj = "Factura y detalles ingresados correctamente";

    } catch (Exception e) {

        // =========================================
        // 6. ERROR → ROLLBACK
        // =========================================

        conn.rollback();

        salida.id = -1;
        salida.result = "ERROR";
        salida.msj = e.getMessage();

    } finally {

        conn.close();
    }

    return salida;
}
public List<Cliente> CargarClientes() throws Exception {

    List<Cliente> lista = new ArrayList<Cliente>();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    try {

        CallableStatement call =
            conn.prepareCall(
                "call " + SCHEMA +
                ".PKG_FACTURA.CARGAR_CLIENTES(?)"
            );

        call.registerOutParameter(
            "P_CURSOR",
            OracleTypes.CURSOR
        );

        call.execute();

        ResultSet rs =
            (ResultSet) call.getObject("P_CURSOR");

        while (rs.next()) {

            Cliente item = new Cliente();

            item.ID_CLIENTE =
                rs.getString("ID");

            item.CLIENTE =
                rs.getString("NOMBRE_CLIENTE");

            item.NIT =
                rs.getString("NIT");

            item.ESTADO =
                rs.getString("ESTADO");

            lista.add(item);
        }

        rs.close();
        call.close();

    } finally {

        conn.close();
    }

    return lista;
}
    public List<Producto> CargarProductos() throws Exception {

    List<Producto> lista = new ArrayList<Producto>();

    ConnectionsPool c = new ConnectionsPool();
    Connection conn = c.conectar();

    try {

        CallableStatement call =
            conn.prepareCall(
                "call " + SCHEMA +
                ".PKG_FACTURA.CARGAR_PRODUCTOS(?)"
            );

        call.registerOutParameter(
            "P_CURSOR",
            OracleTypes.CURSOR
        );

        call.execute();

        ResultSet rs =
            (ResultSet) call.getObject("P_CURSOR");

        while (rs.next()) {

            Producto item = new Producto();

            item.ID_PRODUCTO =
                rs.getString("ID");

            item.PRODUCTO =
                rs.getString("NOMBRE_PRODUCTO");

            item.CODIGO =
                rs.getString("CODIGO");

            item.PRECIO =
                rs.getString("PRECIO");

            item.ESTADO =
                rs.getString("ESTADO");

            lista.add(item);
        }

        rs.close();
        call.close();

    } finally {

        conn.close();
    }

    return lista;
}
}