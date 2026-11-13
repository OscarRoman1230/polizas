package com.polizas.services;

import com.polizas.domain.*;
import com.polizas.dto.*;
import com.polizas.exception.BusinessRuleException;
import com.polizas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private static final String ACTUALIZACION = "ACTUALIZACION";

    private final PolizaRepository polizas;
    private final RiesgoRepository riesgos;
    private final ApplicationEventPublisher events;

    @Value("${app.renovacion.ipc}")
    private BigDecimal ipc;

    @Transactional(readOnly = true)
    public List<PolizaResponse> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizas.buscar(tipo, estado).stream().map(PolizaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> riesgosDe(Long polizaId) {
        buscarPoliza(polizaId);
        return riesgos.findByPolizaIdOrderById(polizaId).stream().map(RiesgoResponse::from).toList();
    }

    @Transactional
    public PolizaResponse renovar(Long id) {
        Poliza p = buscarPoliza(id);
        p.renovar(ipc);
        notificarCore(p.getId());
        return PolizaResponse.from(p);
    }

    @Transactional
    public PolizaResponse cancelar(Long id) {
        Poliza p = buscarPoliza(id);
        p.cancelar();
        notificarCore(p.getId());
        return PolizaResponse.from(p);
    }

    @Transactional
    public RiesgoResponse agregarRiesgo(Long polizaId, CrearRiesgoRequest req) {
        Poliza p = buscarPoliza(polizaId);
        Riesgo nuevo = p.agregarRiesgo(new Riesgo(req.direccionInmueble(), req.arrendatario()));
        riesgos.save(nuevo);
        notificarCore(p.getId());
        return RiesgoResponse.from(nuevo);
    }

    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo r = riesgos.findById(riesgoId).orElseThrow(() ->
                new BusinessRuleException(HttpStatus.NOT_FOUND, "Riesgo " + riesgoId + " no encontrado"));
        r.cancelar();
        notificarCore(r.getPoliza().getId());
        return RiesgoResponse.from(r);
    }

    private Poliza buscarPoliza(Long id) {
        return polizas.findById(id).orElseThrow(() ->
                new BusinessRuleException(HttpStatus.NOT_FOUND, "Póliza " + id + " no encontrada"));
    }

    /** Se publica dentro de la transacción; se envía al CORE solo después del commit. */
    private void notificarCore(Long polizaId) {
        events.publishEvent(new CoreEvento(ACTUALIZACION, polizaId));
    }
}