package com.polizas.domain;

import com.polizas.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "riesgos")
@Getter
@NoArgsConstructor
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id")
    @Setter
    private Poliza poliza;

    private String direccionInmueble;
    private String arrendatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    public Riesgo(String direccionInmueble, String arrendatario) {
        this.direccionInmueble = direccionInmueble;
        this.arrendatario = arrendatario;
    }

    public void cancelar() {
        if (estado == EstadoRiesgo.CANCELADO) {
            throw new BusinessRuleException(HttpStatus.CONFLICT, "El riesgo ya está cancelado");
        }
        this.estado = EstadoRiesgo.CANCELADO;
    }
}