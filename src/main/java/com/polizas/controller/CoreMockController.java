package com.polizas.controller;

import com.polizas.dto.CoreEvento;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Mock del CORE: solo registra en logs que la operación se intentó enviar. */
@Slf4j
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    @PostMapping("/evento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> recibir(@Valid @RequestBody CoreEvento evento) {
        log.info("[CORE-MOCK] Operación intentada hacia el CORE -> evento={}, polizaId={}",
                evento.evento(), evento.polizaId());
        return Map.of("status", "RECIBIDO");
    }
}