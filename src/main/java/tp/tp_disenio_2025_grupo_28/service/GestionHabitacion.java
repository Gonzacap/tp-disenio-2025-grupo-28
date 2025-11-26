package tp.tp_disenio_2025_grupo_28.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.dto.*;
import tp.tp_disenio_2025_grupo_28.mapper.*;
// import tp.tp_disenio_2025_grupo_28.model.*;
// import tp.tp_disenio_2025_grupo_28.model.enums.*;
import tp.tp_disenio_2025_grupo_28.repository.*;

@Service
@Transactional
public class GestionHabitacion {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public void validarFecha(Date fechaDesde, Date fechaHasta) {

        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }

        if (fechaDesde.after(fechaHasta)) {
            throw new IllegalArgumentException("La fecha Desde no puede ser posterior a la fecha Hasta.");
        }
    }

    public List<Map<String, Object>> obtenerTiposHabitacion() {
        List<Map<String, Object>> lista = new ArrayList<>();

        lista.add(Map.of("nombre", "Individual Estándar", "cantidad", 10));
        lista.add(Map.of("nombre", "Doble Estándar", "cantidad", 12));
        lista.add(Map.of("nombre", "Doble Superior", "cantidad", 8));
        lista.add(Map.of("nombre", "Family Plan", "cantidad", 5));
        lista.add(Map.of("nombre", "Suite", "cantidad", 3));

        return lista;
    }

    public List<HabitacionDTO> obtenerHabitacionesOrdenadas() {
        return habitacionRepository
                .findAllByOrderByTipoAscNumeroHabitacionAsc()
                .stream()
                .map(HabitacionMapper::toDTO)
                .toList();
    }

    public List<ReservaDTO> obtenerReservasEntre(Date desde, Date hasta) {
        return reservaRepository.findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(desde, hasta)
                .stream()
                .map(ReservaMapper2::toDTO)
                .toList();
    }

    public List<Date> generarDiasEntre(Date desde, Date hasta) {
        List<Date> dias = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(desde);

        while (!c.getTime().after(hasta)) {
            dias.add(c.getTime());
            c.add(Calendar.DATE, 1);
        }

        return dias;
    }

    public List<Map<String, Object>> construirGrillaEstados(
            List<HabitacionDTO> habitaciones,
            List<ReservaDTO> reservas,
            List<Date> dias
    ) {

        List<Map<String, Object>> salida = new ArrayList<>();

        for (Date dia : dias) {

            Map<String, Object> row = new HashMap<>();
            row.put("fecha", dia);

            List<String> estados = new ArrayList<>();

            for (HabitacionDTO hab : habitaciones) {

                String estado = "DISPONIBLE";

                for (ReservaDTO r : reservas) {

                    boolean afecta
                            = r.getHabitaciones()
                                    .stream()
                                    .anyMatch(h -> h.getNumeroHabitacion().equals(hab.getNumeroHabitacion()))
                            && !dia.before(r.getFechaDesde())
                            && !dia.after(r.getFechaHasta());

                    if (afecta) {
                        estado = r.getEstado().toString();
                        break;
                    }
                }

                estados.add(estado);
            }

            row.put("estados", estados);
            salida.add(row);
        }

        return salida;
    }

}
