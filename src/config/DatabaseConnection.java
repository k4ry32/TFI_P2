package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String PROPERTIES_FILE = "/db.properties";
    private static String url = "jdbc:mysql://localhost:3306/tpi_pedidos";
    private static String user = "root";
    private static String pass = "d61af0015ddc77da13110d5056240853"; // Verificar!
    private static String driver;

    static {
        try (InputStream input = DatabaseConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo " + PROPERTIES_FILE);
            }

            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            pass = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            // Cargar el driver de MySQL (opcional pero recomendado)
            Class.forName(driver);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar la configuración de la base de datos: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}