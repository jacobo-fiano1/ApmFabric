# ApmFabric
Backend de el proyecto de Monsteral-Tech de APM.

## Tareas realizadas:
1. Lectura y realización del Getting Started que proporciona Hyperledger para aprender como desarrollar una app con Fabric.
2. Creación y despliegue de una red a partir de los ficheros del tutorial de 3 nodos, 2 nodos de tipo peer y uno de tipo orderer.Despliegue tambien de un contenedor con la herramienta de línea de comandos que proporciona.
3. Creación de un canal de transacciones entre organizaciones mediante los scripts proporcionados.
4. Creación de un smart contract que permita registrar los productos junto sus transaccciones en el ledger.
   Siguiendo los ejemplos del tutortial, se creo un chaincode en java que permitia registrar los productos que registraríamos en nuestra aplicación de monsteral-tech.
   Para ello se creo una clase DataType llamada Product en la que incluimos los atributos de los productos como id, nombre, url de la correspondiente página de wallapop, propietario, etc.
   Junto a esta se creo su correspondiente clase ProductTransfer en la que se definió todas las posibles transacciones que podrían tener los prodcutos, entre ellas tareas CRUD, cambios de propietario y tares de control.
