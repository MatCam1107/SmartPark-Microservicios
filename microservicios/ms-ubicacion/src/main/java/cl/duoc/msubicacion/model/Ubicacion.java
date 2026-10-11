package cl.duoc.msubicacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false, unique = true)
    private Long estacionamientoId;

    private Integer piso;
    private String sector;
    private Double latitud;
    private Double longitud;

    public Ubicacion() {
    }

    public Ubicacion(Long id, Zona zona, Long estacionamientoId, Integer piso,
                     String sector, Double latitud, Double longitud) {
        this.id = id;
        this.zona = zona;
        this.estacionamientoId = estacionamientoId;
        this.piso = piso;
        this.sector = sector;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
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
