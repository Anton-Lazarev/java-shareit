# ShareIt
This Maven multi-module project include:

## Gateway
This layer aimed at receiving income messages from users and validating data for server.
Gateway exchanges messages with server with help of inner HTTP client.

## Server
Main functions of server:
* Add / modify / delete / browse users;
* Add / modify / browse / search items for sharing;
* Add / modify / delete / browse requests for absent items in system;
* Add / modify /browse bookings for using items;

Additional functionality is adding comments to items which use in the past.

DB scheme for server [here](/assets/db-scheme.png).

POSTMAN tests for server [here](https://github.com/yandex-praktikum/java-shareit/blob/add-docker/postman/sprint.json).
***
### Stack
For Gateway used Java 11, RestTemplate, Spring Boot 2.7.8.

For Server used Java 11, Spring Boot 2.7.8, Hibernate, PostgreSQL.
Server unit tests created in JUnit + Mockito.

Both modules collected in two linked docker services via [Docker](/docker-compose.yml) which contains three containers.