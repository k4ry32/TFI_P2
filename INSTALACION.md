# Guía de Instalación - Sistema de Gestión de Pedidos y Envíos

## Requisitos Previos

### 1. Java Development Kit (JDK)
- Java 8 o superior
- Verificar instalación: `java -version`

### 2. MySQL Server
- MySQL 8.0 o superior
- Verificar instalación: `mysql --version`

### 3. MySQL Connector/J
- Descargar desde: https://dev.mysql.com/downloads/connector/j/
- Versión recomendada: 8.2.0 o superior
- Ya incluido en el proyecto en `lib/mysql-connector-j-8.2.0.jar`


## Pasos de Instalación

### 1. Configurar la Base de Datos

#### Configuración manual

1. Si MySQL está en Docker, verifica que el contenedor esté corriendo:
```bash
sudo docker ps | grep mysql
```

2. Si MySQL está local, asegurate que el servicio esta corriendo correctamente:
```bash
sudo systemctl status mysql
```

Si no lo está, puedes iniciarlo con el comando:
```bash
sudo systemctl start mysql
```

3. Ejecutar el script SQL manualmente:
```bash
mysql -h localhost -P 3306 -u root -p < setup-database.sql
```

### 2. Configurar el Driver MySQL
Este paso solo es necesario si quiere una version más nueva. Ya hay un driver funcional en la carpeta `lib/`.

1. Para una version más reciente descargar MySQL Connector/J desde:
   https://dev.mysql.com/downloads/connector/j/

2. Extraer el archivo JAR y copiarlo a la carpeta `lib/`:
```bash
cp mysql-connector-java-[version].jar lib/
```

### 3. Configurar las Credenciales

Hacer una copia del archivo model.db.properties en src/ y cambiar el nombre por `db.properties`.

Editar el archivo `src/db.properties` con sus credenciales de MySQL, por ejemplo:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/gestion_envios?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=tu_contraseña_mysql
```

Este archivo esta excluido por el .gitignore para que cada desarrollador lo adapte a su preferencia y como medio de seguridad para no compartir las claves a todas las personas con acceso al repo.

**Nota:** Si usas MySQL en un docker, cambia el puerto de `3306` al puerto utilizado por el contenedor en la URL.

### 4. Poblar la base de datos con datos de prueba (Opcional)
```bash
# Compilar primero
./compile.sh

# Ejecutar el seeder Java
java -cp ".:classes:lib/mysql-connector-j-8.2.0.jar" util.DatabaseSeeder
```

### 5. Compilar y Ejecutar

1. Compilar el proyecto:
```bash
./compile.sh
```

2. Ejecutar el programa:
```bash
./run.sh
```

## Verificación de la Instalación

### 1. Verificar Conexión a la Base de Datos
El programa verificará automáticamente la conexión al iniciar. 

### 2. Probar Funcionalidades
1. Crear un pedido
2. Crear un envío
3. Crear un pedido con envio asociado (operacion compuesta)
4. Buscar pedido por numero
5. Buscar envío por tracking

## Estructura Final del Proyecto

```
TFI_Programacion2/
├── src/
│   ├── config/
│   ├── dao/
│   ├── entities/
│   ├── main/
│   ├── service/
|   └── util/
├── lib/
│   └── mysql-connector-j-8.2.0.jar
├── classes/ (generado al compilar)
├── setup-database.sql       # Script SQL para crear BD y tablas
├── compile.sh
├── run.sh
├── README.md
└── INSTALACION.md
```

## Comandos Útiles

### Compilar manualmente:
```bash
javac -cp ".:lib/mysql-connector-java-8.2.0.jar" -d classes src/**/*.java
```

### Ejecutar manualmente:
```bash
java -cp ".:lib/mysql-connector-java-8.2.0.jar:classes" main.Main
```

### Verificar tablas en MySQL:
```sql
USE gestion_envios;
SHOW TABLES;
DESCRIBE pedidos;
DESCRIBE envios;
```
