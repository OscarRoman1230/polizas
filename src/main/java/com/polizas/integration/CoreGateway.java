package com.polizas.integration;

import com.polizas.dto.CoreEvento;

public interface CoreGateway {
    void enviarEvento(CoreEvento evento);
}