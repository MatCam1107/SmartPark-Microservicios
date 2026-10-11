package cl.duoc.msestacionamiento.dto;

public class NotificacionRequest {

    private Long usuarioId;
    private String mensaje;
    private String tipo;
    private Boolean leida;

    public NotificacionRequest() {
    }

    public NotificacionRequest(Long usuarioId, String mensaje,
                               String tipo, Boolean leida) {
        this.usuarioId = usuarioId;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.leida = leida;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }
}
