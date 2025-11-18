# Sistema de Gestión de Pedidos y Envíos

## Trabajo Final de Integración II - Programación 2

### Autores - Comisión 4
- Gaston Paschetta
- Javier Ovelar
- Bruno Maza
- Karina Rodriguez

Enlace al video:

**Institución**: Tecnicatura Universitaria en Programación - Universidad Tecnológica Nacional

---

### Descripción del Proyecto

Este Trabajo Final de Integración tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos, Persistencia de Datos y Transacciones aprendidos durante el cursado de Programación 2. El proyecto consiste en desarrollar un sistema completo de gestión de pedidos y sus envíos asociados que permita realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta con soporte transaccional.

### Objetivos Académicos

El desarrollo de este sistema permite aplicar y consolidar los siguientes conceptos clave de la materia:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en capas diferenciadas
- Capa de Presentación (Main/AppMenu): Interacción con el usuario mediante consola
- Capa de Lógica de Negocio (Service): Validaciones, reglas de negocio y orquestación de transacciones
- Capa de Acceso a Datos (DAO): Operaciones de persistencia con JDBC
- Capa de Modelo (Entities): Representación de entidades del dominio (Pedido, Envio, Enums)
- Capa de Configuración (Config): Gestión de conexiones y transacciones

**2. Programación Orientada a Objetos**
- Aplicación de principios SOLID (Single Responsibility, Dependency Injection)
- Uso de interfaces genéricas (GenericDao, GenericService)
- Encapsulamiento con atributos privados y métodos de acceso
- Uso de enumeraciones para tipos de datos (TipoEnvio, EstadoEnvio, EmpresaEnvio, EstadoPedido)
- Sobrescritura de métodos (toString)

**3. Persistencia de Datos con JDBC y Relaciones**
- Conexión a base de datos MySQL mediante JDBC
- Implementación del patrón DAO (Data Access Object)
- Uso de PreparedStatements para prevenir SQL Injection
- **Relación 1→1 unidireccional**: Pedido contiene referencia a Envío
- Implementación mediante **Clave Foránea Única (FK Única)**: columna `envio` en tabla `pedidos` con restricción UNIQUE
- Manejo de claves autogeneradas (AUTO_INCREMENT)
- Validación de integridad referencial (FOREIGN KEY)

**4. Gestión de Transacciones (Característica Principal del TFI)**
- **Operación atómica crítica**: Creación de Pedido + Envío debe ser indivisible
- Uso de `setAutoCommit(false)` para iniciar transacciones
- Confirmación con `commit()` si todas las operaciones tienen éxito
- Reversión con `rollback()` si alguna operación falla
- Implementación de `TransactionManager` con AutoCloseable
- Orquestación transaccional en la capa Service

**5. Manejo de Recursos y Excepciones**
- Uso del patrón try-with-resources para gestión automática de recursos JDBC
- Implementación de AutoCloseable en TransactionManager
- Manejo apropiado de excepciones con propagación controlada
- Validación multi-nivel: base de datos y aplicación

**6. Patrones de Diseño**
- Factory Pattern (DatabaseConnection)
- Service Layer Pattern (separación lógica de negocio)
- DAO Pattern (abstracción del acceso a datos)
- Transaction Management Pattern (TransactionManager)
- Dependency Injection manual

**7. Validación de Integridad de Datos**
- Validación de campos obligatorios en múltiples niveles
- Validación de unicidad (número de pedido, tracking de envío)
- Validación de valores positivos (total de pedido >= 0, costo de envío >= 0)
- Validación de formato de fecha (YYYY-MM-DD)
- Validación de integridad referencial mediante Foreign Keys

### Dominio Elegido: Pedido → Envío

Para el desarrollo del TFI se eligió la pareja **Pedido → Envío**, donde la clase Pedido contiene la referencia unidireccional **1→1** a la clase Envío.

**Justificación de la Elección**:

Esta elección se fundamenta en la naturaleza atómica de la operación de creación, que requiere **obligatoriamente** el uso de transacciones en la capa de persistencia.

En un escenario de comercio electrónico, cuando un cliente finaliza una compra se inicia una operación que no puede dividirse sin comprometer la integridad del sistema:

1. **Crear el registro Envío** y obtener su ID generado
2. **Crear el registro Pedido** asociando el ID del Envío recién creado

Si alguna de estas operaciones falla y no se utilizan transacciones, la base de datos quedaría en un **estado inconsistente** (por ejemplo, un envío sin pedido asociado).

**Solución Implementada**: Uso de transacciones en la capa Service mediante `setAutoCommit(false)`, ejecutando `commit()` si ambas operaciones tienen éxito, o `rollback()` si alguna falla.

### Diseño de la Relación 1→1

Se implementó la relación 1→1 mediante **Clave Foránea Única (FK Única)**:

| Tabla | Clave Primaria (PK) | Restricción de Relación |
|-------|---------------------|------------------------|
| `envios` (B) | `id` (bigint, autoincremental) | tracking UNIQUE, costo CHECK >= 0 |
| `pedidos` (A) | `id` (bigint, autoincremental) | numero UNIQUE NOT NULL, total CHECK >= 0, **envio FOREIGN KEY UNIQUE** |

La columna `envio` en la tabla `pedidos`:
1. Es una **FOREIGN KEY** que referencia `envios(id)` → Garantiza integridad referencial
2. Tiene restricción **UNIQUE** → Impide que dos pedidos referencien el mismo envío (garantiza 1→1)

## Características Principales

- **Gestión de Pedidos**: Crear, listar, actualizar y eliminar pedidos con validación de número único
- **Gestión de Envíos**: Administrar envíos de forma independiente o asociados a pedidos
- **Operación Transaccional**: Creación atómica de Pedido + Envío con rollback automático en caso de error
- **Validación Robusta**: Validaciones en múltiples capas (Service y Base de Datos)
- **Seguridad**: Protección contra SQL injection mediante PreparedStatements
- **Estados Configurables**: Enumeraciones para tipos de envío, empresas, y estados
- **Búsqueda Inteligente**: Filtrar pedidos por ID, número, cliente o rango de fechas

## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 8 o superior |
| MySQL | 8.0 o superior |
| MySQL Connector/J | 8.2.0 o superior (incluido en `lib/`) |
| Sistema Operativo | Windows, Linux o macOS |

## Instalación y Ejecución

Para instrucciones detalladas sobre cómo instalar, configurar y ejecutar el sistema, consultar el archivo:

📋 **[INSTALACION.md](INSTALACION.md)**

Este documento incluye:
- Configuración de la base de datos (manual y con Docker)
- Configuración del driver MySQL
- Configuración de credenciales (`db.properties`)
- Población de datos de prueba
- Scripts de compilación y ejecución (`compile.sh` y `run.sh`)
- Comandos útiles para MySQL
- Solución de problemas comunes

## Uso del Sistema

### Menú Principal

```
========= MENÚ PRINCIPAL =========
1. CREAR NUEVO PEDIDO CON ENVÍO
2. BUSCAR PEDIDO POR ID
3. LISTAR TODOS LOS PEDIDOS
4. ACTUALIZAR PEDIDO
5. ELIMINAR PEDIDO
6. BUSCAR ENVÍO POR ID
7. LISTAR TODOS LOS ENVÍOS
8. ACTUALIZAR ENVÍO
9. ELIMINAR ENVÍO
0. SALIR
==================================
```

### Operaciones Principales

#### 1. Crear Nuevo Pedido con Envío (Operación Transaccional)

Esta es la operación más importante del sistema, ya que demuestra el uso de **transacciones**.

**Flujo:**
1. Solicita datos del pedido (número, fecha, cliente, total, estado)
2. Valida los datos del pedido
3. Solicita datos del envío (tracking, empresa, tipo, costo, fechas, estado)
4. Valida los datos del envío
5. **Inicia transacción** (`setAutoCommit(false)`)
6. Inserta el envío en la BD y obtiene su ID
7. Asocia el ID del envío al pedido
8. Inserta el pedido en la BD
9. Si todo es exitoso: **confirma transacción** (`commit()`)
10. Si hay error: **revierte transacción** (`rollback()`)

**Ejemplo de creación exitosa:**
```
Número de pedido: PED000001
Fecha del pedido (YYYY-MM-DD): 2025-10-10
Nombre del cliente: Pepe Argento
Total del pedido: $45000

DATOS DEL ENVÍO ASOCIADO
Número de tracking: PED000000001
Empresa de envío:
  1. OCA
  2. CORREO_ARG
Seleccione (1-2): 2
Tipo de envío:
  1. ESTANDAR
  2. EXPRES
Seleccione (1-2): 1
Costo del envío: $9900
Fecha de despacho (YYYY-MM-DD): 1990-12-13

✓ Pedido y envío creados exitosamente
```

**Ejemplo de rollback automático:**
```
Número de pedido: PED000000001
Fecha del pedido (YYYY-MM-DD): 2025-12-13
Nombre del cliente: Pepe Argento
Total del pedido: $45000

DATOS DEL ENVÍO ASOCIADO
Número de tracking: PED000000001
[...]

✗ Transacción activa detectada al cerrar la conexión - ejecutando rollback automático
✗ Error al crear el pedido: INSERT ERROR SERVICE PEDIDO - Error DAO al insertar el envío: 
   Duplicate entry 'PED000000001' for key 'envios.tracking'
```

#### 2. Buscar Pedido por ID

Permite consultar un pedido específico y ver toda su información, incluyendo el envío asociado.

**Ejemplo:**
```
Ingrese el ID del pedido: 1

ID: 1 | Nro: PED000000001 | Cliente: Luis López | Total: $58,02 | Estado: FACTURADO
???????????????????????
ID: 1 | Nro: PED000000001 | Cliente: Luis López | Total: $58,02 | Estado: FACTURADO
```

#### 3. Listar Todos los Pedidos

Muestra todos los pedidos activos (no eliminados) del sistema con su información básica.

**Ejemplo:**
```
Total de pedidos: 50001
???????????????????????
ID: 1   | Nro: PED000000001  | Cliente: Luis López      | Total: $58,02   | Estado: FACTURADO
ID: 2   | Nro: PED000000002  | Cliente: María Díaz      | Total: $60,54   | Estado: FACTURADO
[...]
```

#### 4. Actualizar Pedido

Permite modificar los datos de un pedido existente. Los campos que se dejan en blanco mantienen su valor actual.

**Ejemplo:**
```
Ingrese el ID del pedido a actualizar: 1

Datos actuales:
ID: 1 | Nro: PED000000001 | Cliente: Luis López | Total: $58,02 | Estado: FACTURADO

Nuevo número de pedido (Enter para mantener): 
Fecha del pedido YYYY-MM-DD (Enter para mantener): 
Nombre del cliente (Enter para mantener): Luis López García
Total del pedido (Enter para mantener): $65000
Estado del pedido:
  1. NUEVO_PEDIDO
  2. ENVIADO
  3. CORREO_ARG
[...]

✓ Pedido actualizado exitosamente
```

#### 5. Eliminar Pedido

Elimina lógicamente un pedido (marca como eliminado sin borrar físicamente de la BD).

**Ejemplo:**
```
Ingrese el ID del pedido a eliminar: 50
¿Está seguro que desea eliminar este pedido? (s/n): s

✓ Pedido eliminado exitosamente
```

#### 6-9. Operaciones sobre Envíos

Similar a las operaciones de pedidos, permite buscar, listar, actualizar y eliminar envíos de forma independiente.

## Arquitectura del Sistema

### Diagrama UML

El sistema está diseñado con una arquitectura en capas:

```
main
  ↓
AppMenu
  ↓
service (PedidoService, EnvioService)
  ↓
dao (PedidoDao, EnvioDao)
  ↓
config (DatabaseConnection, TransactionManager)
  ↓
MySQL Database (envios, pedidos)
```

**Flujo de una operación transaccional:**

1. **Main** → Inicia la aplicación
2. **AppMenu** → Muestra menú y captura entrada del usuario
3. **PedidoService.insertar()**:
   - Valida datos del pedido y envío
   - Abre conexión con `DatabaseConnection.getConnection()`
   - Inicia transacción con `TransactionManager.startTransaction()`
   - Llama a `EnvioDao.crearenvio()` para insertar envío
   - Obtiene el ID del envío recién creado
   - Asocia el ID al objeto Pedido
   - Llama a `PedidoDao.crearpedido()` para insertar pedido
   - Si todo OK: `TransactionManager.commit()`
   - Si hay error: `TransactionManager.rollback()` (automático en close())
4. **DatabaseConnection** → Gestiona conexiones a MySQL
5. **MySQL** → Ejecuta las sentencias SQL y devuelve resultados

### Capas del Sistema

#### 1. Capa de Presentación (main)
- **Main.java**: Punto de entrada de la aplicación
- **AppMenu.java**: Gestiona la interacción con el usuario, muestra menús y captura opciones

#### 2. Capa de Servicio (service)
- **GenericService\<T\>**: Interfaz genérica con operaciones CRUD
- **PedidoService**: Lógica de negocio para pedidos, validaciones y **orquestación de transacciones**
- **EnvioService**: Lógica de negocio para envíos y validaciones

#### 3. Capa de Acceso a Datos (dao)
- **GenericDao\<T\>**: Interfaz genérica con métodos de persistencia
- **PedidoDao**: Operaciones CRUD sobre tabla `pedidos`
- **EnvioDao**: Operaciones CRUD sobre tabla `envios`

#### 4. Capa de Entidades (entities)
- **Pedido**: Entidad con atributos: id, eliminado, numero, fecha, clienteNombre, total, estadoPedido, **envio (Envio)**
- **Envio**: Entidad con atributos: id, eliminado, tracking, empresa, tipo, costo, fechaDespacho, estado

#### 5. Capa de Configuración (config)
- **DatabaseConnection**: Factory para obtener conexiones a MySQL
- **TransactionManager**: Gestiona el ciclo de vida de transacciones (start, commit, rollback, close)

#### 6. Capa de Utilidades (util)
- **DatabaseSeeder**: Clase para poblar la base de datos con datos de prueba

## Validaciones y Reglas de Negocio

### Validaciones para Envío

La clase `Validations.java` define las siguientes reglas para envíos:

- El objeto Envío **no puede ser nulo**
- El **código de tracking es obligatorio**, longitud máxima de 40 caracteres
- La **empresa debe ser obligatoria** (valores del enum EmpresaEnvio)
- El **tipo de envío es obligatorio** (valores del enum TipoEnvio)
- El **costo del envío no puede ser menor a 0**
- El **estado** por defecto es "EN_PREPARACION" si no se especifica (valores del enum EstadoEnvio)

### Validaciones para Pedido

La clase `Validations.java` define las siguientes reglas para pedidos:

- El objeto Pedido **no puede ser nulo**
- El **número de pedido es obligatorio**, longitud máxima de 20 caracteres
- La **fecha de pedido es obligatoria**, debe tener formato **YYYY-MM-DD**
- El **total del pedido debe ser mayor a 0**
- El **estado del pedido es obligatorio** (valores del enum EstadoPedido)
- El **nombre del cliente es obligatorio**, longitud máxima de 120 caracteres
- El **envío asociado debe existir** (referencia válida a un objeto Envio)

**En caso de violación**: Se lanza `IllegalArgumentException` con mensaje descriptivo del error.

### Restricciones de Base de Datos

Además de las validaciones en Java, la base de datos aplica:

- **UNIQUE**: Garantiza unicidad de `numero` en pedidos y `tracking` en envíos
- **NOT NULL**: Campos obligatorios
- **CHECK**: Valida que `total >= 0` y `costo >= 0`
- **FOREIGN KEY**: Garantiza integridad referencial (cada `envio` en pedidos debe existir en tabla envios)
- **UNIQUE en FK**: Garantiza relación 1→1 (un envío solo puede estar asociado a un pedido)

## Transacciones: Implementación Detallada

### ¿Por qué son necesarias las transacciones?

La creación de un Pedido con su Envío es una **operación compuesta** que involucra dos inserciones en la base de datos:

1. `INSERT INTO envios (...) VALUES (...)`
2. `INSERT INTO pedidos (..., envio) VALUES (..., ID_del_envio_recien_creado)`

Si la segunda operación falla (por ejemplo, por violación de UNIQUE en `numero`), pero la primera ya se ejecutó, quedaría un **envío huérfano** en la base de datos (sin pedido asociado), rompiendo la integridad del sistema.

### Flujo Transaccional Implementado

**Clase**: `PedidoService.insertar()`

```
1. try (Connection conn = DatabaseConnection.getConnection()) {
2.     TransactionManager tm = TransactionManager.startTransaction(conn);
3.     try {
4.         // Validar datos del pedido y del envío
5.         Validations.validarEnvio(pedido.getEnvio());
6.         Validations.validarPedido(pedido);
7.         
8.         // Insertar envío y obtener ID generado
9.         Envio envioConId = envioDao.crearenvio(pedido.getEnvio(), conn);
10.        
11.        // Asociar el ID del envío al pedido
12.        pedido.setEnvio(envioConId);
13.        
14.        // Insertar pedido con la referencia al envío
15.        pedidoDao.crearpedido(pedido, conn);
16.        
17.        // Si todo OK, confirmar transacción
18.        tm.commit();
19.        
20.    } catch (Exception e) {
21.        // Si hay error, se hace rollback automático en tm.close()
22.        throw new RuntimeException("Error al crear pedido: " + e.getMessage());
23.    }
24. }
```

**Clase**: `TransactionManager`

```java
public class TransactionManager implements AutoCloseable {
    
    public static TransactionManager startTransaction(Connection conn) {
        conn.setAutoCommit(false);  // Desactiva autocommit
        return new TransactionManager(conn);
    }
    
    public void commit() {
        conn.commit();  // Confirma cambios
        committed = true;
    }
    
    @Override
    public void close() {
        if (!committed) {
            conn.rollback();  // Revierte si no se hizo commit
        }
        conn.setAutoCommit(true);  // Restaura autocommit
    }
}
```

### Ventajas de esta Implementación

1. **Atomicidad**: La operación es todo o nada (ambas inserciones o ninguna)
2. **Consistencia**: No quedan datos huérfanos en la base de datos
3. **Rollback Automático**: Si ocurre una excepción, el rollback se ejecuta automáticamente al salir del try-with-resources
4. **Código Limpio**: El uso de AutoCloseable simplifica el manejo de transacciones

## Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Base de Datos**: MySQL 8.x
- **JDBC Driver**: mysql-connector-j 8.4.0
- **IDE Recomendado**: IntelliJ IDEA / Eclipse

## Estructura de Directorios

```
TFI_P2/
├── src/
│   ├── config/
│   │   ├── DatabaseConnection.java    # Factory de conexiones
│   │   └── TransactionManager.java    # Gestor de transacciones
│   ├── dao/
│   │   ├── GenericDao.java            # Interfaz genérica DAO
│   │   ├── PedidoDao.java             # DAO de Pedidos
│   │   └── EnvioDao.java              # DAO de Envíos
│   ├── entities/
│   │   ├── Pedido.java                # Entidad Pedido
│   │   ├── Envio.java                 # Entidad Envío
│   │   ├── EstadoPedido.java          # Enum estados de pedido
│   │   ├── EstadoEnvio.java           # Enum estados de envío
│   │   ├── TipoEnvio.java             # Enum tipos de envío
│   │   └── EmpresaEnvio.java          # Enum empresas de envío
│   ├── service/
│   │   ├── GenericService.java        # Interfaz genérica Service
│   │   ├── PedidoService.java         # Servicio de Pedidos
│   │   ├── EnvioService.java          # Servicio de Envíos
│   │   └── Validations.java           # Validaciones de negocio
│   ├── main/
│   │   ├── Main.java                  # Punto de entrada
│   │   └── AppMenu.java               # Menú de usuario
│   └── util/
│       └── DatabaseSeeder.java        # Poblador de datos de prueba
├── setup-database.sql                 # Script de creación de BD
├── Informe_TFI_P2.pdf                # Informe técnico del proyecto
└── README.md                          # Este archivo
```

## Solución de Problemas

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Causa**: JAR de MySQL no está en classpath

**Solución**: Agregar `mysql-connector-j-8.4.0.jar` al classpath

### Error: "Communications link failure"
**Causa**: MySQL no está ejecutándose

**Solución**:
```bash
# Linux/macOS
sudo systemctl start mysql

# Windows
net start MySQL80
```

### Error: "Access denied for user 'root'@'localhost'"
**Causa**: Credenciales incorrectas

**Solución**: Verificar usuario/contraseña en `DatabaseConnection.java`

### Error: "Unknown database 'TFI_P2'"
**Causa**: Base de datos no creada

**Solución**: Ejecutar el script `setup-database.sql` en MySQL Workbench

### Error: "Duplicate entry 'XXXXX' for key 'tracking'"
**Causa**: Número de tracking ya existe en la base de datos

**Solución**: Usar un número de tracking diferente

### Error: "Rollback automático detectado"
**Causa**: Alguna operación dentro de la transacción falló

**Solución**: Revisar el mensaje de error específico (ej: violación de UNIQUE, formato de fecha inválido, etc.)

## Limitaciones Conocidas

1. **Interfaz solo consola**: No hay GUI gráfica (mejora futura: implementar interfaz web o de escritorio)
2. **Un envío por pedido**: La relación 1→1 impide múltiples envíos por pedido (limitación por diseño del TFI)
3. **Sin pool de conexiones**: Nueva conexión por operación (aceptable para aplicación de consola educativa)
4. **Eliminación lógica**: Los registros eliminados permanecen en la BD con flag `eliminado = true`
5. **Sin paginación**: Listar todos puede ser lento con muchos registros (mejora futura: implementar paginación)
6. **Validación de fecha simplificada**: Solo valida formato YYYY-MM-DD, no valida fechas futuras o pasadas ilógicas

## Mejoras Futuras

### Técnicas
- Implementar **pruebas unitarias** con JUnit para fortalecer confiabilidad
- Agregar **manejo de excepciones personalizadas** (PedidoNotFoundException, TransactionException)
- Implementar **patrón Repository** sobre DAO para mayor abstracción
- Usar **Dependency Injection** con framework (Spring) para reducir acoplamiento
- Migrar a **JPA/Hibernate** (ORM) para simplificar acceso a datos
- Agregar **pool de conexiones** (HikariCP) para mejorar rendimiento
- Implementar **logging** con SLF4J para trazabilidad

### Funcionales
- Extender sistema de menús hacia **interfaz gráfica** (JavaFX/Swing) o **API REST** (Spring Boot)
- Agregar **búsquedas avanzadas** (por rango de fechas, por empresa de envío, por estado)
- Implementar **reportes** (pedidos por mes, envíos pendientes, facturación)
- Agregar **auditoría** (quién y cuándo modificó cada registro)
- Soporte para **múltiples envíos por pedido** (cambiar relación 1→1 a 1→N)
- **Integración con APIs externas** (tracking real de empresas de envío)

## Conclusiones

Este proyecto permitió consolidar los conocimientos de:
- **Arquitectura en capas** con separación clara de responsabilidades
- **Persistencia con JDBC** usando PreparedStatements
- **Transacciones** para garantizar integridad de operaciones atómicas
- **Relaciones 1→1** mediante claves foráneas únicas
- **Validaciones multi-nivel** (Java + Base de Datos)
- **Patrones de diseño** (DAO, Service Layer, Factory, Transaction Management)

A nivel académico y profesional, el proyecto nos permitió no solo afianzar conceptos técnicos sino también desarrollar habilidades blandas fundamentales como la comunicación, organización del trabajo y coordinación en equipo, fundamentales para lograr una solución robusta y bien estructurada.

## Contexto Académico

**Materia**: Programación II
**Institución**: Tecnicatura Universitaria en Programación - Universidad Tecnológica Nacional
**Tipo de Evaluación**: Trabajo Final de Integración (TFI)
**Modalidad**: Desarrollo de sistema CRUD con persistencia y transacciones
**Objetivo**: Aplicar conceptos de POO, JDBC, arquitectura en capas, relaciones entre entidades y manejo de transacciones

---

**Versión**: 1.0
**Fecha**: Noviembre 2025
**Java**: 17+
**MySQL**: 8.x
**Proyecto Educativo** - Trabajo Final de Integración de Programación 2
