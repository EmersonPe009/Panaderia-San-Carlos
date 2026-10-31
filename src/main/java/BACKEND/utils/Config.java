
package BACKEND.utils;

import java.net.URL;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public final class Config {

    private XMLConfiguration config;

    public Config() {

        // El XML es opcional.
        // En Render utilizaremos variables de entorno.
        URL archivo = Config.class.getClassLoader()
                .getResource("config.xml");

        if (archivo != null) {
            try {
                Configurations configs = new Configurations();
                this.config = configs.xml(archivo);

            } catch (ConfigurationException e) {
                throw new IllegalStateException(
                        "Error al cargar config.xml", e
                );
            }
        }
    }

    private String obtenerValor(String variable, String claveXML) {

        String valor = System.getenv(variable);

        if (valor != null && !valor.trim().isEmpty()) {
            return valor.trim();
        }

        if (config != null) {
            return config.getString(claveXML);
        }

        throw new IllegalStateException(
                "Falta la variable de entorno: " + variable
        );
    }

    public String getDBHost() {
        return obtenerValor("DB_HOST", "db.host");
    }

    public String getDBPort() {
        return obtenerValor("DB_PORT", "db.port");
    }

    public String getDBService() {
        return obtenerValor("DB_SERVICE", "db.service");
    }

    public String getDBUser() {
        return obtenerValor("DB_USER", "db.user");
    }

    public String getDBPass() {
        return obtenerValor("DB_PASS", "db.pass");
    }

    public String getDBSchema() {
        return obtenerValor("DB_SCHEMA", "db.schema");
    }

    public String getSistemaNombre() {
        return "BACKEND";
    }
}
