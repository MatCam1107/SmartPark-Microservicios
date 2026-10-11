package cl.duoc.msestacionamiento.dto;

import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EstacionamientoRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede superar los 10 caracteres")
    private String codigo;

    private EstadoEstacionamiento estado;

    private Long ubicacionId;

    public EstacionamientoRequest() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public EstadoEstacionamiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEstacionamiento estado) {
        this.estado = estado;
    }

    public Long getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(Long ubicacionId) {
        this.ubicacionId = ubicacionId;
    }
}
