/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.DatabaseConnection;
import config.TransactionManager;
import java.sql.Connection;
import entities.Envio;
import dao.GenericDao;
import dao.EnvioDao;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author kari
 */
public class EnvioService implements GenericService<Envio>{
    
    private final GenericDao<Envio> envioDao;
    private final Validations validations;

    public EnvioService() {
        this.envioDao = new EnvioDao();
        this.validations = new Validations();
    }

    @Override
    public void insertar(Envio envio) throws Exception {
        // se valida el envio antes de insertarlo en DB
        validations.validarEnvio(envio, "insertar");
        
        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            envioDao.crear(envio, tm.getConnection());
            tm.commit();
        } catch (Exception e) {
            // El TransactionManager hará rollback automáticamente en close() si hay una transacción activa           
            throw new Exception("INSERT ERROR SERVICE ENVIO - " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Envio envio) throws Exception {
        // se valida el envio antes de actualizarlo en DB
        validations.validarEnvio(envio, "actualizar");
        
        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            boolean actualizado = envioDao.actualizar(envio, tm.getConnection());
            
            if (!actualizado) {
                throw new Exception("No se pudo actualizar el envío. Puede que no exista o esté eliminado.");
            }

            tm.commit();
        } catch (Exception e) {
            throw new Exception("UPDATE ERROR SERVICE ENVIO - " + e.getMessage(), e);
        }
    }

    @Override
    public Envio obtenerPorId(Long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {            
            Optional<Envio> envio = envioDao.leer(id, conn);
            // Si el envío existe, se retorna
            if (envio.isPresent()) {
                return envio.get();
            } else {
                // Si el envío no existe, se lanza una excepción
                throw new Exception("No se encontró el envío con ID: " + id);
            }
        } catch (Exception e) {
            throw new Exception("GET BY ID ERROR SERVICE ENVIO - " + e.getMessage(), e);
        }
    }

    @Override
    public List<Envio> obtenerTodos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Envio> envios = envioDao.leerTodos(conn);
            // Si no hay envíos, se lanza una excepción
            if (envios.isEmpty()) {
                throw new Exception("No se encontraron envíos");
            } else {
                // Si hay envíos, se retornan
                return envios;
            }
        } catch (Exception e) {
            throw new Exception("GET ALL ERROR SERVICE ENVIO - " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        try (TransactionManager tm = new TransactionManager(DatabaseConnection.getConnection())) {
            tm.startTransaction();
            boolean eliminado = envioDao.eliminar(id, tm.getConnection());
            // Si el envío no existe, se lanza una excepción
            if (!eliminado) {
                throw new Exception("No se encontró el envío con ID: " + id);
            }
            tm.commit();
        } catch (Exception e) {
            throw new Exception("DELETE ERROR SERVICE ENVIO - " + e.getMessage(), e);
        }
    }
    
    
    
}
