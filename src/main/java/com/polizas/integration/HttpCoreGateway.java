package com.polizas.integration;

import com.polizas.dto.CoreEvento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class HttpCoreGateway implements CoreGateway {

    private final RestClient client;

    public HttpCoreGateway(@Value("${core.base-url}") String baseUrl,
                           @Value("${app.security.api-key}") String apiKey) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    @Override
    public void enviarEvento(CoreEvento evento) {
        try {
            client.post().uri("/core-mock/evento")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(evento)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Evento enviado al CORE: {}", evento);
        } catch (Exception e) {
            // Un fallo del CORE no debe revertir la operación local (en producción: outbox + reintentos)
            log.error("No se pudo enviar el evento al CORE: {} - {}", evento, e.getMessage());
        }
    }
}