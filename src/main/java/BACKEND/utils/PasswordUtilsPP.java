
package BACKEND.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtilsPP {

    // Generar un hash seguro de una contraseña
    public static String generarHash(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                "La contraseña no puede estar vacía."
            );
        }

        return BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        );
    }

    // Verificar una contraseña contra su hash
    public static boolean verificarPassword(
            String password,
            String hash) {

        if (password == null || hash == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}


