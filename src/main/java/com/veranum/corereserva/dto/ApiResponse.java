package com.veranum.corereserva.dto;

public class ApiResponse<T> {

    private int codigo;
    private String mensaje;
    private boolean error;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int codigo, String mensaje, boolean error, T data) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.error = error;
        this.data = data;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
