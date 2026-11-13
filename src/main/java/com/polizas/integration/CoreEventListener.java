package com.polizas.integration;

import com.polizas.dto.CoreEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CoreEventListener {

    private final CoreGateway gateway;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alConfirmar(CoreEvento evento) {
        gateway.enviarEvento(evento);
    }
}