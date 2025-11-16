package service;

import config.DatabaseConnection;
import config.TransactionManager;
import java.sql.Connection;
import entities.Pedido;
import entities.Envio;
import dao.GenericDao;
import dao.PedidoDao;
import dao.EnvioDao;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author kari
 */
public class PedidoService implements GenericService<Pedido>{
    private final PedidoDao pedidoDao;
    private final GenericDao<Envio> envioDao;
    private final Validations validations;

    public PedidoService() {
        this.pedidoDao = new PedidoDao();
        this.envioDao = new EnvioDao();
        this.validations = new Validations();
    }

    @Override
    public void insertar(Pedido pedido) throws Exception {
        // se valida el pedido antes de insertarlo en DB
        validations.validarPedido(pedido);

        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            
            // Si existe una pedido secundaria, se inserta y se asigna al pedido
            if (pedido.getEnvio() != null) {
                Envio envio = pedido.getEnvio();
                validations.validarEnvio(envio, "insertar");
                envioDao.crear(envio, tm.getConnection());
                pedido.setEnvio(envio);
            }

            // Se inserta la pedido principal
            pedidoDao.crear(pedido, tm.getConnection());

            tm.commit();
        } catch (Exception e) {
            throw new Exception("INSERT ERROR SERVICE PEDIDO - " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Pedido pedido) throws Exception {
        // se valida el pedido antes de actualizarlo en DB
        validations.validarPedido(pedido);

        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            
            // Verificar si el envío ya está asociado a otro pedido (regla 1→1)
            if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null) {
                boolean yaAsociado = pedidoDao.envioYaAsociadoAOtroPedido(pedido.getEnvio().getId(), pedido.getId(), tm.getConnection());
                
                if (yaAsociado) {
                    throw new Exception("El envío con ID " + pedido.getEnvio().getId() + " ya está asociado a otro pedido");
                }
            }
            
            boolean actualizado = pedidoDao.actualizar(pedido, tm.getConnection());
            
            if (!actualizado) {
                throw new Exception("No se pudo actualizar el pedido. Puede que no exista o esté eliminado.");
            }
            
            tm.commit();
        } catch (Exception e) {
            throw new Exception("UPDATE ERROR SERVICE PEDIDO - " + e.getMessage(), e);
        }
    }

    @Override
    public Pedido obtenerPorId(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Optional<Pedido> pedido = pedidoDao.leer(id, conn);
            if (pedido.isPresent()) {
                return pedido.get();
            } else {
                throw new Exception("No se encontró el pedido con ID: " + id);
            }
        } catch (Exception e) {
            throw new Exception("GET BY ID ERROR SERVICE PEDIDO - " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> obtenerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Pedido> pedidos = pedidoDao.leerTodos(conn);
            if (pedidos.isEmpty()) {
                throw new Exception("No se encontraron pedidos");
            } else {
                return pedidos;
            }
        } catch (Exception e) {
            throw new Exception("GET ALL ERROR SERVICE PEDIDO - " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            boolean eliminado = pedidoDao.eliminar(id, tm.getConnection());
            // Si el pedido no existe, se lanza una excepción
            if (!eliminado) {
                throw new Exception("No se encontró el pedido con ID: " + id);
            }
            tm.commit();
        } catch (Exception e) {
            throw new Exception("DELETE ERROR SERVICE PEDIDO - " + e.getMessage(), e);
        }
    }
}
