# LABS
- Los ejemplos de los request están en los proyectos de SOAP UI
- Se ha versionado las particularidades de cada lab en ramas independientes
- La rama master es usada como la base para los laboratorios.

## Lab 3, 5
- Se puede usar https://www.base64encode.org/ para encodificar user:pass

# 1. PreRequisitos: TOOLS
## Git
- https://git-scm.com/downloads
- Instalar

## Docker Desktop
- https://www.docker.com/products/docker-desktop/
- Instalar

## Soap UI
- https://www.soapui.org/downloads/latest-release/
- Instalar

# 2. BUILD
- run
````
docker-compose build
````
- if necessary, delete db to restart database

# 3. RUN
````
docker-compose up
````
- wait until ready for connections
- access to http://localhost:8080, it shows a whitelabel

# 4. TEST
- Cambiar de laboratorio usando `git checkout labN` donde N es el número de laboratorio
- Ejecutar desde la carpeta del proyecto para cambiar de laboratorio
````
git checkout lab1
git checkout lab10
````
- Y ejecutar con el parámetro build para aplicar los cambios al contenedor.
````
docker-compose up --build
````
- use soap ui e importar xml de la carpeta client según el proyecto
- para ver la base de datos, conectarse usando el terminal del docker.

# UPDATE code
- Ejecutar
````
git pull --all
````

# Otro formas de ejecutar el proyecto
- See README.old.md
