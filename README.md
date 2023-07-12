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
- Para cambiar de rama usar
````
git checkout lab1
git checkout lab10
````
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

# Otro formas de ejecutar el proyecto
- See README.old.md
