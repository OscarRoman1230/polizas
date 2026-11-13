package com.polizas.controller;

import com.polizas.dto.RiesgoResponse;
import com.polizas.services.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final PolizaService service;

    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Long id) {
        return service.cancelarRiesgo(id);
    }
}