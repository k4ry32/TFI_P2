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
import java.sql.Date;


public class EnvioDao implements GenericDao<Envio>{

public boolean crear(Envio entidad, Connection conn) throws Exception {
    String sql = "INSERT INTO ENVIOS (id,eliminado,tracking,empresa,tipo,costo,fechaDespacho,fechaEstimada,estado) VALUES (?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setLong(1,entidad.getId());
        stmt.setBoolean(2, entidad.getEliminado());
        stmt.setString(3, entidad.getTracking());
        stmt.setString(4, entidad.getEmpresa().name());
        stmt.setString(5, entidad.getTipo().name());
        stmt.setDouble(6, entidad.getCosto());
        stmt.setDate(7, entidad.getFechaDespacho() != null ? Date.valueOf(entidad.getFechaDespacho()) : null);
        stmt.setDate(8, entidad.getFechaEstimada() != null ? Date.valueOf(entidad.getFechaEstimada()) : null);
        stmt.setString(9, entidad.getEstado().name());

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
                envio = new Envio();
                
                envio.setId(rs.getLong("id"));
                envio.setEliminado(rs.getBoolean("eliminado"));
                envio.setTracking(rs.getString("tracking"));
                envio.setEmpresa(Envio.EmpresaEnvio.valueOf(rs.getString("empresa")));
                envio.setTipo(Envio.TipoEnvio.valueOf(rs.getString("tipo")));
                envio.setCosto(rs.getDouble("costo"));
                envio.setEstado(Envio.EstadoEnvio.valueOf(rs.getString("estado")));
                
                Date fechaDespacho = rs.getDate("fechaDespacho");
                if(fechaDespacho != null)  
                    envio.setFechaDespacho(fechaDespacho.toLocalDate());
                
                Date fechaEstimada = rs.getDate("fechaEstimada");
                if (fechaEstimada != null)
                    envio.setFechaEstimada(fechaEstimada.toLocalDate());
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
                rs.getBoolean("eliminado"),
                rs.getString("tracking"),
                Envio.EmpresaEnvio.valueOf(rs.getString("empresa")),
                Envio.TipoEnvio.valueOf(rs.getString("tipo")),
                rs.getDouble("costo"),
                rs.getDate("fechaDespacho") != null ? rs.getDate("fechaDespacho").toLocalDate() : null,
                rs.getDate("fechaEstimada") != null ? rs.getDate("fechaEstimada").toLocalDate() : null,
                Envio.EstadoEnvio.valueOf(rs.getString("estado"))
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
        stmt.setString(2, entidad.getEmpresa().name());
        stmt.setString(3, entidad.getTipo().name());
        stmt.setDouble(4, entidad.getCosto());
        stmt.setDate(5, entidad.getFechaDespacho() != null ? Date.valueOf(entidad.getFechaDespacho()) : null);
        stmt.setDate(6, entidad.getFechaEstimada() != null ? Date.valueOf(entidad.getFechaEstimada()) : null);
        stmt.setString(7, entidad.getEstado().name());
        
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
