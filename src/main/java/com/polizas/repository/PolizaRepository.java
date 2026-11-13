package com.polizas.repository;

import com.polizas.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("""
           select p from Poliza p
           where (:tipo is null or p.tipo = :tipo)
             and (:estado is null or p.estado = :estado)
           order by p.id
           """)
    List<Poliza> buscar(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);
}