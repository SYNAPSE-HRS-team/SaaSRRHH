package com.SaasRRHH.main;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
	@Bean
	CommandLineRunner initData(
			UsuarioRepository usuarioRepository,
			RolRepository rolRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Buscar el rol ADMIN (idRol = 1)
			Rol rolAdmin = rolRepository.findById(1L)
					.orElseThrow(() -> new RuntimeException("❌ Rol ADMIN no encontrado"));

			// Verificar si ya existe el usuario
			if (!usuarioRepository.findByEmail("admin@test.com").isPresent()) {

				// Crear nuevo usuario
				Usuario admin = new Usuario();
				admin.setEmail("admin@test.com");
				admin.setPassword(passwordEncoder.encode("admin123")); // ✅ Se encripta solo
				admin.setRol(rolAdmin);
				admin.setActivo(true);

				// Guardar en BD
				usuarioRepository.save(admin);

				System.out.println("✅ USUARIO CREADO EXITOSAMENTE:");
				System.out.println("   Email: admin@test.com");
				System.out.println("   Password: admin123");
				System.out.println("   Rol: ADMIN");
			} else {
				System.out.println("ℹ️ El usuario admin@test.com ya existe");
			}
		};
	}
}