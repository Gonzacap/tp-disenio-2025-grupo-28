package tp.tp_disenio_2025_grupo_28.dto;

import java.util.List;

//lo que devolvemos en el front
public class ReservaResponseDTO {

    private Integer idReserva;
    private String estado;
    private List<Integer> habitacionesReservadas;

    public ReservaResponseDTO() {
    }

    public ReservaResponseDTO(String estado, List<Integer> habitacionesReservadas, Integer idReserva) {
        this.estado = estado;
        this.habitacionesReservadas = habitacionesReservadas;
        this.idReserva = idReserva;
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Integer> getHabitacionesReservadas() {
        return habitacionesReservadas;
    }

    public void setHabitacionesReservadas(List<Integer> habitacionesReservadas) {
        this.habitacionesReservadas = habitacionesReservadas;
    }

}
