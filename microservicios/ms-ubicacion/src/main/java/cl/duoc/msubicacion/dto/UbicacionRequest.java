package cl.duoc.msubicacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UbicacionRequest {

    @NotNull(message = "El ID de la zona es obligatorio")
    private Long zonaId;

    @NotNull(message = "El ID del estacionamiento es obligatorio")
    private Long estacionamientoId;

    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @NotBlank(message = "El sector es obligatorio")
    private String sector;

    private Double latitud;
    private Double longitud;

    public UbicacionRequest() {
    }

    public Long getZonaId() {
        return zonaId;
    }

    public void setZonaId(Long zonaId) {
        this.zonaId = zonaId;
    }

    public Long getEstacionamientoId() {
        return estacionamientoId;
    }

    public void setEstacionamientoId(Long estacionamientoId) {
        this.estacionamientoId = estacionamientoId;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
