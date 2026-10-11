package cl.duoc.msestacionamiento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estacionamientos")
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstacionamiento estado;

    private Long ubicacionId;

    public Estacionamiento() {
    }

    public Estacionamiento(Long id, String codigo,
                           EstadoEstacionamiento estado, Long ubicacionId) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.ubicacionId = ubicacionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
