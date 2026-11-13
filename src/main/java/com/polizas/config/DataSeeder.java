package com.polizas.config;

import com.polizas.domain.*;
import com.polizas.repository.PolizaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PolizaRepository repo;

    public DataSeeder(PolizaRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        LocalDate inicio = LocalDate.of(2026, 1, 1);

        Poliza p1 = new Poliza("IND-001", TipoPoliza.INDIVIDUAL, "Juan Pérez", inicio, 12, new BigDecimal("1000000.00"));
        p1.incorporarRiesgo(new Riesgo("Cra 7 #45-10, Bogotá", "Juan Pérez"));

        Poliza p2 = new Poliza("COL-001", TipoPoliza.COLECTIVA, "Inmobiliaria Andina", inicio, 12, new BigDecimal("2000000.00"));
        p2.incorporarRiesgo(new Riesgo("Calle 100 #15-20 Apto 301", "María Gómez"));
        p2.incorporarRiesgo(new Riesgo("Calle 100 #15-20 Apto 302", "Carlos Ruiz"));

        Poliza p3 = new Poliza("IND-002", TipoPoliza.INDIVIDUAL, "Ana Torres", inicio, 12, new BigDecimal("900000.00"));
        p3.incorporarRiesgo(new Riesgo("Av 68 #20-30, Bogotá", "Ana Torres"));
        p3.cancelar();

        Poliza p4 = new Poliza("COL-002", TipoPoliza.COLECTIVA, "Admin. Conjunto Los Pinos", inicio, 6, new BigDecimal("1500000.00"));
        p4.incorporarRiesgo(new Riesgo("Torre 1 Apto 101", "Luis Mora"));
        p4.incorporarRiesgo(new Riesgo("Torre 1 Apto 102", "Sara León"));

        repo.save(p1); repo.save(p2); repo.save(p3); repo.save(p4);
    }
}