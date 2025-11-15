/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import config.DatabaseConnection;
import entities.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import java.util.ArrayList;


public class EnvioDao implements GenericDao<Envio>{

public boolean crear(Envio entidad, Connection conn) throws Exception {
    String sql = "INSERT INTO ENVIOS (id,eliminado,tracking,empresa,tipo,costo,fechaDespacho,fechaEstimada,estado) VALUES (?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setLong(1,entidad.getId());
        stmt.setInt(2, entidad.getEliminado());
        stmt.setString(3, entidad.getTracking());
        stmt.setString(4, entidad.getEmpresa());
        stmt.setString(5, entidad.getTipo());
        stmt.setDouble(6, entidad.getCosto());
        stmt.setString(7, entidad.getFechaDespacho());
        stmt.setString(8, entidad.getFechaEstimada());
        stmt.setString(9, entidad.getEstado());

        int filasAfectadas = stmt.executeUpdate();
        
        if (filasAfectadas > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long nuevoID = generatedKeys.getLong(1);
                    entidad.setId(nuevoID);
                    System.out.println("Envío agregado con ID: " + nuevoID);
                }
            }
            return true;
        }
        
        return false;

    } catch (Exception e) {
        e.printStackTrace();
        throw new Exception("Error DAO al insertar el envío.", e);
    }
}

    @Override
    public Optional<Envio> leer(long id, Connection conn) throws Exception {
    
    Envio envio = null; 
    
    String sql = "SELECT id, eliminado, tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado FROM ENVIOS WHERE id = ? AND eliminado = 0";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, id); 
        
        try (ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                envio = new Envio(
                    rs.getLong("id"),
                    rs.getInt("eliminado"),
                    rs.getString("tracking"),
                    rs.getString("empresa"),
                    rs.getString("tipo"),
                    rs.getDouble("costo"),
                    rs.getString("fechaDespacho"),
                    rs.getString("fechaEstimada"),
                    rs.getString("estado")
                );
            }
        }
        
    } catch (SQLException e) {
        System.err.println("Error DAO al leer el envío por ID: " + e.getMessage());
        throw new Exception("Error al consultar el envío.", e);
    }
    
    return Optional.ofNullable(envio);
}

    @Override
public List<Envio> leerTodos(Connection conn) throws Exception {
    
    List<Envio> listaEnvios = new ArrayList<>();
    
    String sql = "SELECT id, eliminado, tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado FROM ENVIOS WHERE eliminado = 0";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            
            Envio envio = new Envio(
                rs.getLong("id"),
                rs.getInt("eliminado"),
                rs.getString("tracking"),
                rs.getString("empresa"),
                rs.getString("tipo"),
                rs.getDouble("costo"),
                rs.getString("fechaDespacho"),
                rs.getString("fechaEstimada"),
                rs.getString("estado")
            );
            
            listaEnvios.add(envio);
        }
        
    } catch (SQLException e) {
        System.err.println("Error DAO al leer todos los envíos: " + e.getMessage());
        throw new Exception("Error al consultar la lista de envíos.", e);
    }
    
    return listaEnvios;
}


@Override
public boolean actualizar(Envio entidad, Connection conn) throws Exception {
    
    String sql = "UPDATE ENVIOS SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fechaDespacho = ?, fechaEstimada = ?, estado = ? WHERE id = ? AND eliminado = 0";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, entidad.getTracking());
        stmt.setString(2, entidad.getEmpresa());
        stmt.setString(3, entidad.getTipo());
        stmt.setDouble(4, entidad.getCosto());
        stmt.setString(5, entidad.getFechaDespacho());
        stmt.setString(6, entidad.getFechaEstimada());
        stmt.setString(7, entidad.getEstado());
        
        stmt.setLong(8, entidad.getId()); 

        int filasAfectadas = stmt.executeUpdate();
        
        return filasAfectadas == 1; 

    } catch (SQLException e) {
        System.err.println("Error DAO al actualizar el envío: " + e.getMessage());
        throw new Exception("Error al actualizar el envío.", e);
    }
}

    @Override
public boolean eliminar(long id, Connection conn) throws Exception {
    
    String sql = "UPDATE ENVIOS SET eliminado = 1 WHERE id = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, id); 

        int filasAfectadas = stmt.executeUpdate();
        
        return filasAfectadas == 1; 

    } catch (SQLException e) {
        System.err.println("Error DAO al eliminar (baja lógica) el envío: " + e.getMessage());
        throw new Exception("Error al marcar el envío como eliminado.", e);
    }
}

}
