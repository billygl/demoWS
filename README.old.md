# TOOLS
## JDK 11
- https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
- Instalar

## VSCode
- https://code.visualstudio.com/download
- Instalar Extension Pack for Java
- Configurar en sección Java Projects, clic en tres puntos que aparecen a la derecha del título
  - Configurar Java Runtime para JDK 11.

## Soap UI
- https://www.soapui.org/downloads/latest-release/
- Instalar

# DATABASE
- Usar MySQL
- Instalar sql/demohacking.sql
````
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
FLUSH PRIVILEGES;
````
- run
````
cat demoHacking.sql | sudo mysql -u root -p
````

# RUN
- Usar VSCode
- En sección Java Projects, clic derecho en demows/Run