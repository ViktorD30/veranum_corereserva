package com.veranum.corereserva.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class ReservaRequest {

    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Pattern(
            regexp = "^[0-9]+-[0-9kK]{1}$",
            message = "Formato de RUT inválido. Debe ser sin puntos y con guion"
    )
    private String rutUsuario;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @FutureOrPresent(message = "La fecha de ingreso no puede estar en el pasado")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida;

    @NotNull(message = "Debe indicar el ID de la habitación")
    private Long habitacionId;

    @NotNull(message = "La cantidad de huéspedes es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private Integer cantidadHuespedes;

    public ReservaRequest() {
    }

    public String getRutUsuario() {
        return rutUsuario;
    }

    public void setRutUsuario(String rutUsuario) {
        this.rutUsuario = rutUsuario;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public Integer getCantidadHuespedes() {
        return cantidadHuespedes;
    }

    public void setCantidadHuespedes(Integer cantidadHuespedes) {
        this.cantidadHuespedes = cantidadHuespedes;
    }
}
