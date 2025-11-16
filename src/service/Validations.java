
package service;

import entities.Envio;
import entities.Pedido;

/**
 *
 * @author kari
 */
public class Validations {
    public Validations() {
    }

    protected void validarEnvio(Envio envio, String operacion) {
        if(envio == null) 
            throw new IllegalArgumentException("El envio no puede ser nulo");
        
        if (envio.getTracking() == null || envio.getTracking().trim().isEmpty() || envio.getTracking().length() > 40)
            throw new IllegalArgumentException("El id de tracking es requerido y no puede tener más de 40 caracteres");        
               
        if (envio.getEmpresa() == null)
            throw new IllegalArgumentException("La empresa es requerida");
        
        if (envio.getTipo() == null)
            throw new IllegalArgumentException("El tipo de envio es requerido");
        
        if (envio.getCosto() < 0)
            throw new IllegalArgumentException("El costo no puede ser negativo");
        
        if (envio.getEstado() == null) {
            // Si el estado no es enviado al insertar un nuevo registro, se setea por defecto en EN_PREPARACION
            if (operacion.equals("insertar")) {
                envio.setEstado(Envio.EstadoEnvio.EN_PREPARACION);
            }
            // Si el estado no es enviado al actualizar un registro, se lanza una excepción
            else if (operacion.equals("actualizar")) {
                throw new IllegalArgumentException("El estado es requerido");
            }
        }
    }

    protected void validarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo");
        }

        if (pedido.getNumero() == null || pedido.getNumero().isEmpty()) {
            throw new IllegalArgumentException("El número del pedido es requerido");
        }
        else if (pedido.getNumero().length() > 20) {
            throw new IllegalArgumentException("El número del pedido no puede tener más de 20 caracteres");
        }

        if (pedido.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del pedido es requerida");
        }

        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede ser negativo");
        }

        if (pedido.getEstado() == null) {
            throw new IllegalArgumentException("El estado del pedido es requerido");
        }

        if (pedido.getClienteNombre() == null || pedido.getClienteNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es requerido");
        }
        else if (pedido.getClienteNombre().length() > 120) {
            throw new IllegalArgumentException("El nombre del cliente no puede tener más de 120 caracteres");
        }
    }
}
