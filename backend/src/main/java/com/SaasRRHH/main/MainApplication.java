package com.SaasRRHH.main;

import com.SaasRRHH.main.model.Rol;
import com.SaasRRHH.main.model.Usuario;
import com.SaasRRHH.main.repository.RolRepository;
import com.SaasRRHH.main.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
@EnableScheduling
@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
