package com.veranum.corereserva.client;

import com.veranum.corereserva.dto.ApiResponse;
import com.veranum.corereserva.dto.HabitacionResponse;
import com.veranum.corereserva.exception.BusinessException;
import com.veranum.corereserva.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class HabitacionClient {

    private final RestClient restClient;

    public HabitacionClient(
            RestClient.Builder builder,
            @Value("${habitacion.service.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public HabitacionResponse obtenerHabitacion(Long id) {

        try {
            ApiResponse<HabitacionResponse> respuesta = restClient.get()
                    .uri("/api/v1/habitaciones/{id}", id)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<HabitacionResponse>>() {
                    });

            if (respuesta == null || respuesta.getData() == null) {
                throw new ResourceNotFoundException(
                        "La habitación solicitada no existe"
                );
            }

            return respuesta.getData();

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(
                        "La habitación solicitada no existe"
                );
            }

            throw new BusinessException(
                    "Error al consultar el microservicio de habitaciones"
            );

        } catch (Exception ex) {
            throw new BusinessException(
                    "No fue posible comunicarse con el microservicio de habitaciones"
            );
        }
    }
}
