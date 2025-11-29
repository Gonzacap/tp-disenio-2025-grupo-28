package tp.tp_disenio_2025_grupo_28.mapper;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;

public class OcupacionMapper {

    public static EstadoHabitacionPeriodo toPeriodo(OcupacionRequestDTO dto) {
        EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo();
        p.setNumeroHabitacion(dto.getNumeroHabitacion());
        p.setEstado(EstadoHabitacion.ocupada);
        p.setFechaDesde(dto.getFechaDesde());
        p.setFechaHasta(dto.getFechaHasta());
        return p;
    }
}
