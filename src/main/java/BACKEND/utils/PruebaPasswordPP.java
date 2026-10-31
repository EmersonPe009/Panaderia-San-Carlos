
package BACKEND.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PruebaPasswordPP {

    public static void main(String[] args) {

        String password = "Chiva2026!";

        String hash = "$2a$12$IMcM7qrkP2a9anr8Oq1Kq.P4CSMEwTwJhFK6XI/McdJ.QedkqDsly";

        // Comprobar la contraseña actual
        boolean resultado = BCrypt.checkpw(password, hash);

        System.out.println("================================");
        System.out.println("PRUEBA DE CONTRASEÑA");
        System.out.println("================================");
        System.out.println("Contraseña correcta: " + resultado);

        // Generar un nuevo hash para esa contraseña
        String nuevoHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        System.out.println("================================");
        System.out.println("NUEVO HASH BCrypt:");
        System.out.println(nuevoHash);
        System.out.println("================================");
    }
}
