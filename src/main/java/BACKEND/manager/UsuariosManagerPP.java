
package BACKEND.manager;

import BACKEND.utils.Config;
import BACKEND.utils.ConnectionsPool;
import BACKEND.utils.PasswordUtilsPP;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import oracle.jdbc.OracleTypes;

public class UsuariosManagerPP {

    String SCHEMA = new Config().getDBSchema();

   
public Map<String, Object> ValidarLogin(
        String usuario,
        String contrasena) throws Exception {

    Map<String, Object> resultado =
            new HashMap<String, Object>();

    resultado.put("AUTENTICADO", false);
    resultado.put("MENSAJE", "Usuario o contraseña incorrectos.");

    // =========================================
    // 1. VALIDAR DATOS RECIBIDOS
    // =========================================

    if (usuario == null || usuario.trim().isEmpty()
            || contrasena == null || contrasena.isEmpty()) {

        System.out.println("LOGIN: Usuario o contraseña vacíos");
        return resultado;
    }

    System.out.println("==================================");
    System.out.println("INICIANDO VALIDACIÓN DE LOGIN");
    System.out.println("LOGIN: Usuario solicitado = " + usuario.trim());
    System.out.println("LOGIN: Esquema utilizado = " + SCHEMA);

    ConnectionsPool c = new ConnectionsPool();

    // =========================================
    // 2. CONSULTAR USUARIO EN ORACLE
    // =========================================

    try (Connection conn = c.conectar();
         CallableStatement call = conn.prepareCall(
                 "{call " + SCHEMA
                 + ".PKG_USUARIOS.GET_USUARIO_LOGIN(?,?)}")) {

        call.setString(1, usuario.trim());
        call.registerOutParameter(2, OracleTypes.CURSOR);

        call.execute();

        System.out.println("LOGIN: Procedimiento ejecutado correctamente");

        try (ResultSet rset = (ResultSet) call.getObject(2)) {

            if (rset.next()) {

                System.out.println("LOGIN: Usuario encontrado en Oracle");

                // =========================================
                // 3. VERIFICAR CONTRASEÑA
                // =========================================

                String hashGuardado =
                        rset.getString("PASSWORD_HASH");

                System.out.println("LOGIN: Hash presente = "
                        + (hashGuardado != null));

                boolean passwordCorrecto =
                        PasswordUtilsPP.verificarPassword(
                                contrasena,
                                hashGuardado
                        );
                
System.out.println("LOGIN: Longitud contraseña recibida = "
        + contrasena.length());

System.out.println("LOGIN: Longitud hash Oracle = "
        + (hashGuardado == null ? 0 : hashGuardado.length()));

System.out.println("LOGIN: Contraseña de prueba válida = "
        + PasswordUtilsPP.verificarPassword(
                "Chiva2026!",
                hashGuardado
        ));

System.out.println("LOGIN: Contraseña recibida válida = "
        + passwordCorrecto);


                System.out.println("LOGIN: Contraseña válida = "
                        + passwordCorrecto);

                // =========================================
                // 4. LOGIN CORRECTO
                // =========================================

                if (passwordCorrecto) {

                    resultado.put("AUTENTICADO", true);
                    resultado.put("MENSAJE",
                            "Inicio de sesión correcto.");

                    resultado.put("ID_USUARIO",
                            rset.getInt("ID_USUARIO"));

                    resultado.put("NOMBRE_COMPLETO",
                            rset.getString("NOMBRE_COMPLETO"));

                    resultado.put("USUARIO",
                            rset.getString("USUARIO"));

                    resultado.put("ID_ROL",
                            rset.getInt("ID_ROL"));

                    resultado.put("ROL",
                            rset.getString("ROL"));

                    // =====================================
                    // 5. OBTENER SUCURSAL DEL EMPLEADO
                    // =====================================

                    int idSucursal =
                            rset.getInt("ID_SUCURSAL");

                    resultado.put("ID_SUCURSAL",
                            rset.wasNull() ? null : idSucursal);

                    resultado.put("NOMBRE_SUCURSAL",
                            rset.getString("NOMBRE_SUCURSAL"));

                    System.out.println("LOGIN: AUTENTICACIÓN CORRECTA");
                    System.out.println("LOGIN: Rol = "
                            + resultado.get("ROL"));
                    System.out.println("LOGIN: Sucursal = "
                            + resultado.get("ID_SUCURSAL"));

                } else {

                    System.out.println(
                            "LOGIN: La contraseña no coincide"
                    );
                }

            } else {

                System.out.println(
                        "LOGIN: Usuario NO encontrado en Oracle"
                );
            }
        }

    } catch (Exception e) {

        System.err.println(
                "LOGIN: Error al consultar Oracle: "
                + e.getMessage()
        );

        throw e;
    }

    System.out.println("LOGIN: Resultado final = "
            + resultado.get("AUTENTICADO"));
    System.out.println("==================================");

    return resultado;
}



}
