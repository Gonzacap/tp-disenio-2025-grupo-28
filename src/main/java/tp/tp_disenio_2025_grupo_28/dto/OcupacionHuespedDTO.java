package tp.tp_disenio_2025_grupo_28.dto;

import java.util.List;

public class OcupacionHuespedDTO {

    private Integer idHuesped;
    private List<Integer> idAcompanantes;

    public OcupacionHuespedDTO() {
    }

    public OcupacionHuespedDTO(List<Integer> idAcompanantes, Integer idHuesped) {
        this.idAcompanantes = idAcompanantes;
        this.idHuesped = idHuesped;
    }

    public Integer getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(Integer idHuesped) {
        this.idHuesped = idHuesped;
    }

    public List<Integer> getIdAcompanantes() {
        return idAcompanantes;
    }

    public void setIdAcompanantes(List<Integer> idAcompanantes) {
        this.idAcompanantes = idAcompanantes;
    }

}
