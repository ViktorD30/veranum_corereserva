package com.veranum.corereserva.service;

import com.veranum.corereserva.client.HabitacionClient;
import com.veranum.corereserva.client.UsuarioClient;
import com.veranum.corereserva.dto.HabitacionResponse;
import com.veranum.corereserva.dto.ReservaRequest;
import com.veranum.corereserva.dto.ReservaResponse;
import com.veranum.corereserva.exception.BusinessException;
import com.veranum.corereserva.exception.ResourceNotFoundException;
import com.veranum.corereserva.model.Reserva;
import com.veranum.corereserva.repository.ReservaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {

    private static final Logger log =
            LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final UsuarioClient usuarioClient;
    private final HabitacionClient habitacionClient;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioClient usuarioClient,
            HabitacionClient habitacionClient) {

        this.reservaRepository = reservaRepository;
        this.usuarioClient = usuarioClient;
        this.habitacionClient = habitacionClient;
    }

    public ReservaResponse crearReserva(ReservaRequest request) {

        log.info(
                "Iniciando reserva para usuario {}",
                request.getRutUsuario()
        );

        validarFechas(request);

        boolean usuarioValido =
                usuarioClient.validarUsuarioParaReserva(
                        request.getRutUsuario()
                );

        if (!usuarioValido) {
            throw new BusinessException(
                    "El usuario no está habilitado para realizar reservas"
            );
        }

        HabitacionResponse habitacion =
                habitacionClient.obtenerHabitacion(
                        request.getHabitacionId()
                );

        validarHabitacion(habitacion);
        validarCapacidad(request, habitacion);

        long superpuestas =
                reservaRepository.contarReservasSuperpuestas(
                        request.getHabitacionId(),
                        request.getFechaIngreso(),
                        request.getFechaSalida()
                );

        if (superpuestas > 0) {
            throw new BusinessException(
                    "La habitación ya se encuentra reservada en las fechas seleccionadas"
            );
        }

        long noches = ChronoUnit.DAYS.between(
                request.getFechaIngreso(),
                request.getFechaSalida()
        );

        double total =
                noches * habitacion.getPrecioPorNoche();

        Reserva reserva = new Reserva();

        reserva.setRutUsuario(request.getRutUsuario());
        reserva.setHabitacionId(request.getHabitacionId());
        reserva.setFechaIngreso(request.getFechaIngreso());
        reserva.setFechaSalida(request.getFechaSalida());
        reserva.setCantidadHuespedes(
                request.getCantidadHuespedes()
        );
        reserva.setCostoTotal(total);
        reserva.setEstado("CONFIRMADA");

        Reserva guardada =
                reservaRepository.save(reserva);

        log.info(
                "Reserva creada con ID {} y total ${}",
                guardada.getId(),
                total
        );

        return convertirResponse(guardada);
    }

    public List<ReservaResponse> obtenerTodas() {

        return reservaRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    public ReservaResponse obtenerPorId(Long id) {

        Reserva reserva = buscarEntidad(id);

        return convertirResponse(reserva);
    }

    public List<ReservaResponse> obtenerPorRut(String rut) {

        return reservaRepository
                .findByRutUsuario(rut)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    public ReservaResponse actualizarReserva(
            Long id,
            ReservaRequest request) {

        Reserva reserva = buscarEntidad(id);

        if ("CANCELADA".equalsIgnoreCase(
                reserva.getEstado())) {

            throw new BusinessException(
                    "No se puede modificar una reserva cancelada"
            );
        }

        validarFechas(request);

        HabitacionResponse habitacion =
                habitacionClient.obtenerHabitacion(
                        request.getHabitacionId()
                );

        validarHabitacion(habitacion);
        validarCapacidad(request, habitacion);

        long superpuestas =
                reservaRepository.contarReservasSuperpuestas(
                        request.getHabitacionId(),
                        request.getFechaIngreso(),
                        request.getFechaSalida()
                );

        /*
         * Si estamos actualizando la misma reserva,
         * la consulta podría contarla a ella misma.
         *
         * Por ahora permitimos continuar únicamente
         * cuando no existe otra reserva superpuesta.
         */
        if (superpuestas > 1) {
            throw new BusinessException(
                    "La habitación ya posee una reserva para esas fechas"
            );
        }

        long noches = ChronoUnit.DAYS.between(
                request.getFechaIngreso(),
                request.getFechaSalida()
        );

        double total =
                noches * habitacion.getPrecioPorNoche();

        reserva.setRutUsuario(request.getRutUsuario());
        reserva.setHabitacionId(
                request.getHabitacionId()
        );
        reserva.setFechaIngreso(
                request.getFechaIngreso()
        );
        reserva.setFechaSalida(
                request.getFechaSalida()
        );
        reserva.setCantidadHuespedes(
                request.getCantidadHuespedes()
        );
        reserva.setCostoTotal(total);

        return convertirResponse(
                reservaRepository.save(reserva)
        );
    }

    public ReservaResponse cancelarReserva(Long id) {

        Reserva reserva = buscarEntidad(id);

        if ("CANCELADA".equalsIgnoreCase(
                reserva.getEstado())) {

            throw new BusinessException(
                    "La reserva ya se encuentra cancelada"
            );
        }

        reserva.setEstado("CANCELADA");

        return convertirResponse(
                reservaRepository.save(reserva)
        );
    }

    public void eliminarReserva(Long id) {

        Reserva reserva = buscarEntidad(id);

        reservaRepository.delete(reserva);

        log.info(
                "Reserva {} eliminada correctamente",
                id
        );
    }

    private Reserva buscarEntidad(Long id) {

        return reservaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reserva no encontrada con ID: " + id
                        )
                );
    }

    private void validarFechas(
            ReservaRequest request) {

        if (!request.getFechaSalida()
                .isAfter(request.getFechaIngreso())) {

            throw new BusinessException(
                    "La fecha de salida debe ser posterior a la fecha de ingreso"
            );
        }
    }

    private void validarHabitacion(
            HabitacionResponse habitacion) {

        if ("MANTENIMIENTO".equalsIgnoreCase(
                habitacion.getEstado())) {

            throw new BusinessException(
                    "La habitación se encuentra en mantenimiento"
            );
        }
    }

    private void validarCapacidad(
            ReservaRequest request,
            HabitacionResponse habitacion) {

        int capacidadMax;

        if ("SIMPLE".equalsIgnoreCase(
                habitacion.getTipo())) {

            capacidadMax = 1;

        } else if ("DOBLE".equalsIgnoreCase(
                habitacion.getTipo())) {

            capacidadMax = 2;

        } else {

            capacidadMax = 4;
        }

        if (request.getCantidadHuespedes()
                > capacidadMax) {

            throw new BusinessException(
                    "La cantidad de huéspedes supera la capacidad máxima de la habitación: "
                            + capacidadMax
            );
        }
    }

    private ReservaResponse convertirResponse(
            Reserva reserva) {

        ReservaResponse response =
                new ReservaResponse();

        response.setId(reserva.getId());
        response.setRutUsuario(
                reserva.getRutUsuario()
        );
        response.setHabitacionId(
                reserva.getHabitacionId()
        );
        response.setFechaIngreso(
                reserva.getFechaIngreso()
        );
        response.setFechaSalida(
                reserva.getFechaSalida()
        );
        response.setCantidadHuespedes(
                reserva.getCantidadHuespedes()
        );
        response.setCostoTotal(
                reserva.getCostoTotal()
        );
        response.setEstado(
                reserva.getEstado()
        );

        return response;
    }
}
