package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionHuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;

@Controller
@RequestMapping("/ocupacion")
public class OcupacionController {

    @Autowired
    private GestionHabitacion gestionHabitacion;

    @Autowired
    private GestionHuesped gestionHuesped;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    // ----------------- 1) MOSTRAR BUSCADOR -----------------
    @GetMapping("/buscar")
    public String mostrarBuscar(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String cuitResponsable,
            Model model) {

        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("documento", documento);
        model.addAttribute("error", error);
        model.addAttribute("cuitResponsable", cuitResponsable); // Para recordar la selección

        return "ocuparHabitacion/buscar";
    }

    // ----------------- 2) PROCESAR BÚSQUEDA -----------------
    @PostMapping("/buscar")
    public String procesarBusqueda(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String documento,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        TipoDocumento tipoDocEnum = null;
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            try {
                tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
            } catch (Exception e) {
                model.addAttribute("error", "Tipo de documento inválido.");
                model.addAttribute("numero_habitacion", numero_habitacion);
                model.addAttribute("reservaId", reservaId);
                model.addAttribute("fechaDesde", fechaDesde);
                model.addAttribute("fechaHasta", fechaHasta);
                model.addAttribute("nombre", nombre);
                model.addAttribute("apellido", apellido);
                model.addAttribute("tipoDocumento", tipoDocumento);
                model.addAttribute("documento", documento);

            }
        }

        List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(apellido, nombre, tipoDocEnum, documento);

        model.addAttribute("resultados", resultados);
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/buscar";
    }

    // ----------------- 3) SELECCIONAR OCUPANTES -----------------
    @PostMapping("/seleccionar")
    public String seleccionarOcupantes(
            @RequestParam(required = false) String responsablePago,
            @RequestParam(required = false) List<String> acompanantes,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(required = false) String nombreBusqueda,
            @RequestParam(required = false) String apellidoBusqueda,
            @RequestParam(required = false) String tipoDocumentoBusqueda,
            @RequestParam(required = false) String documentoBusqueda,
            Model model) {

        if (responsablePago == null || responsablePago.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar un responsable de pago.");
            // Recargar filtros y resultados
            model.addAttribute("nombre", nombreBusqueda);
            model.addAttribute("apellido", apellidoBusqueda);
            model.addAttribute("tipoDocumento", tipoDocumentoBusqueda);
            model.addAttribute("documento", documentoBusqueda);
            model.addAttribute("numero_habitacion", numero_habitacion);
            model.addAttribute("reservaId", reservaId);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

            TipoDocumento tipoDocEnum = null;
            if (tipoDocumentoBusqueda != null && !tipoDocumentoBusqueda.isEmpty()) {
                try {
                    tipoDocEnum = TipoDocumento.valueOf(tipoDocumentoBusqueda);
                } catch (Exception ignored) {
                }
            }

            List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(apellidoBusqueda, nombreBusqueda, tipoDocEnum, documentoBusqueda);
            model.addAttribute("resultados", resultados);

            model.addAttribute("cuitResponsable", null);
            return "ocuparHabitacion/buscar";
        }

        // Filtrar acompañantes para no incluir al responsable
        List<String> acomp = (acompanantes == null) ? List.of()
                : acompanantes.stream().filter(c -> !c.equals(responsablePago)).collect(Collectors.toList());

        OcupacionHuespedDTO dto = new OcupacionHuespedDTO();
        dto.setIdHuesped(responsablePago);
        dto.setIdAcompanantes(acomp);

        OcupacionRequestDTO req = new OcupacionRequestDTO();
        req.setNumeroHabitacion(numero_habitacion);
        try {
            req.setFechaDesde(df.parse(fechaDesde));
            req.setFechaHasta(df.parse(fechaHasta));
        } catch (Exception e) {
            model.addAttribute("error", "Fecha inválida.");
            model.addAttribute("numero_habitacion", numero_habitacion);
            model.addAttribute("reservaId", reservaId);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

        }

        try {
            gestionHabitacion.ocuparHabitacion(reservaId, req, dto, false);
            return "redirect:/ocupacion/resumen?numero_habitacion=" + numero_habitacion
                    + "&reservaId=" + reservaId
                    + "&fechaDesde=" + fechaDesde
                    + "&fechaHasta=" + fechaHasta;
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("numero_habitacion", numero_habitacion);
            model.addAttribute("reservaId", reservaId);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

            model.addAttribute("cuitResponsable", responsablePago);
            model.addAttribute("cuitsAcompanantes", acomp);
            model.addAttribute("solapamiento", true);

            // Recargar resultados
            TipoDocumento tipoDocEnum = null;
            if (tipoDocumentoBusqueda != null && !tipoDocumentoBusqueda.isEmpty()) {
                try {
                    tipoDocEnum = TipoDocumento.valueOf(tipoDocumentoBusqueda);
                } catch (Exception ignored) {
                }
            }
            List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(apellidoBusqueda, nombreBusqueda, tipoDocEnum, documentoBusqueda);
            model.addAttribute("resultados", resultados);

            return "ocuparHabitacion/buscar";
        }
    }

    // ----------------- 4) RESUMEN -----------------
    @GetMapping("/resumen")
    public String resumen(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        List<PersonaFisica> ocupantes = gestionHabitacion.obtenerOcupantesAsignados(reservaId, numero_habitacion);
        PersonaFisica responsable = ocupantes.isEmpty() ? null : ocupantes.get(0);

        model.addAttribute("ocupantes", ocupantes);
        model.addAttribute("responsable", responsable);
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/resumen";
    }

    // ----------------- 5) FORZAR OCUPACIÓN -----------------
    @PostMapping("/forzar")
    public String ocuparIgualmente(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam String responsablePago,
            @RequestParam(required = false) List<String> acompanantes) {

        if (responsablePago == null || responsablePago.isEmpty()) {
            // Nunca dejar null
            return "redirect:/ocupacion/buscar?numero_habitacion=" + numero_habitacion + "&reservaId=" + reservaId + "&fechaDesde=" + fechaDesde + "&fechaHasta=" + fechaHasta;
        }

        OcupacionRequestDTO req = new OcupacionRequestDTO();
        req.setNumeroHabitacion(numero_habitacion);

        OcupacionHuespedDTO dto = new OcupacionHuespedDTO();
        dto.setIdHuesped(responsablePago);
        dto.setIdAcompanantes((acompanantes == null) ? List.of() : acompanantes);

        try {
            req.setFechaDesde(df.parse(fechaDesde));
            req.setFechaHasta(df.parse(fechaHasta));
        } catch (Exception e) {
            return "redirect:/ocupacion/buscar?numero_habitacion=" + numero_habitacion + "&reservaId=" + reservaId + "&fechaDesde=" + fechaDesde + "&fechaHasta=" + fechaHasta;
        }

        gestionHabitacion.ocuparHabitacion(reservaId, req, dto, true);

        return "redirect:/ocupacion/resumen?numero_habitacion=" + numero_habitacion + "&reservaId=" + reservaId + "&fechaDesde=" + fechaDesde + "&fechaHasta=" + fechaHasta;
    }
}
