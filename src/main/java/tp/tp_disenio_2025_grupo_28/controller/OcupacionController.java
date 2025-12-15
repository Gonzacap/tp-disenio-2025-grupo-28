package tp.tp_disenio_2025_grupo_28.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.service.EstadiaService;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;
import tp.tp_disenio_2025_grupo_28.service.ReservaService;

@Controller
@SessionAttributes({"huespedCargado", "ocupantesCargados", "responsable", "reservaId", "estadiaId", "fechaDesde", "fechaHasta", "numeroHabitacion", "personasResultados"})
@RequestMapping("/ocupacion")
public class OcupacionController {

    @Autowired
    private GestionHabitacion gestionHabitacion;

    @Autowired
    private GestionHuesped gestionHuesped;

    @Autowired
    private EstadiaService estadiaService;

    @Autowired
    private ReservaService reservaService;

    /// ***********
    /// Nuevos metodos
    /// ***********

    @ModelAttribute("huespedCargado")
    public Huesped huespedCargado() {
        return null;
    }

    @ModelAttribute("ocupantesCargados")
    public List<PersonaFisica> ocupantesCargados() {
        return new ArrayList<>();
    }

    @ModelAttribute("personasResultados")
    public List<PersonaFisica> personasResultados() {
        return new ArrayList<>();
    }

    @ModelAttribute("reservaId")
    public Integer reservaId() {
        return null;
    }

    @ModelAttribute("estadiaId")
    public Integer estadiaId() {
        return null;
    }

    @ModelAttribute("fechaDesde")
    public Date fechaDesde() {
        return null;
    }

    @ModelAttribute("fechaHasta")
    public Date fechaHasta() {
        return null;
    }

    @ModelAttribute("numeroHabitacion")
    public Integer numeroHabitacion() {
        return null;
    }

    /**
     * 1) INICIALIZAR PANTALLA DE CARGA
     */
    @GetMapping("/cargar")
    public String iniciarCarga(
            @RequestParam(required = false) Integer reservaIdParam,
            @RequestParam(required = false) Integer estadiaIdParam,
            @RequestParam(required = false) Integer numeroHabitacionParam,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaDesdeParam,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaHastaParam,
            @ModelAttribute("reservaId") Integer reservaId,
            @ModelAttribute("estadiaId") Integer estadiaId,
            @ModelAttribute("numeroHabitacion") Integer numeroHabitacion,
            @ModelAttribute("fechaDesde")
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaDesde,
            @ModelAttribute("fechaHasta")
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaHasta,
            Model model,
            @ModelAttribute("huespedCargado") Huesped huespedCargado,
            @ModelAttribute("ocupantesCargados") List<PersonaFisica> ocupantesCargados,
            RedirectAttributes redirect
    ) {

        try {
            System.out.println("\n\n llego a iniciarCarga() \n\n");

            Integer reservaIdAux = reservaIdParam != null ? reservaIdParam : reservaId;
            Integer estadiaIdAux = estadiaIdParam != null ? estadiaIdParam : estadiaId;
            Integer numeroHabitacionAux = numeroHabitacionParam != null ? numeroHabitacionParam : numeroHabitacion;
            Date fechaDesdeAux = fechaDesdeParam != null ? fechaDesdeParam : fechaDesde;
            Date fechaHastaAux = fechaHastaParam != null ? fechaHastaParam : fechaHasta;

            // Guardar en sesión
            model.addAttribute("reservaId", reservaIdAux);
            model.addAttribute("estadiaId", estadiaIdAux);
            model.addAttribute("numeroHabitacion", numeroHabitacionAux);
            model.addAttribute("fechaDesde", fechaDesdeAux);
            model.addAttribute("fechaHasta", fechaHastaAux);
            model.addAttribute("personasResultados", new ArrayList<>());

            if (huespedCargado == null) {
                ocupantesCargados = new ArrayList<>();
            }

            model.addAttribute("huespedCargado", huespedCargado);
            model.addAttribute("ocupantesCargados", ocupantesCargados);

            System.out.println("\n valores en sesion...\n\n");
            System.out.println("reservaId: " + reservaId + " \n");
            System.out.println("estadiaId: " + estadiaId + " \n");
            System.out.println("numeroHabitacion: " + numeroHabitacion + " \n");
            System.out.println("fechaDesde: " + fechaDesde + " \n");
            System.out.println("fechaHasta: " + fechaHasta + " \n");
            System.out.println("huespedCargado: " + huespedCargado + " \n");
            System.out.println("ocupantesCargados: " + ocupantesCargados + " \n");

            System.out.println("\n llego al final de iniciarCarga() \n");

            return "ocupacion/cargar";

        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

    /**
     * 2) BUSCAR HUESPEDES
     */
    @PostMapping("/buscar-huesped")
    public String buscarHuesped(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String documento,
            Model model,
            @SessionAttribute(value = "huespedCargado", required = false) Huesped huespedCargado,
            @ModelAttribute("ocupantesCargados") List<PersonaFisica> ocupantesCargados,
            @ModelAttribute("personasResultados") List<PersonaFisica> personasResultados,
            RedirectAttributes redirect
    ) {

        try {
            System.out.println("\n\n llego a buscarHuesped() \n\n");

            TipoDocumento tipo = null;

            // Tipo de documento no valido
            if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
                try {
                    tipo = TipoDocumento.valueOf(tipoDocumento);
                } catch (Exception ignored) {
                    tipo = null;
                }
            }

            // Primero busco un huesped
            if (huespedCargado == null) {
                personasResultados = gestionHuesped.buscarHuesped(apellido, nombre, tipo, documento);
            } else {
                personasResultados = gestionHuesped.buscarPersona(apellido, nombre, tipo, documento);
            }

            model.addAttribute("personasResultados", personasResultados);

            model.addAttribute("huespedCargado", huespedCargado);
            model.addAttribute("ocupantesCargados", ocupantesCargados);

            System.out.println("\n valores en sesion...\n\n");
            System.out.println("huespedCargado: " + huespedCargado + " \n");
            System.out.println("ocupantesCargados: " + ocupantesCargados + " \n");

            System.out.println("\n llego al final de buscarHuesped() \n");

            return "ocupacion/cargar";
        } catch (Exception e) {
            
            e.printStackTrace();

            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

    /**
     * 3) AGREGAR OCUPANTE A LA LISTA
     */
    @PostMapping("/agregar-huesped")
    public String agregarOcupante(
            @RequestParam(value = "personasSeleccionadas", required = false) List<Integer> idsSeleccionados,
            Model model,
            @SessionAttribute(value = "huespedCargado", required = false) Huesped huespedCargado,
            @ModelAttribute("ocupantesCargados") List<PersonaFisica> ocupantesCargados,
            @ModelAttribute("personasResultados") List<PersonaFisica> personasResultados,
            RedirectAttributes redirect
    ) {

        try {
            System.out.println("\n\n llego a agregarOcupante() \n\n");

            List<PersonaFisica> seleccionadas = new ArrayList<>();

            if (idsSeleccionados != null && !idsSeleccionados.isEmpty()) {
                // Personas seleccionadas desde sesión usando ID
                seleccionadas = personasResultados.stream()
                        .filter(p -> idsSeleccionados.contains(p.getId()))
                        .toList();
            }

            if (seleccionadas.isEmpty()) {
                redirect.addFlashAttribute("errorMessage", "No se encontraron las personas seleccionadas en sesión.");
                return "redirect:/ocupacion/cargar";
            }

            if (huespedCargado == null) {

                Huesped huesped = gestionHuesped.obtenerHuesped(seleccionadas.getFirst());

                if (huesped == null) {
                    redirect.addFlashAttribute("errorMessage", "Hubo un error al cargar el Huesped.");
                    return "redirect:/ocupacion/cargar";
                }

                model.addAttribute("huespedCargado", huesped);
                System.out.println("\n huesped: " + huesped + " \n");
            } else {
                model.addAttribute("huespedCargado", huespedCargado);
            }

            // Agregar acompañantes
            seleccionadas.forEach(p -> {
                if (!ocupantesCargados.contains(p)) {
                    ocupantesCargados.add(p);
                }
            });

            model.addAttribute("ocupantesCargados", ocupantesCargados);
            model.addAttribute("personasResultados", new ArrayList<>());

            System.out.println("\n valores en sesion...\n\n");
            System.out.println("huespedCargado: " + huespedCargado + " \n");
            System.out.println("ocupantesCargados: " + ocupantesCargados + " \n");

            redirect.addFlashAttribute("successMessage", "Huesped agregado correctamente");

            System.out.println("\n llego al final de agregarOcupante() \n");

            return "redirect:/ocupacion/resumen-ocupacion";

        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

    /**
     * 4) RESUMEN FINAL
     */
    @GetMapping("/resumen-ocupacion")
    @SuppressWarnings("CallToPrintStackTrace")
    public String resumen(
            Model model,
            @ModelAttribute("reservaId") Integer reservaId,
            @ModelAttribute("estadiaId") Integer estadiaId,
            @ModelAttribute("numeroHabitacion") Integer numeroHabitacion,
            @ModelAttribute("fechaDesde")
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaDesde,
            @ModelAttribute("fechaHasta")
            @DateTimeFormat(pattern = "EEE MMM dd HH:mm:ss zzz yyyy") Date fechaHasta,
            @ModelAttribute("huespedCargado") Huesped huespedCargado,
            @ModelAttribute("ocupantesCargados") List<PersonaFisica> ocupantesCargados,
            RedirectAttributes redirect
    ) {

        try {

            System.out.println("\n\n llego a resumen() \n\n");

            Estadia estadia;

            // 1) Si NO hay estadia creada → CREARLA
            if (estadiaId == null) {

                System.out.println("Estadia NO existe. Se creeara la estadia.\n");

                // Validaciones
                if (huespedCargado == null) {
                    System.out.println("ERROR: Debe seleccionar un huésped antes de continuar. " + huespedCargado + "\n");

                    redirect.addFlashAttribute("errorMessage", "Debe seleccionar un huésped antes de continuar.");
                    return "redirect:/ocupacion/cargar";
                }

                if (numeroHabitacion == null || fechaDesde == null || fechaHasta == null) {
                    System.out.println(
                            "ERROR: \n"
                            + "numeroHabitacion: " + numeroHabitacion + "\n"
                            + "fechaDesde: " + fechaDesde + "\n"
                            + "fechaHasta: " + fechaHasta + "\n"
                    );

                    redirect.addFlashAttribute("errorMessage", "Faltan datos de habitación o fechas.");
                    return "redirect:/ocupacion/cargar";
                }

                Reserva reservaAUsar = estadiaService.procesarReserva(huespedCargado, numeroHabitacion, fechaDesde, fechaHasta, reservaId);

                // Guardar ID en sesión
                reservaId = reservaAUsar.getIdReserva();
                model.addAttribute("reservaId", reservaId);

                estadia = estadiaService.iniciarCarga(huespedCargado, numeroHabitacion, fechaDesde, fechaHasta, reservaAUsar);

                // Guardar ID en sesión
                estadiaId = estadia.getIdEstadia();
                model.addAttribute("estadiaId", estadiaId);

                System.out.println(">>> Estadia creada con ID: " + estadiaId + "\n");

            } else {

                // Si ya existe la estadia -> actualizar acompañantes
                System.out.println("Estadia ya existente. Se debe actualizaran los acmpañantes.\n");

                estadia = estadiaService.obtenerEstadia(estadiaId);

                if (estadia == null) {
                    redirect.addFlashAttribute("errorMessage", "Error: la estadía no existe.");
                    return "redirect:/ocupacion/cargar";
                }

                // Actualizar acompañantes
                if (estadiaService.validarCapacidadHabitacion(numeroHabitacion, ocupantesCargados.size())) {
                    estadiaService.agregarAcompanantes(reservaId, ocupantesCargados);
                } else {
                    redirect.addFlashAttribute("errorMessage", "Error: no hay suficiente capacidad en esta habitacion.");
                    return "redirect:/ocupacion/cargar";
                }

                System.out.println(">>> Estadia actualizada correctamente.\n");
            }

            // ---- fin lógica creación/actualización ----
            model.addAttribute("reservaId", reservaId);
            model.addAttribute("estadiaId", estadiaId);
            model.addAttribute("numeroHabitacion", numeroHabitacion);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);
            model.addAttribute("huespedCargado", huespedCargado);
            model.addAttribute("ocupantesCargados", ocupantesCargados);

            System.out.println("\n valores en sesion...\n\n");

            System.out.println("reservaId: " + reservaId + " \n");
            System.out.println("estadiaId: " + estadiaId + " \n");
            System.out.println("numeroHabitacion: " + numeroHabitacion + " \n");
            System.out.println("fechaDesde: " + fechaDesde + " \n");
            System.out.println("fechaHasta: " + fechaHasta + " \n");
            System.out.println("huespedCargado: " + huespedCargado + " \n");
            System.out.println("ocupantesCargados: " + ocupantesCargados + " \n");

            System.out.println("\n llego al final de resumen() \n\n");

            return "ocupacion/resumen";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() + "\n");

            e.printStackTrace();

            redirect.addFlashAttribute("errorMessage", "Ocurrió un error inesperado: " + e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

    /**
     * 5) CARGAR OTRA HABITACION
     */
    @PostMapping("/limpiar-ocupantes-y-validar")
    public String limpiarOcupantesYValidar(Model model) {

        // limpiar SOLO los ocupantes
        model.addAttribute("ocupantesCargados", new ArrayList<>());

        // Reenvio el mismo POST a validar-fecha // NO redirect
        return "forward:/habitacion/validar-fecha";
    }

}
