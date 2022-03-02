# Laboratorio 01:
-----------------------
- Armar un Web Service en SOAP
  - Método: Consulta de Saldos 
    - ws/ getBalancesRequest
  - Método: Retiro de dinero
    - ws/ withdrawRequest
  - Método: Deposito de dinero
    - ws/ depositRequest
  - Método: Transferir dinero
    - ws/ transferRequest
- Se necesita agregar una cabecera para el cliente del app.
  
- Autenticación: incluir usuario y password en un parámetro. 
  - Un único usuario para realizar todas las operaciones. Algo así:
````
<wsKey></wsKey>
<wsSecret></wsSecret>

<documentId></documentId>
<pass></pass>

<cuenta_origen></cuenta_destino>
<pass_cuenta></pass_cuenta>
<monto></monto>
<cuenta_destino></cuenta_destino>
````

# Laboratorio 02:
----------------------
- Igual al # Laboratorio 01 pero sin la autenticación inicial.
- Web Service en SOAP sin password ni validación de user
  - Método: Consulta de Saldos 
    - ws/ getBalancesRequest
  - Método: Retiro de dinero
    - ws/ withdrawRequest
  - Método: Deposito de dinero
    - ws/ depositRequest
  - Método: Transferir dinero
    - ws/ transferRequest

# Laboratorio 03:
---------------------
- Las mismas operaciones del # Laboratorio 01 pero a través de REST.
  - Método: Consulta de Saldos 
    - GET rest/balances
  - Método: Retiro de dinero
    - POST rest/withdraw
  - Método: Deposito de dinero
    - POST rest/deposit
  - Método: Transferir dinero
    - POST rest/transfer
- Se usa headers
  - X-API-Key: para el key de acceso al servicio web
  - Authorization: Basic BASE64(user:pass)
    - BASE64(user:pass) es la encodificación de user:passs

# Laboratorio 04:
---------------------
- SOAP vulnerable a inyecciones SQL.
  - Se cambiaron las queries de los DAO
  - Se tiene un ejemplo en ws/ getBalancesRequest

# Laboratorio 04b:
--------------------
- REST vulnerable a inyecciones SQL.
  - usando como el ejercicio 1
- Rama basada en Lab4

# Laboratorio 05:
--------------------
- REST vulnerable a inyecciones SQL.
  - Se tiene un ejemplo en GET rest/balances
    - Authorization base64(user@mail.com:' OR '' = ')
- Rama basada en Lab4

# Laboratorio 06:
--------------------
- Laboratorio 01 pero con JWT.

- Armar un Web Service en SOAP
  - Método: Autenticar
    - ws/ authenticate
    ````
    <user></user>
    <pass></pass
    ````
  - Método: Consulta de Saldos 
    - ws/ getBalancesJwtRequest
  - Método: Retiro de dinero
    - ws/ withdrawJwtRequest
  - Método: Deposito de dinero
    - ws/ depositJwtRequest
  - Método: Transferir dinero
    - ws/ transferJwtRequest
- similar    
````
<token></token>

<cuenta_origen></cuenta_destino>
<pass_cuenta></pass_cuenta>
<monto></monto>
<cuenta_destino></cuenta_destino>
````

# Laboratorio 07:
--------------------
@gustavo aquí tenemos que florear sobre tus vulns. De JWT.
- no hay rama al respecto

# Laboratorio 08:
--------------------
- Armar un Web Service en REST
  - Método: Ingreso de credenciales al sistema (cuenta y pass, este genera un TOKEN JWT)
    - POST /restjwt/authenticate
  - Método: Consulta de Saldos (que incluya el JWT, la idea es que se pueda modificar la cuenta origen para ver saldos de otras personas)
    - GET /restjwt/balances/{userId}
  - Método: Retiro de dinero
    - POST Cuenta origen y cuenta destino, la idea es que se pueda  modificar la cuenta ORIGEN para retirar dinero de manera no autorizada
    - POST /restjwt/withdraw
  - Método: Deposito de dinero
    - POST /restjwt/deposit
  - Método: Transferencia
    - POST /restjwt/transfer
  - Método: Logout (que no cierre el TOKEN y sea vulnerable)
    - GET /restjwt/logout

# Laboratorio 09: (muy parecido al 08)
--------------------
- Armar un Web Service en REST
  - Método: Ingreso de credenciales al sistema (cuenta y pass, este genera un TOKEN JWT)
    - POST /storejwt/authenticate
  - Método: Consulta de Listado Productos (que a pesar que debe utilizar TOKEN, el sistema no lo pida y sea vulnerable, debe haber 3 usuarios en el sistema y cada usuario ver productos diferentes)
    - GET /storejwt/products/{userId}
  - Método: Modificación de Productos (que solicite JWT, que un usuario pueda modificar un producto que no le corresponda)
    - PATCH /storejwt/products/{productId}
  - Método: Eliminación de Producto  (que solicite JWT, que solo el usuario adecuado pueda eliminar su producto)
    - DELETE /storejwt/products/{productId}
  - Método: Logout (que cierre el token y no se pueda reutilizar)
    - GET /storejwt/logout