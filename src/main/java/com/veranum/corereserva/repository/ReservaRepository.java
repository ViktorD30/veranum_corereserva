package com.veranum.corereserva.repository;

import com.veranum.corereserva.model.Reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByRutUsuario(String rutUsuario);

    @Query("""
            SELECT COUNT(r)
            FROM Reserva r
            WHERE r.habitacionId = :habitacionId
            AND r.estado <> 'CANCELADA'
            AND r.fechaIngreso < :salida
            AND r.fechaSalida > :ingreso
            """)
    long contarReservasSuperpuestas(
            @Param("habitacionId") Long habitacionId,
            @Param("ingreso") LocalDate ingreso,
            @Param("salida") LocalDate salida
    );
}
