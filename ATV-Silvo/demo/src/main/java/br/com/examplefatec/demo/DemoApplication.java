package br.com.examplefatec.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principal da aplicacao Spring Boot.
 * Tambem indica os pacotes onde o Spring deve procurar componentes, entities e repositories.
 */
@SpringBootApplication(scanBasePackages = "br.com.examplefatec")
@EntityScan(basePackages = "br.com.examplefatec.entity")
@EnableJpaRepositories(basePackages = "br.com.examplefatec.repository")
public class DemoApplication {

	/**
	 * Ponto de entrada para iniciar o servidor web embutido.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
