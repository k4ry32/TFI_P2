package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import entities.Pedido;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
// Importamos solo lo necesario para la lógica SQL, NO DatabaseConnection

// La clase PedidoDAO asume que se le pasa la conexión desde el exterior.
public class PedidoDao implements GenericDao<Pedido>{

    public boolean crear(Pedido entidad, Connection conn){
        
        String sql = "INSERT INTO PEDIDOS (id, eliminado, numero, fecha, clienteNombre, total, estado, envio) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, entidad.getId());
            stmt.setInt(2, entidad.getEliminado());
            stmt.setString(3, entidad.getNumero());
            stmt.setString(4, entidad.getFecha());
            stmt.setString(5, entidad.getClienteNombre());
            stmt.setDouble(6, entidad.getTotal());
            stmt.setString(7, entidad.getEstado());
            stmt.setLong(8, entidad.getEnvio());

            int filasAfectadas = stmt.executeUpdate();

            //Si devuelve 1 o mas de una fila afectada, indica que se ejecuto con exito
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long nuevoID = generatedKeys.getLong(1);
                        entidad.setId(nuevoID);
                        System.out.println("Pedido agregado con ID: " + nuevoID);
                    }
                }
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error al crear, "+ e.getMessage());
            return false;
        }

    }

    /**
     * Devuelve un objeto pedido wrapeado en un objeto Optional.
     * @param id
     * @param conn
     * @return 
     */
    @Override
    public Optional<Pedido> leer(long id, Connection conn){
        Pedido pedido=null;
        String sql = "SELECT * FROM pedidos WHERE id =  ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,(int)id);
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()){
                rs.getInt("id");
                
                pedido = new Pedido(rs.getLong("id"),rs.getInt("eliminado"),rs.getString("numero"),rs.getString("fecha"),rs.getString("clienteNombre"), rs.getDouble("total"), rs.getString("estado"), rs.getLong("envio"));
                
            }
            else{
                System.out.println("No encontrado");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar, "+ e.getMessage());
        }
        return Optional.of(pedido);
    }

    @Override
    public List leerTodos(Connection conn){
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean actualizar(Pedido entidad, Connection conn) throws Exception {

        String sql = "UPDATE PEDIDOS SET numero = ?, fecha = ?, clienteNombre = ?, total = ?, estado = ?, envio = ? WHERE id = ? AND eliminado = 0";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entidad.getNumero());
            stmt.setString(2, entidad.getFecha());
            stmt.setString(3, entidad.getClienteNombre());
            stmt.setDouble(4, entidad.getTotal());
            stmt.setString(5, entidad.getEstado());
            stmt.setLong(6, entidad.getEnvio());

            // Cláusula WHERE
            stmt.setLong(7, entidad.getId()); 

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas == 1; 

        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar el pedido: " + e.getMessage());
            throw new Exception("Error al actualizar el pedido.", e);
        }
    }

    @Override
    public boolean eliminar(long id, Connection conn) throws Exception {

        // Baja logica es con 1
        String sql = "UPDATE PEDIDOS SET eliminado = 1 WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id); 

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas == 1; 

        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar (baja lógica) el pedido: " + e.getMessage());
            throw new Exception("Error al marcar el pedido como eliminado.", e);
        }
    }
}