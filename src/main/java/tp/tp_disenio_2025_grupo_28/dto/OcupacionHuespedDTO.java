package tp.tp_disenio_2025_grupo_28.dto;

import java.util.List;

public class OcupacionHuespedDTO {

    private String idHuesped;
    private List<String> idAcompanantes;

    public OcupacionHuespedDTO() {
    }

    public OcupacionHuespedDTO(List<String> idAcompanantes, String idHuesped) {
        this.idAcompanantes = idAcompanantes;
        this.idHuesped = idHuesped;
    }

    public String getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(String idHuesped) {
        this.idHuesped = idHuesped;
    }

    public List<String> getIdAcompanantes() {
        return idAcompanantes;
    }

    public void setIdAcompanantes(List<String> idAcompanantes) {
        this.idAcompanantes = idAcompanantes;
    }
}
