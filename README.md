# ElectroShop

Nowoczesna aplikacja sklepu internetowego zbudowana z użyciem Spring Boot, Thymeleaf, Bootstrap i PostgreSQL.

## Technologie

- Java 17+
- Spring Boot (MVC, Security, JPA)
- Thymeleaf
- Bootstrap 5
- PostgreSQL
- Hibernate
- Lombok

---

## Uruchomienie projektu

### 1. Sklonuj repozytorium

```bash
git clone https://github.com/lukasz-zbrzeski/java_ElectroShop.git
cd java_ElectroShop
```

### 2. Skonfiguruj bazę danych

W pliku `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shop-app
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

Upewnij się, że baza `shop-app` istnieje.

### 3. Uruchom aplikację

#### Z IDE (np. IntelliJ)
Uruchom klasę `ElectroShopApplication`

#### Lub przez terminal

```bash
./mvnw spring-boot:run
```
