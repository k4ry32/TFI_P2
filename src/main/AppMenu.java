/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import entities.*;
import entities.Pedido.EstadoPedido;
import entities.Envio.*;
import service.PedidoService;
import service.EnvioService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Etapa 4: Interfaz de Usuario y Entregables Finales
 * Clase AppMenu - Interfaz de usuario por consola 
 * Esta clase maneja toda la interacción con el usuario a través de un menú
 * de consola, permitiendo realizar operaciones CRUD sobre Pedidos y Envíos.
 * 
 * @author Bruno Maza
 */
public class AppMenu {
    
    // Variables estáticas compartidas por todos los métodos
    private static Scanner scanner = new Scanner(System.in);
    private static PedidoService pedidoService = new PedidoService();
    private static EnvioService envioService = new EnvioService();
    
    /**
     * Método principal que inicia el menú y mantiene un ciclo hasta que el usuario decida salir
     */
    public static void iniciar() {
        boolean continuar = true;
        
        System.out.println("Bienvenido al Sistema de Gestión de Pedidos y Envíos");
        
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    crearPedidoCompleto();
                    break;
                case 2:
                    buscarPedidoPorId();
                    break;
                case 3:
                    listarTodosPedidos();
                    break;
                case 4:
                    actualizarPedido();
                    break;
                case 5:
                    eliminarPedido();
                    break;
                case 6:
                    buscarPedidoPorNumero();
                    break;
                case 7:
                    crearEnvio();
                    break;
                case 8:
                    listarTodosEnvios();
                    break;
                case 9:
                    buscarEnvioPorTracking();
                    break;
                case 0:
                    continuar = false;
                    System.out.println("¡Gracias por usar nuestro ssitema de envios! Hasta luego.");
                    break;
                default:
                    System.out.println("Opción inválida. Por favor ingrese un número entre 0 y 9.");
            }
            
            // Pausa para que el usuario vea el resultado antes de volver al menú
            if (continuar && opcion != -1) {
                pausar();
            }
        }
        
        scanner.close();
    }
    
   
    private static void mostrarMenuPrincipal() {
        System.out.println("----- SISTEMA DE GESTIÓN DE PEDIDOS Y ENVÍOS -----");
        System.out.println("  GESTIÓN DE PEDIDOS ─────────────────────────────");
        System.out.println("    1. Crear Pedido (con Envío)");
        System.out.println("    2. Buscar Pedido por ID");
        System.out.println("    3. Listar todos los Pedidos");
        System.out.println("    4. Actualizar Pedido");
        System.out.println("    5. Eliminar Pedido (baja lógica)");
        System.out.println("    6. Buscar Pedido por número");
        System.out.println("  ─ GESTIÓN DE ENVÍOS ──────────────────────────────");
        System.out.println("    7. Crear Envío independiente");
        System.out.println("    8. Listar todos los Envíos");
        System.out.println("    9. Buscar Envío por tracking");
        System.out.println("    0. Salir del sistema");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print(" Seleccione una opción: ");
    }
    
    /**
     * Lee la opción ingresada por el usuario
     * Maneja el error si el usuario ingresa texto en lugar de número
     * Retorna un número de opción válido o -1 si hubo error
     */
    private static int leerOpcion() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    private static void pausar() {
        System.out.println("\n[Presione Enter para continuar...]");
        scanner.nextLine();
    }
    
    
    //MÉTODOS CRUD - PEDIDOS
    
    /**
     * Crea un nuevo pedido completo con su envío asociado
     * Implementa la relación 1→1 unidireccional
     */
    private static void crearPedidoCompleto() {
        System.out.println("    CREAR NUEVO PEDIDO CON ENVÍO       ");
        
        try {
            // Crear el objeto Pedido
            Pedido pedido = new Pedido();
            
            // 1. Número de pedido (UNIQUE - convertir a mayúsculas)
            System.out.print("Número de pedido: ");
            String numero = scanner.nextLine().trim().toUpperCase();
            if (numero.isEmpty()) {
                System.out.println("El número de pedido es obligatorio.");
                return;
            }
            pedido.setNumero(numero);
            
            // 2. Fecha del pedido
            System.out.print("Fecha del pedido (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine().trim();
            try {
                LocalDate fecha = LocalDate.parse(fechaStr);
                pedido.setFecha(fecha);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD (ej: 2024-12-15)");
                return;
            }
            
            // 3. Nombre del cliente (convertir a mayúsculas)
            System.out.print("Nombre del cliente: ");
            String clienteNombre = scanner.nextLine().trim().toUpperCase();
            if (clienteNombre.isEmpty()) {
                System.out.println("El nombre del cliente es obligatorio.");
                return;
            }
            pedido.setClienteNombre(clienteNombre);
            
            // 4. Total del pedido
            System.out.print("Total del pedido: $");
            try {
                double total = Double.parseDouble(scanner.nextLine().trim());
                if (total <= 0) {
                    System.out.println("El total debe ser mayor a 0.");
                    return;
                }
                pedido.setTotal(total);
            } catch (NumberFormatException e) {
                System.out.println("El total debe ser un número válido.");
                return;
            }
            
            // 5. Estado del pedido
            pedido.setEstado(EstadoPedido.NUEVO);
            
            // 6. Crear el envío asociado
            System.out.println("DATOS DEL ENVÍO ASOCIADO");
            Envio envio = crearEnvioInteractivo();
            if (envio == null) {
                return; // Hubo error al crear el envío
            }
            pedido.setEnvio(envio);
            
            // Insertar pedido con envío (transacción)
            pedidoService.insertar(pedido);
            
            System.out.println("Pedido creado exitosamente!");
            System.out.println("   ID: " + pedido.getId());
            System.out.println("   Número: " + pedido.getNumero());
            System.out.println("   Cliente: " + pedido.getClienteNombre());
            System.out.println("   Envío tracking: " + envio.getTracking());
            
        } catch (Exception e) {
            System.out.println("Error al crear el pedido: " + e.getMessage());
        }
    }
    
    /**
     * Método auxiliar para crear un envío de forma interactiva
     * Retorna Envio creado o null si hubo error
     */
    private static Envio crearEnvioInteractivo() {
        try {
            Envio envio = new Envio();
            
            // 1. Tracking (UNIQUE - convertir a mayúsculas)
            System.out.print("Número de tracking: ");
            String tracking = scanner.nextLine().trim().toUpperCase();
            if (tracking.isEmpty()) {
                System.out.println("El número de tracking es obligatorio.");
                return null;
            }
            envio.setTracking(tracking);
            
            // 2. Empresa de envío
            System.out.println("Empresa de envío:");
            System.out.println("  1. ANDREANI");
            System.out.println("  2. OCA");
            System.out.println("  3. CORREO_ARG");
            System.out.print("Seleccione (1-3): ");
            int empresaOpcion = Integer.parseInt(scanner.nextLine().trim());
            EmpresaEnvio empresa;
            switch (empresaOpcion) {
                case 1: empresa = EmpresaEnvio.ANDREANI; break;
                case 2: empresa = EmpresaEnvio.OCA; break;
                case 3: empresa = EmpresaEnvio.CORREO_ARG; break;
                default:
                    System.out.println("Opción de empresa inválida.");
                    return null;
            }
            envio.setEmpresa(empresa);
            
            // 3. Tipo de envío
            System.out.println("?Tipo de envío:");
            System.out.println("  1. ESTANDAR");
            System.out.println("  2. EXPRES");
            System.out.print("Seleccione (1-2): ");
            int tipoOpcion = Integer.parseInt(scanner.nextLine().trim());
            TipoEnvio tipo = (tipoOpcion == 1) ? TipoEnvio.ESTANDAR : TipoEnvio.EXPRES;
            envio.setTipo(tipo);
            
            // 4. Costo
            System.out.print("Costo del envío: $");
            double costo = Double.parseDouble(scanner.nextLine().trim());
            envio.setCosto(costo);
            
            // 5. Fecha de despacho
            System.out.print("Fecha de despacho (YYYY-MM-DD): ");
            String fechaDespStr = scanner.nextLine().trim();
            if (!fechaDespStr.isEmpty()) {
                envio.setFechaDespacho(LocalDate.parse(fechaDespStr));
            }
            
            // 6. Fecha estimada
            System.out.print("Fecha estimada de entrega (YYYY-MM-DD): ");
            String fechaEstStr = scanner.nextLine().trim();
            if (!fechaEstStr.isEmpty()) {
                envio.setFechaEstimada(LocalDate.parse(fechaEstStr));
            }
            
            // 7. Estado del envío
            envio.setEstado(EstadoEnvio.EN_PREPARACION);
            
            return envio;
            
        } catch (Exception e) {
            System.out.println("Error al crear el envío: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca un pedido por su ID
     */
    private static void buscarPedidoPorId() {
        System.out.println("      BUSCAR PEDIDO POR ID             ");
        
        try {
            System.out.print("Ingrese el ID del pedido: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            
            Pedido pedido = pedidoService.obtenerPorId(id);
            
            System.out.println("Pedido encontrado:");
            mostrarPedido(pedido);
            
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.out.println("X" + e.getMessage());
        }
    }
    
    /**
     * Lista todos los pedidos no eliminados
     */
    private static void listarTodosPedidos() {
        System.out.println("      LISTADO DE TODOS LOS PEDIDOS     ");
        
        try {
            List<Pedido> pedidos = pedidoService.obtenerTodos();
            
            System.out.println("Total de pedidos: " + pedidos.size());
            System.out.println("─".repeat(80));
            
            for (Pedido pedido : pedidos) {
                mostrarPedidoResumido(pedido);
                System.out.println("─".repeat(80));
            }
            
        } catch (Exception e) {
            System.out.println("X" + e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de un pedido existente
     */
    private static void actualizarPedido() {
        System.out.println("       ACTUALIZAR PEDIDO               ");
        
        try {
            System.out.print("Ingrese el ID del pedido a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            
            Pedido pedido = pedidoService.obtenerPorId(id);
            
            System.out.println("Datos actuales:");
            mostrarPedido(pedido);
            
            System.out.println(" Ingrese los nuevos datos (Enter para mantener el actual):");
            
            // Actualizar número
            System.out.print("Número [" + pedido.getNumero() + "]: ");
            String numero = scanner.nextLine().trim().toUpperCase();
            if (!numero.isEmpty()) {
                pedido.setNumero(numero);
            }
            
            // Actualizar cliente
            System.out.print("Cliente [" + pedido.getClienteNombre() + "]: ");
            String cliente = scanner.nextLine().trim().toUpperCase();
            if (!cliente.isEmpty()) {
                pedido.setClienteNombre(cliente);
            }
            
            // Actualizar total
            System.out.print("Total [" + pedido.getTotal() + "]: $");
            String totalStr = scanner.nextLine().trim();
            if (!totalStr.isEmpty()) {
                pedido.setTotal(Double.parseDouble(totalStr));
            }
            
            // Actualizar estado
            System.out.println("Estado actual: " + pedido.getEstado());
            System.out.println("1. NUEVO  2. FACTURADO  3. ENVIADO");
            System.out.print("Nuevo estado (1-3, Enter para mantener): ");
            String estadoStr = scanner.nextLine().trim();
            if (!estadoStr.isEmpty()) {
                int estadoOpcion = Integer.parseInt(estadoStr);
                switch (estadoOpcion) {
                    case 1: pedido.setEstado(EstadoPedido.NUEVO); break;
                    case 2: pedido.setEstado(EstadoPedido.FACTURADO); break;
                    case 3: pedido.setEstado(EstadoPedido.ENVIADO); break;
                }
            }
            
            pedidoService.actualizar(pedido);
            System.out.println("Pedido actualizado exitosamente!");
            
        } catch (NumberFormatException e) {
            System.out.println("Formato de número inválido.");
        } catch (Exception e) {
            System.out.println("X" + e.getMessage());
        }
    }
    
    /**
     * Realiza la baja lógica de un pedido
     */
    private static void eliminarPedido() {
        System.out.println("    ELIMINAR PEDIDO (Baja Lógica)      ");
        
        try {
            System.out.print("Ingrese el ID del pedido a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            
            Pedido pedido = pedidoService.obtenerPorId(id);
            mostrarPedidoResumido(pedido);
            
            System.out.print("¿Está seguro de eliminar este pedido? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if (confirmacion.equals("S")) {
                pedidoService.eliminar(id);
                System.out.println("Pedido eliminado exitosamente (baja lógica).");
            } else {
                System.out.println("Operación cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.out.println("X" + e.getMessage());
        }
    }
    
    /**
     * Busca un pedido por su número único
     */
    private static void buscarPedidoPorNumero() {
        System.out.println("      BUSCAR PEDIDO POR NÚMERO         ");
        
        try {
            System.out.print("Ingrese el número de pedido: ");
            String numero = scanner.nextLine().trim().toUpperCase();
            
            if (numero.isEmpty()) {
                System.out.println("Debe ingresar un número de pedido.");
                return;
            }
            
            Pedido pedido = pedidoService.buscarPorNumero(numero);
            
            System.out.println("Pedido encontrado:");
            mostrarPedido(pedido);
            
        } catch (Exception e) {
            System.out.println("X " + e.getMessage());
        }
    }
     
    //MÉTODOS CRUD - ENVÍOS
    
    /**
     * Crea un envío independiente
     */
    private static void crearEnvio() {
        System.out.println("     CREAR ENVÍO INDEPENDIENTE         ");
        
        try {
            Envio envio = crearEnvioInteractivo();
            if (envio == null) {
                return;
            }
            
            envioService.insertar(envio);
            
            System.out.println("Envío creado exitosamente!");
            System.out.println("   ID: " + envio.getId());
            System.out.println("   Tracking: " + envio.getTracking());
            System.out.println("   Empresa: " + envio.getEmpresa());
            
        } catch (Exception e) {
            System.out.println("X " + e.getMessage());
        }
    }
    
    /**
     * Lista todos los envíos no eliminados
     */
    private static void listarTodosEnvios() {
        System.out.println("      LISTADO DE TODOS LOS ENVÍOS      ");
        
        try {
            List<Envio> envios = envioService.obtenerTodos();
            
            System.out.println("Total de envíos: " + envios.size());
            System.out.println("─".repeat(80));
            
            for (Envio envio : envios) {
                mostrarEnvioResumido(envio);
                System.out.println("─".repeat(80));
            }
            
        } catch (Exception e) {
            System.out.println("X " + e.getMessage());
        }
    }
    
    /**
     * Busca un envío por su tracking
     */
    private static void buscarEnvioPorTracking() {
        System.out.println("      BUSCAR ENVÍO POR TRACKING        ");
        try {
            System.out.print("Ingrese el número de tracking: ");
            String tracking = scanner.nextLine().trim().toUpperCase();
            
            if (tracking.isEmpty()) {
                System.out.println("Debe ingresar un número de tracking.");
                return;
            }
            
            Envio envio = envioService.buscarPorTracking(tracking);
            
            System.out.println("Envío encontrado:");
            mostrarEnvio(envio);
            
        } catch (Exception e) {
            System.out.println("X " + e.getMessage());
        }
    }
    
    //MÉTODOS AUXILIARES DE VISUALIZACIÓN
    
    /**
     * Muestra los datos completos de un pedido
     */
    private static void mostrarPedido(Pedido pedido) {
        System.out.println("  ID: " + pedido.getId());
        System.out.println("  Número: " + pedido.getNumero());
        System.out.println("  Fecha: " + pedido.getFecha());
        System.out.println("  Cliente: " + pedido.getClienteNombre());
        System.out.println("  Total: $" + String.format("%.2f", pedido.getTotal()));
        System.out.println("  Estado: " + pedido.getEstado());
        
        if (pedido.getEnvio() != null) {
            System.out.println("Envío asociado:");
            System.out.println("     Tracking: " + pedido.getEnvio().getTracking());
            System.out.println("     Empresa: " + pedido.getEnvio().getEmpresa());
            System.out.println("     Estado: " + pedido.getEnvio().getEstado());
        } else {
            System.out.println("Sin envío asociado");
        }
    }
    
    /**
     * Muestra un resumen de un pedido
     */
    private static void mostrarPedidoResumido(Pedido pedido) {
        System.out.printf("ID: %-5d | Nro: %-15s | Cliente: %-30s | Total: $%-10.2f | Estado: %s%n",
                pedido.getId(),
                pedido.getNumero(),
                pedido.getClienteNombre(),
                pedido.getTotal(),
                pedido.getEstado());
    }
    
    /**
     * Muestra los datos completos de un envío
     */
    private static void mostrarEnvio(Envio envio) {
        System.out.println("  ID: " + envio.getId());
        System.out.println("  Tracking: " + envio.getTracking());
        System.out.println("  Empresa: " + envio.getEmpresa());
        System.out.println("  Tipo: " + envio.getTipo());
        System.out.println("  Costo: $" + String.format("%.2f", envio.getCosto()));
        System.out.println("  Estado: " + envio.getEstado());
        
        if (envio.getFechaDespacho() != null) {
            System.out.println("  Fecha despacho: " + envio.getFechaDespacho());
        }
        
        if (envio.getFechaEstimada() != null) {
            System.out.println("  Fecha estimada: " + envio.getFechaEstimada());
        }
    }
    
    /**
     * Muestra un resumen de un envío
     */
    private static void mostrarEnvioResumido(Envio envio) {
        System.out.printf("ID: %-5d | Tracking: %-20s | Empresa: %-15s | Estado: %-20s | Costo: $%.2f%n",
                envio.getId(),
                envio.getTracking(),
                envio.getEmpresa(),
                envio.getEstado(),
                envio.getCosto());
    }
}