package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import entities.Pedido;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.sql.SQLException;
import java.sql.Date;
import entities.Envio;
// Importamos solo lo necesario para la lógica SQL, NO DatabaseConnection

// La clase PedidoDAO asume que se le pasa la conexión desde el exterior.
public class PedidoDao implements GenericDao<Pedido>{
    private final EnvioDao envioDao;
    
    public PedidoDao() {
        this.envioDao = new EnvioDao();
    }
    
    @Override
    public boolean crear(Pedido entidad, Connection conn) throws Exception {
        
        String sql = "INSERT INTO pedidos (eliminado, numero, fecha, clienteNombre, total, estado, envio) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBoolean(1, entidad.getEliminado());
            stmt.setString(2, entidad.getNumero());
            stmt.setDate(3, entidad.getFecha() != null ? Date.valueOf(entidad.getFecha()) : null);
            stmt.setString(4, entidad.getClienteNombre());
            stmt.setDouble(5, entidad.getTotal());
            stmt.setString(6, entidad.getEstado().name());
            stmt.setObject(7, entidad.getEnvio() != null ? entidad.getEnvio().getId() : null);

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
            throw new Exception("Error DAO al insertar el pedido: " + e.getMessage(), e);
        }

    }

    /**
     * Devuelve un objeto pedido wrapeado en un objeto Optional.
     * @param id
     * @param conn
     * @return 
     */
    @Override
    public Optional<Pedido> leer(long id, Connection conn) throws Exception {
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
                
                long envioId = rs.getLong("envio");
                if(!rs.wasNull() && envioId != 0){
                    // Obtener el envío completo usando EnvioDao
                    Optional<Envio> envioOpt = envioDao.leer(envioId, conn);
                    if(envioOpt.isPresent()){
                        pedido.setEnvio(envioOpt.get());
                    }
                }
                
            }
            else{
                System.out.println("No encontrado");
            }
        } catch (Exception e) {
            throw new Exception("Error DAO al buscar el pedido: " + e.getMessage(), e);
        }
        return Optional.ofNullable(pedido);
    }

    @Override
    public List<Pedido> leerTodos(Connection conn) throws Exception {
        List<Pedido> listaPedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE eliminado = 0";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Pedido pedido = new Pedido();
                pedido.setId(rs.getLong("id"));
                pedido.setEliminado(rs.getBoolean("eliminado"));
                pedido.setNumero(rs.getString("numero"));
                pedido.setFecha(rs.getDate("fecha").toLocalDate());
                pedido.setClienteNombre(rs.getString("clienteNombre"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setEstado(Pedido.EstadoPedido.valueOf(rs.getString("estado")));

                long envioId = rs.getLong("envio");
                if(!rs.wasNull() && envioId != 0){
                    // Obtener el envío completo usando EnvioDao
                    Optional<Envio> envioOpt = envioDao.leer(envioId, conn);
                    if(envioOpt.isPresent()){
                        pedido.setEnvio(envioOpt.get());
                    }
                }

                listaPedidos.add(pedido);
            }
        } catch (Exception e) {
            throw new Exception("Error DAO al buscar los pedidos: " + e.getMessage(), e);
        }
        return listaPedidos;
    }

    @Override
    public boolean actualizar(Pedido entidad, Connection conn) throws Exception {

        String sql = "UPDATE pedidos SET numero = ?, fecha = ?, clienteNombre = ?, total = ?, estado = ?, envio = ? WHERE id = ? AND eliminado = 0";

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
            //System.err.println("Error DAO al actualizar el pedido: " + e.getMessage());
            throw new Exception("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(long id, Connection conn) throws Exception {

        // Baja logica es con 1
        String sql = "UPDATE pedidos SET eliminado = 1 WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id); 

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas == 1; 

        } catch (SQLException e) {
            //System.err.println("Error DAO al eliminar (baja lógica) el pedido: " + e.getMessage());
            throw new Exception("Error al marcar el pedido como eliminado.", e);
        }
    }
    
    // Verifica si un envío ya está asociado a otro pedido (diferente al pedido actual)
    public boolean envioYaAsociadoAOtroPedido(Long envioId, Long pedidoId, Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE envio = ? AND id != ? AND eliminado = 0";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, envioId);
            stmt.setLong(2, pedidoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar si el envío está asociado a otro pedido: " + e.getMessage(), e);
        }
        
        return false;
    }

    public Optional<Pedido> buscarPorNumero(String tracking, Connection conn){
        Pedido pedido=null;
        String sql = "SELECT * from pedidos WHERE numero = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, tracking);
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()){
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
                System.out.println("Tracking No encontrado");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar por numero de tracking, "+ e.getMessage());
        }
        return Optional.of(pedido);
    }
}