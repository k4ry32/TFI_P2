#!/bin/bash

# Script de compilación para el Sistema de Gestión de Pedidos y Envíos
# Asegúrese de tener el driver MySQL Connector/J en el directorio lib/

echo "Compilando el Sistema de Gestión de Pedidos y Envíos..."

# Crear directorio de clases si no existe
mkdir -p classes

# Compilar todas las clases Java
javac -cp ".:lib/mysql-connector-j-8.2.0.jar" -d classes src/**/*.java

# Copiar archivos de recursos (como db.properties) al directorio classes
if [ -f "src/db.properties" ]; then
    cp src/db.properties classes/db.properties
    echo "Archivo db.properties copiado a classes/"
fi

if [ $? -eq 0 ]; then
    echo "Compilación exitosa!"
    echo "Para ejecutar el programa, use: ./run.sh"
else
    echo "Error en la compilación. Verifique los errores arriba."
    exit 1
fi
