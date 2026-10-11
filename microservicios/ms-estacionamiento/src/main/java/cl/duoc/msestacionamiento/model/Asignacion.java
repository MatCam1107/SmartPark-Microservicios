package cl.duoc.msestacionamiento.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estacionamiento_id")
    private Estacionamiento estacionamiento;

    @Column(nullable = false)
    private Long vehiculoId;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaTermino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsignacion estado;

    public Asignacion() {
    }

    public Asignacion(Long id, Estacionamiento estacionamiento, Long vehiculoId,
                      LocalDateTime fechaInicio, LocalDateTime fechaTermino,
                      EstadoAsignacion estado) {
        this.id = id;
        this.estacionamiento = estacionamiento;
        this.vehiculoId = vehiculoId;
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estacionamiento getEstacionamiento() {
        return estacionamiento;
    }

    public void setEstacionamiento(Estacionamiento estacionamiento) {
        this.estacionamiento = estacionamiento;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(LocalDateTime fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }
}
