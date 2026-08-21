# ElektroStorage

Et simpelt full-stack eksamensprojekt lavet med Spring Boot, JPA, H2, HTML, CSS og JavaScript.

## Start programmet

1. Åbn en terminal i denne mappe.
2. Kør `mvn spring-boot:run`.
3. Åbn `http://localhost:8080` i en browser.

H2-databasen oprettes automatisk med demonstrationsdata, hver gang programmet starter.

## Kør tests

Kør `mvn test`.

## REST API

- `GET/POST /api/components`
- `PATCH /api/components/{nummer}/discontinue`
- `GET/POST /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders/{id}/lines`
- `PATCH /api/orders/{id}/send`
- `PATCH /api/orders/{id}/receive`
- `GET /api/inventory`
- `POST /api/inventory/{komponentnummer}/counts`
- `GET /api/assemblies`
- `GET /api/suppliers`

## H2-konsol

Konsollen findes på `http://localhost:8080/h2-console`.

- JDBC URL: `jdbc:h2:mem:electrostorage`
- Brugernavn: `sa`
- Adgangskode: tom
