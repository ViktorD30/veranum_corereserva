package com.veranum.corereserva.controller;

import com.veranum.corereserva.dto.ApiResponse;
import com.veranum.corereserva.dto.ReservaRequest;
import com.veranum.corereserva.dto.ReservaResponse;
import com.veranum.corereserva.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(
            ReservaService reservaService) {

        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservaResponse>>
    crearReserva(
            @Valid @RequestBody ReservaRequest request) {

        ReservaResponse reserva =
                reservaService.crearReserva(request);

        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(
                new ApiResponse<>(
                        201,
                        "Reserva creada correctamente",
                        false,
                        reserva
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservaResponse>>>
    obtenerTodas() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Listado de reservas",
                        false,
                        reservaService.obtenerTodas()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponse>>
    obtenerPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Reserva encontrada",
                        false,
                        reservaService.obtenerPorId(id)
                )
        );
    }

    @GetMapping("/usuario/{rut}")
    public ResponseEntity<ApiResponse<List<ReservaResponse>>>
    obtenerPorRut(@PathVariable String rut) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Historial de reservas del usuario",
                        false,
                        reservaService.obtenerPorRut(rut)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponse>>
    actualizarReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Reserva actualizada correctamente",
                        false,
                        reservaService.actualizarReserva(
                                id,
                                request
                        )
                )
        );
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaResponse>>
    cancelarReserva(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Reserva cancelada correctamente",
                        false,
                        reservaService.cancelarReserva(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>>
    eliminarReserva(@PathVariable Long id) {

        reservaService.eliminarReserva(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Reserva eliminada correctamente",
                        false,
                        null
                )
        );
    }
}
