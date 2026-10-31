
package BACKEND.utils;

import java.io.Console;

public class GenerarHashAdminPP {

    public static void main(String[] args) {

        Console consola = System.console();

        if (consola == null) {
            System.out.println(
                "Ejecuta esta clase desde una terminal "
                + "para ingresar la contraseña de forma segura."
            );
            return;
        }

        char[] password = consola.readPassword(
            "Contraseña del administrador: "
        );

        if (password == null || password.length < 12) {
            System.out.println(
                "La contraseña debe tener al menos 12 caracteres."
            );
            return;
        }

        String hash = PasswordUtilsPP.generarHash(
            new String(password)
        );

        java.util.Arrays.fill(password, '\0');

        System.out.println("HASH GENERADO:");
        System.out.println(hash);
    }
}
