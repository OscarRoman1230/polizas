package com.polizas.dto;

import com.polizas.domain.*;

public record RiesgoResponse(Long id, Long polizaId, String direccionInmueble,
                             String arrendatario, EstadoRiesgo estado) {
    public static RiesgoResponse from(Riesgo r) {
        return new RiesgoResponse(r.getId(), r.getPoliza().getId(),
                r.getDireccionInmueble(), r.getArrendatario(), r.getEstado());
    }
}