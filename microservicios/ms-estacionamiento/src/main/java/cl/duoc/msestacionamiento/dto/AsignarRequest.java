package cl.duoc.msestacionamiento.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarRequest {

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    public AsignarRequest() {
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }
}
