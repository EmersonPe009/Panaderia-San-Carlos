
package BACKEND.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionsPool {

    private Connection conn;

    public Connection conectar() throws SQLException {

        Config cf = new Config();

        String HOST = cf.getDBHost();
        String PORT = cf.getDBPort();
        String SERVICE = cf.getDBService();
        String USR = cf.getDBUser();
        String PASS = cf.getDBPass();

        // Oracle Cloud utiliza TCPS.
        // Oracle XE local utiliza TCP.
        boolean oracleCloud = "1522".equals(PORT);

        String PROTOCOL = oracleCloud ? "tcps" : "tcp";

        String URL = "jdbc:oracle:thin:@"
                + "(description="
                + "(address="
                + "(protocol=" + PROTOCOL + ")"
                + "(host=" + HOST + ")"
                + "(port=" + PORT + ")"
                + ")"
                + "(connect_data="
                + "(service_name=" + SERVICE + ")"
                + ")"
                + (oracleCloud
                    ? "(security=(ssl_server_dn_match=yes))"
                    : "")
                + ")";

        
try {
    Class.forName("oracle.jdbc.OracleDriver");
} catch (ClassNotFoundException e) {
    throw new SQLException(
        "No se encontro el controlador JDBC de Oracle (ojdbc8)",
        e
    );
}

this.conn = DriverManager.getConnection(
        URL, USR, PASS
);

return this.conn;

    }

    public Connection conectarSQLSERVER()
            throws SQLException, ClassNotFoundException {

        String HOST = "KUALA-LUMPUR";
        String PORT = "1433";
        String DB = "DBAntecedentesPenales";
        String USR = "unap-app";
        String PASS = "unap";

        String connectionUrl =
                "jdbc:sqlserver://" + HOST + ":" + PORT + ";"
                + "databaseName=" + DB
                + ";user=" + USR
                + ";password=" + PASS;

        Class.forName(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        );

        this.conn = DriverManager.getConnection(connectionUrl);

        return this.conn;
    }
}
