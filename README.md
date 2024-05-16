# e-chat application
e-chat application for ORDINAL recruitment process 


## Conditions Respected
1. Eclipse version pour Java : https://www.eclipse.org/downloads/
2. Java SE adoptium : https://adoptium.net/temurin/releases/?os=windows&version=8
3. MySQL server community 5.7.44 : https://dev.mysql.com/downloads/installer/
4. Pour l’administration de MySQL vous pouvez utiliser Workbench (si la dernière version ne
fonctionne pas bien utiliser la version 8.0.22) : https://dev.mysql.com/downloads/workbench/
5. Driver JDBC de MySQL : https://dev.mysql.com/downloads/
6. Utiliser uniquement Java SE sans aucun framework uniquement le driver JDBC de MySQL
7. Uniquement SWING pour la partie graphique
8. Le multithreading
9. Les sockets
10. Faire le développement pour plateforme Windows

## Diagrams
1. [class_diagrame](src/main/resources/diagrammes/class_diagram.jpg)
2. [connexion_sequence_diagram](src/main/resources/diagrammes/connexion_sequence_diagram.jpg)
3. [communication_sequence_diagram](src/main/resources/diagrammes/communication_sequence_diagram.jpg)
4. [data_model_diagram](src/main/resources/diagrammes/data_model_diagram.jpg)
5. [use_case_diagram](src/main/resources/diagrammes/use_case_diagram.jpg)

## Database
2. the schema used in this project [simple schema](src/main/resources/db_scripts/chat_schema.sql) is a simple schema
in order to simplify the project.otherwise if the time was enough i would go with my second schema [Optimized schema](src/main/resources/db_scripts/clean_chat_schema.sql)
design which take in consideration most of the variables such as conversations and on which server ...





## How to run the application 
1. import database from [e-chat database](src/main/resources/db_scripts/db_scripts.sql). 
2. Run `mvn clean install -DskipTests` by going inside each folder to build the applications. 
3. After that run `mvn exec:java -Dexec.mainClass=org.ordinal.Main` by going inside each folder to start the applications.

