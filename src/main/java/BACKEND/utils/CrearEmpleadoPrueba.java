
package BACKEND.utils;

public class CrearEmpleadoPrueba {

    public static void main(String[] args) {

        generarHash("empleado_central", "Central2026!");
        generarHash("empleado_barco", "Barco2026!");
        generarHash("empleado_heydi", "Heydi2026!");
        generarHash("empleado_terminal", "Terminal2026!");
    }

    private static void generarHash(
            String usuario,
            String contrasena) {

        String hash =
                org.mindrot.jbcrypt.BCrypt.hashpw(
                    contrasena,
                    org.mindrot.jbcrypt.BCrypt.gensalt(12)
                );

        System.out.println("--------------------------");
        System.out.println("USUARIO: " + usuario);
        System.out.println("HASH_BCRYPT: " + hash);
    }
}
