# user-service

Service REST autonome (Java 17, Spring Boot 3, PostgreSQL, Flyway) sur le port 8081. Swagger UI : http://localhost:8081/swagger-ui.html.

## Lancement
Pour exécuter le service avec le reste du projet sans Docker, lancer le profil local depuis ce dossier. Il utilise H2 en mémoire sur le port 8088, évitant la base partagée et le port 8081 utilisé par service-batiments :

    .\mvnw.cmd -Dspring-boot.run.profiles=local spring-boot:run

Swagger UI sera disponible sur http://localhost:8088/swagger-ui.html. Les données H2 sont supprimées à l'arrêt du processus.

Variables : SERVER_PORT (8081), DB_URL (jdbc:postgresql://localhost:5432/user_db), DB_USERNAME (user), DB_PASSWORD (user). Le service-batiments utilise déjà 8081 dans ce dépôt. Pour lancer user-service en parallèle sans Docker ni base partagée, utiliser le profil local, qui écoute sur 8088 avec H2 en mémoire :

    .\mvnw.cmd -Dspring-boot.run.profiles=local spring-boot:run

Pour PostgreSQL, lancer docker compose up -d user-db puis .\mvnw.cmd spring-boot:run. Cela utilise une base dédiée user_db sur le port 5432, distincte de water_db sur le port 5433. Si service-batiments tourne en même temps, définir SERVER_PORT=8088 avant le lancement.

Le fichier docker-compose.yml démarre uniquement PostgreSQL. Pour construire l'image du service : docker build -t user-service . puis docker run --rm -p 8088:8081 -e DB_URL=jdbc:postgresql://host.docker.internal:5432/user_db -e DB_USERNAME=user -e DB_PASSWORD=user user-service.

## API
Tous les corps sont JSON. Le mot de passe est hashé avec BCrypt et n'apparaît jamais dans les réponses. Les exemples utilisent le port 8081 par défaut ; en profil local, utiliser le port 8088.

### Utilisateurs
Les exemples suivants couvrent chaque route utilisateur (les identifiants 1 et 2 viennent des créations) :

curl -X POST localhost:8081/api/users -H 'Content-Type: application/json' -d '{"firstName":"Amina","lastName":"Ben Ali","email":"amina@example.com","phone":"+21612345678","password":"motdepasse1","role":"CONSUMER"}'
curl 'localhost:8081/api/users?page=0&size=20&role=CONSUMER&status=ACTIVE'
curl localhost:8081/api/users/1
curl -X PUT localhost:8081/api/users/1 -H 'Content-Type: application/json' -d '{"firstName":"Amina","lastName":"Ben Ali","phone":"+21699887766","role":"CONSUMER"}'
curl -X DELETE localhost:8081/api/users/1
curl -X PATCH localhost:8081/api/users/1/deactivate
curl -X PATCH localhost:8081/api/users/1/reactivate
curl -X PATCH localhost:8081/api/users/1/password -H 'Content-Type: application/json' -d '{"oldPassword":"motdepasse1","newPassword":"nouveaumdp2"}'
curl -X POST localhost:8081/api/users/authenticate -H 'Content-Type: application/json' -d '{"email":"amina@example.com","password":"motdepasse1"}'
curl 'localhost:8081/api/users/1/login-attempts?page=0&size=20'
curl -X POST localhost:8081/api/users -H 'Content-Type: application/json' -d '{"firstName":"Sami","lastName":"Trabelsi","email":"sami@example.com","password":"motdepasse1","role":"TECHNICIAN","speciality":"Plomberie","serviceZone":"Tunis"}'
curl localhost:8081/api/users/2/technician-profile
curl -X PUT localhost:8081/api/users/2/technician-profile -H 'Content-Type: application/json' -d '{"speciality":"Électricité","serviceZone":"Sfax"}'
curl -X PATCH localhost:8081/api/users/2/technician-profile/availability -H 'Content-Type: application/json' -d '{"availability":"BUSY"}'

### Rôles
curl localhost:8081/api/roles
curl localhost:8081/api/roles/1
curl -X POST localhost:8081/api/roles -H 'Content-Type: application/json' -d '{"name":"CONSUMER","description":"Client"}'
curl -X PUT localhost:8081/api/roles/1 -H 'Content-Type: application/json' -d '{"name":"ADMIN","description":"Administrateur"}'
curl -X DELETE localhost:8081/api/roles/1

Les rôles standards sont initialisés au démarrage. Supprimer un rôle utilisé renvoie 409. La cinquième tentative erronée verrouille le compte (423); les précédentes répondent 401.

## Tests
Exécuter .\mvnw.cmd test. Les tests vérifient BCrypt, le verrouillage au cinquième échec et le CRUD MockMvc sur H2.
