package com.fincore.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * FinCore Config Server
 *
 * Centraliza la configuración de todos los microservicios.
 * Lee properties desde un repositorio Git y las sirve a cada servicio al arrancar.
 *
 * En desarrollo local: usa la carpeta config-repo/ dentro del classpath.
 * En producción: apunta a un repositorio GitHub privado.
 *
 * Cada microservicio llama a este servidor al iniciar:
 *   GET http://config-server:8888/{service-name}/{profile}
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}