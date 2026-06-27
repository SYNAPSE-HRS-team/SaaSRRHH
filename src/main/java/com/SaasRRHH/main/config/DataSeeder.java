package com.SaasRRHH.main.config;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
    }

    private void seedRoles() {
        if (rolRepository.count() > 0) {
            log.info("Roles ya existen, se omite la seed.");
            return;
        }

        log.info("Creando roles por defecto...");

        Rol admin = new Rol();
        admin.setNombreRol("ADMIN");
        admin.setDescripcion("Administrador del sistema con acceso completo");
        rolRepository.save(admin);

        Rol supervisor = new Rol();
        supervisor.setNombreRol("SUPERVISOR");
        supervisor.setDescripcion("Supervisor con acceso a gestion de equipos");
        rolRepository.save(supervisor);

        Rol trabajador = new Rol();
        trabajador.setNombreRol("TRABAJADOR");
        trabajador.setDescripcion("Empleado regular con acceso basico");
        rolRepository.save(trabajador);

        log.info("Roles creados: ADMIN, SUPERVISOR, TRABAJADOR");
    }

    private void seedUsers() {
        if (usuarioRepository.count() > 0) {
            log.info("Usuarios ya existen, se omite la seed.");
            return;
        }

        log.info("Creando usuarios por defecto...");

        Rol adminRol = rolRepository.findByNombreRol("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
        Rol supervisorRol = rolRepository.findByNombreRol("SUPERVISOR")
                .orElseThrow(() -> new RuntimeException("Rol SUPERVISOR no encontrado"));
        Rol trabajadorRol = rolRepository.findByNombreRol("TRABAJADOR")
                .orElseThrow(() -> new RuntimeException("Rol TRABAJADOR no encontrado"));

        Usuario admin = new Usuario();
        admin.setEmail("admin@saasrrhh.com");
        admin.setPassword(passwordEncoder.encode("Admin123"));
        admin.setRol(adminRol);
        admin.setActivo(true);
        usuarioRepository.save(admin);
        log.info("Usuario ADMIN creado: admin@saasrrhh.com / Admin123");

        Usuario supervisor = new Usuario();
        supervisor.setEmail("supervisor@saasrrhh.com");
        supervisor.setPassword(passwordEncoder.encode("Super123"));
        supervisor.setRol(supervisorRol);
        supervisor.setActivo(true);
        usuarioRepository.save(supervisor);
        log.info("Usuario SUPERVISOR creado: supervisor@saasrrhh.com / Super123");

        Usuario trabajador = new Usuario();
        trabajador.setEmail("trabajador@saasrrhh.com");
        trabajador.setPassword(passwordEncoder.encode("123456"));
        trabajador.setRol(trabajadorRol);
        trabajador.setActivo(true);
        usuarioRepository.save(trabajador);
        log.info("Usuario TRABAJADOR creado: trabajador@saasrrhh.com / 123456");
    }
}
