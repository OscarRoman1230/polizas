package com.polizas.controller;

import com.polizas.domain.*;
import com.polizas.dto.*;
import com.polizas.services.PolizaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService service;

    @GetMapping
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                       @RequestParam(required = false) EstadoPoliza estado) {
        return service.listar(tipo, estado);
    }

    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> riesgos(@PathVariable Long id) {
        return service.riesgosDe(id);
    }

    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Long id) {
        return service.renovar(id);
    }

    @PostMapping("/{id}/cancelar")
    public PolizaResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public RiesgoResponse agregarRiesgo(@PathVariable Long id,
                                        @Valid @RequestBody CrearRiesgoRequest request) {
        return service.agregarRiesgo(id, request);
    }
}