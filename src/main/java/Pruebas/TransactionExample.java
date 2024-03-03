import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionExample {

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/colegio";
        String user = "root";
        String password = "";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            connection.setAutoCommit(false);

            // Inserta un nuevo alumno
            insertAlumno(connection, 6, "Carlos", "González", "1994-08-23");

            // Intenta insertar un nuevo alumno con el mismo ID, lo cual debería fallar
            insertAlumno(connection, 6, "Laura", "Martínez", "1995-03-10");

            // Si llega aquí sin excepciones, confirma la transacción
            connection.commit();
            System.out.println("Transacción realizada con éxito.");
        } catch (SQLException e) {
            System.out.println("Se produjo un error en la transacción. Se realizará un rollback.");
            e.printStackTrace();
            // Si hay una excepción, realiza un rollback para deshacer cualquier cambio en la base de datos
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al realizar rollback.");
                ex.printStackTrace();
            }
        } finally {
            // Finalmente, cierra la conexión en el bloque finally para asegurar su cierre adecuado
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void insertAlumno(Connection connection, int idAlumno, String nombre, String apellidos, String fechaNac) throws SQLException {
        String query = "INSERT INTO alumnos (id_alumno, nombre, apellidos, fecha_nac) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idAlumno);
            statement.setString(2, nombre);
            statement.setString(3, apellidos);
            statement.setString(4, fechaNac);
            statement.executeUpdate();
            System.out.println("Nuevo alumno insertado: " + nombre + " " + apellidos);
        }
    }
}
