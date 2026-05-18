# ShopFlow — Backend Spring Boot

## Prérequis
- Java 19+
- Maven 3.8+

## Lancement
```bash
mvn spring-boot:run
```

## Accès
- Swagger UI : http://localhost:8081/swagger-ui.html
- H2 Console : http://localhost:8081/h2-console
    - JDBC URL : jdbc:h2:mem:shopflowdb
    - User : sa / Password : (vide)

## Comptes de test
| Email | Password | Rôle |
|---|---|---|
| admin@shopflow.tn | admin123 | ADMIN |
| seller@shopflow.tn | seller123 | SELLER |
| customer@shopflow.tn | customer123 | CUSTOMER |

## Architecture
Controller → Service → Repository → Entity → H2