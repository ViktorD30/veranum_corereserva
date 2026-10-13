package com.veranum.corereserva.client;

import com.veranum.corereserva.dto.ApiResponse;
import com.veranum.corereserva.exception.BusinessException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(
            RestClient.Builder builder,
            @Value("${usuario.service.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public boolean validarUsuarioParaReserva(String rut) {

        try {
            ApiResponse<Boolean> respuesta = restClient.get()
                    .uri("/api/v1/usuarios/validar-reserva/{rut}", rut)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Boolean>>() {
                    });

            return respuesta != null
                    && !respuesta.isError()
                    && Boolean.TRUE.equals(respuesta.getData());

        } catch (RestClientResponseException ex) {
            throw new BusinessException(
                    "El usuario no está habilitado para realizar reservas"
            );
        } catch (Exception ex) {
            throw new BusinessException(
                    "No fue posible comunicarse con el microservicio de usuarios"
            );
        }
    }
}
