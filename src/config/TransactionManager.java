
package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author kari
 */
public class TransactionManager implements AutoCloseable {
    private Connection connection;
    private boolean activeTx;
    
    public TransactionManager(Connection connection) throws SQLException{
        if (connection == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.connection = connection;
        this.activeTx = false;
    }
    
    
    public Connection getConnection() {
        return connection;
    }
    
    public void startTransaction() throws SQLException {
        if (connection == null) {
            throw new SQLException("No se puede iniciar la transacción: conexión no disponible");
        }
        else if (connection.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        
        connection.setAutoCommit(false);
        activeTx = true;
    }
    
    public void commit() throws SQLException {
        if (connection == null) {
            throw new SQLException("Error en commit: conexión no disponible");
        }
        else if (!activeTx) {
            throw new SQLException("No hay una transacción activa");
        }
        
        connection.commit();
        activeTx = false;
        System.out.println("✓ Transacción confirmada (commit) exitosamente");
    }
    
    public void rollback() throws SQLException {
        if (connection == null) {
            throw new SQLException("Error en rollback: conexión no disponible");
        }
        else if (!activeTx) {
            throw new SQLException("No hay una transacción activa");
        }
        
        connection.rollback();
        activeTx = false;
        System.out.println("⚠ Transacción revertida (rollback) - Los cambios no se guardaron");
    }
    
    public boolean isTransactionActive() {
        return activeTx;
    }
    

    @Override
    public void close() throws SQLException {
        if (connection == null) {
            throw new SQLException("Error en rollback: conexión no disponible");
        }
        if (activeTx) {
            System.out.println("⚠ Transacción activa detectada al cerrar la conexión - ejecutando rollback automático");
            rollback();
        }
        
        connection.setAutoCommit(true);
        connection.close();
    }
}
