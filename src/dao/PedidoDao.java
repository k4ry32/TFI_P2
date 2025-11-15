package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import entities.Pedido;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import java.sql.Date;
import entities.Envio;
// Importamos solo lo necesario para la lógica SQL, NO DatabaseConnection

// La clase PedidoDAO asume que se le pasa la conexión desde el exterior.
public class PedidoDao implements GenericDao<Pedido>{

    public boolean crear(Pedido entidad, Connection conn){
        
        String sql = "INSERT INTO PEDIDOS (id, eliminado, numero, fecha, clienteNombre, total, estado, envio) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, entidad.getId());
            stmt.setBoolean(2, entidad.getEliminado());
            stmt.setString(3, entidad.getNumero());
            stmt.setDate(4, entidad.getFecha() != null ? Date.valueOf(entidad.getFecha()) : null);
            stmt.setString(5, entidad.getClienteNombre());
            stmt.setDouble(6, entidad.getTotal());
            stmt.setString(7, entidad.getEstado().name());
            stmt.setObject(8, entidad.getEnvio() != null ? entidad.getEnvio().getId() : null);

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
                
                pedido = new Pedido();
                pedido.setId(rs.getLong("id"));
                pedido.setEliminado(rs.getBoolean("eliminado"));
                pedido.setNumero(rs.getString("numero"));
                pedido.setFecha(rs.getDate("fecha").toLocalDate());
                pedido.setClienteNombre(rs.getString("clienteNombre"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setEstado(Pedido.EstadoPedido.valueOf(rs.getString("estado")));
                
                Long envioId = rs.getLong("envio");
                if(envioId != null){
                    Envio envio = new Envio();
                    envio.setId(envioId);
                    envio.setEliminado(rs.getBoolean("eliminado"));
                    envio.setTracking(rs.getString("tracking"));
                    envio.setEmpresa(Envio.EmpresaEnvio.valueOf(rs.getString("empresa")));
                    envio.setTipo(Envio.TipoEnvio.valueOf(rs.getString("tipo")));
                    envio.setCosto(rs.getDouble("costo"));
                    envio.setFechaDespacho(rs.getDate("fechaDespacho").toLocalDate());
                    envio.setFechaEstimada(rs.getDate("fechaEstimada").toLocalDate());
                    envio.setEstado(Envio.EstadoEnvio.valueOf(rs.getString("estado")));
                    pedido.setEnvio(envio);
                }
                
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
            stmt.setDate(2, entidad.getFecha() != null ? Date.valueOf(entidad.getFecha()) : null);
            stmt.setString(3, entidad.getClienteNombre());
            stmt.setDouble(4, entidad.getTotal());
            stmt.setString(5, entidad.getEstado().name());
            stmt.setObject(6, entidad.getEnvio() != null ? entidad.getEnvio().getId() : null);

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