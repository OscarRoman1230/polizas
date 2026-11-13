package com.polizas.dto;

import com.polizas.domain.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(Long id, String numero, TipoPoliza tipo, EstadoPoliza estado,
                             String tomador, LocalDate vigenciaInicio, LocalDate vigenciaFin,
                             int mesesVigencia, BigDecimal canonMensual, BigDecimal prima) {
    public static PolizaResponse from(Poliza p) {
        return new PolizaResponse(p.getId(), p.getNumero(), p.getTipo(), p.getEstado(),
                p.getTomador(), p.getVigenciaInicio(), p.getVigenciaFin(),
                p.getMesesVigencia(), p.getCanonMensual(), p.getPrima());
    }
}