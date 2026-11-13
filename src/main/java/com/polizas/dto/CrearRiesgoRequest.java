package com.polizas.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearRiesgoRequest(@NotBlank String direccionInmueble,
                                 @NotBlank String arrendatario) {}