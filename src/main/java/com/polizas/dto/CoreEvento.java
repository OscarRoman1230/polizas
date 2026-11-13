package com.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Evento hacia el CORE. También es el cuerpo del endpoint /core-mock/evento. */
public record CoreEvento(@NotBlank String evento, @NotNull Long polizaId) {}