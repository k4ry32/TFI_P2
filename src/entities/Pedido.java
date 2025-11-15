package entities;

import java.time.LocalDate;

public class Pedido {

    private Long id;
    private boolean eliminado;
    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private double total;
    private EstadoPedido estado;
    private Envio envio;

    // Constructores
    public Pedido() {
    }

    public Pedido(Long id, Boolean eliminado, String numero, LocalDate fecha,
            String clienteNombre, double total, EstadoPedido estado, Envio envio) {
        this.id = id;
        this.eliminado = eliminado;
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    // ToString
    @Override
    public String toString() {
        return "Pedido{" + "id=" + id
                + ", eliminado=" + eliminado
                + ", numero=" + numero
                + ", fecha=" + fecha
                + ", clienteNombre=" + clienteNombre
                + ", total=" + total
                + ", estado=" + estado
                + ", envio=" + envio + '}';
    }

    // Enum
    public enum EstadoPedido {
        NUEVO, FACTURADO, ENVIADO;
    }
    
    public boolean tieneEnvio() {
        return envio != null;       // Verifica si el pedido tiene envio asociado.
    }
}