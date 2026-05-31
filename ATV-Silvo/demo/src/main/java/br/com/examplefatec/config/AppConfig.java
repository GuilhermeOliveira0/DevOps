package br.com.examplefatec.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracoes gerais da aplicacao.
 * Hoje fornece um Clock para facilitar testes de regras com data e hora.
 */
@Configuration
public class AppConfig {

    /**
     * Relogio padrao usado por services que dependem do horario atual.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
