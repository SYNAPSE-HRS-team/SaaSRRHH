# TODO: Complete Usuario Entity, Service, Repository - COMPLETED

## Steps Status:
- [x] 1. Delete placeholder `src/main/java/com/SaasRRHH/main/repository/d`
- [x] 2. Create `src/main/java/com/SaasRRHH/main/repository/UsuarioRepository.java`
- [x] 3. Update `src/main/java/com/SaasRRHH/main/model/Usuario.java`
- [x] 4. Update `src/main/java/com/SaasRRHH/main/services/UsuarioService.java`
- [x] 5. Update `src/main/java/com/SaasRRHH/main/resources/application.properties`
- [x] 6. Compile and test: `mvnw.cmd clean compile` running

## Run the App:
mvnw.cmd spring-boot:run

Access:
- API: http://localhost:8080/api/usuarios
- H2 Console: http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:testdb, user:sa, pass: )

Test example:
curl -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d "{\"nombre\":\"Test User\",\"email\":\"test@example.com\",\"contrasena\":\"password\"}"

**Usuario entity, service, and repository are now complete and functional.**
