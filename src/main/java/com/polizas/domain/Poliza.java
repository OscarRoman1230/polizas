package com.polizas.domain;

import com.polizas.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@NoArgsConstructor
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    private String tomador;
    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFin;
    private int mesesVigencia;

    @Column(precision = 15, scale = 2)
    private BigDecimal canonMensual;

    @Column(precision = 15, scale = 2)
    private BigDecimal prima;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    private List<Riesgo> riesgos = new ArrayList<>();

    public Poliza(String numero, TipoPoliza tipo, String tomador,
                  LocalDate inicio, int meses, BigDecimal canonMensual) {
        this.numero = numero;
        this.tipo = tipo;
        this.tomador = tomador;
        this.vigenciaInicio = inicio;
        this.mesesVigencia = meses;
        this.vigenciaFin = inicio.plusMonths(meses).minusDays(1);
        this.canonMensual = canonMensual;
        this.prima = canonMensual.multiply(BigDecimal.valueOf(meses));
        this.estado = EstadoPoliza.VIGENTE;
    }

    /** Regla: solo las pólizas colectivas admiten agregar riesgos. */
    public Riesgo agregarRiesgo(Riesgo riesgo) {
        if (tipo != TipoPoliza.COLECTIVA) {
            throw new BusinessRuleException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Solo las pólizas colectivas permiten agregar riesgos");
        }
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessRuleException(HttpStatus.CONFLICT,
                    "No se pueden agregar riesgos a una póliza cancelada");
        }
        return incorporarRiesgo(riesgo);
    }

    /** Regla: una póliza individual solo puede tener 1 riesgo. */
    public Riesgo incorporarRiesgo(Riesgo riesgo) {
        if (tipo == TipoPoliza.INDIVIDUAL && !riesgos.isEmpty()) {
            throw new BusinessRuleException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Una póliza individual solo puede tener un riesgo");
        }
        riesgo.setPoliza(this);
        riesgos.add(riesgo);
        return riesgo;
    }

    /** Regla: no se renueva una póliza cancelada. Incrementa canon y prima por IPC. */
    public void renovar(BigDecimal ipc) {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessRuleException(HttpStatus.CONFLICT,
                    "No se puede renovar una póliza cancelada");
        }
        this.canonMensual = canonMensual.multiply(BigDecimal.ONE.add(ipc))
                .setScale(2, RoundingMode.HALF_UP);
        this.prima = canonMensual.multiply(BigDecimal.valueOf(mesesVigencia))
                .setScale(2, RoundingMode.HALF_UP);
        this.vigenciaInicio = vigenciaFin.plusDays(1);
        this.vigenciaFin = vigenciaInicio.plusMonths(mesesVigencia).minusDays(1);
        this.estado = EstadoPoliza.RENOVADA;
    }

    /** Regla: cancelar la póliza cancela todos sus riesgos. */
    public void cancelar() {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessRuleException(HttpStatus.CONFLICT, "La póliza ya está cancelada");
        }
        this.estado = EstadoPoliza.CANCELADA;
        riesgos.stream()
                .filter(r -> r.getEstado() == EstadoRiesgo.ACTIVO)
                .forEach(Riesgo::cancelar);
    }
}