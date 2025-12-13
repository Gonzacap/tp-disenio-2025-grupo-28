package tp.tp_disenio_2025_grupo_28.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
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

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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
    public String fechaDesde() {
        return null;
    }

    @ModelAttribute("fechaHasta")
    public String fechaHasta() {
        return null;
    }

    @ModelAttribute("numeroHabitacion")
    public Integer numeroHabitacion() {
        return null;
    }

    @ModelAttribute("errorMessage")
    public String errorMessage() {
        return null;
    }

    @ModelAttribute("successMessage")
    public String successMessage() {
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
            @RequestParam(required = false) String fechaDesdeParam,
            @RequestParam(required = false) String fechaHastaParam,
            @ModelAttribute("reservaId") Integer reservaId,
            @ModelAttribute("estadiaId") Integer estadiaId,
            @ModelAttribute("numeroHabitacion") Integer numeroHabitacion,
            @ModelAttribute("fechaDesde") String fechaDesde,
            @ModelAttribute("fechaHasta") String fechaHasta,
            @ModelAttribute("errorMessage") String errorMessage,
            @ModelAttribute("successMessage") String successMessage,
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
            String fechaDesdeAux = fechaDesdeParam != null ? fechaDesdeParam : fechaDesde;
            String fechaHastaAux = fechaHastaParam != null ? fechaHastaParam : fechaHasta;

            // Guardar en sesión
            model.addAttribute("reservaId", reservaIdAux);
            model.addAttribute("estadiaId", estadiaIdAux);
            model.addAttribute("fechaDesde", fechaDesdeAux);
            model.addAttribute("fechaHasta", fechaHastaAux);
            model.addAttribute("personasResultados", new ArrayList<>());

            model.addAttribute("numeroHabitacion", numeroHabitacionAux);

            model.addAttribute("huespedCargado", huespedCargado);

            // Primero cargo el huesped
            if (huespedCargado == null) {
                ocupantesCargados = new ArrayList<>();
            }

            model.addAttribute("ocupantesCargados", ocupantesCargados);

            System.out.println("\n\n reservaId: " + reservaId + " \n\n");
            System.out.println("\n\n estadiaId: " + estadiaId + " \n\n");
            System.out.println("\n\n numeroHabitacion: " + numeroHabitacion + " \n\n");
            System.out.println("\n\n fechaDesde: " + fechaDesde + " \n\n");
            System.out.println("\n\n fechaHasta: " + fechaHasta + " \n\n");
            System.out.println("\n\n huespedCargado: " + huespedCargado + " \n\n");
            System.out.println("\n\n ocupantesCargados: " + ocupantesCargados + " \n\n");

            System.out.println("\n\n llego al final de iniciarCarga() \n\n");

            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("successMessage", successMessage);

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
            @ModelAttribute("huespedCargado") Huesped huespedCargado,
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

            System.out.println("\n\n huespedCargado: " + huespedCargado + " \n\n");
            System.out.println("\n\n ocupantesCargados: " + ocupantesCargados + " \n\n");

            System.out.println("\n\n llego al final de buscarHuesped() \n\n");

            return "ocupacion/cargar";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

    /**
     * 3) AGREGAR OCUPANTE A LA LISTA
     */
    @PostMapping("/agregar-huesped")
    public String agregarOcupante(
            // @RequestParam List<PersonaFisica> personasSeleccionadas,
            @RequestParam(name = "personasSeleccionadas") List<String> cuitsSeleccionados,
            Model model,
            @ModelAttribute("huespedCargado") Huesped huespedCargado,
            @ModelAttribute("ocupantesCargados") List<PersonaFisica> ocupantesCargados,
            @ModelAttribute("personasResultados") List<PersonaFisica> personasResultados,
            RedirectAttributes redirect
    ) {

        try {
            System.out.println("\n\n llego a agregarOcupante() \n\n");

            // CUITs que NO están en cargados
            List<String> cuitsFaltantes = cuitsSeleccionados.stream()
                    .filter(cuit -> ocupantesCargados.stream()
                    .noneMatch(p -> cuit.equals(p.getCuit())))
                    .toList();

            // Recupero las personas desde la lista de sesión cagada anteriormente por su CUIT
            List<PersonaFisica> seleccionadas = cuitsFaltantes.stream()
                    .map(cuit -> personasResultados.stream()
                    .filter(p -> cuit != null && cuit.equals(p.getCuit()))
                    .findFirst()
                    .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            if (seleccionadas.isEmpty()) {

                System.out.println("\n\n redirige a /cargar \n\n");

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
                System.out.println("\n\n huesped: " + huesped + " \n\n");
            } else {
                model.addAttribute("huespedCargado", huespedCargado);
            }

            // Agregar acompañantes
            for (PersonaFisica persona : seleccionadas) {
                if (!ocupantesCargados.contains(persona)) {
                    ocupantesCargados.add(persona);
                }
            }

            model.addAttribute("ocupantesCargados", ocupantesCargados);
            model.addAttribute("personasResultados", new ArrayList<>());

            System.out.println("\n\n huespedCargado: " + huespedCargado + " \n\n");
            System.out.println("\n\n ocupantesCargados: " + ocupantesCargados + " \n\n");

            redirect.addFlashAttribute("successMessage", "Huesped agregado correctamente");

            System.out.println("\n\n llego al final de agregarOcupante() \n\n");

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
    public String resumen(
            Model model,
            @ModelAttribute("reservaId") Integer reservaId,
            @ModelAttribute("estadiaId") Integer estadiaId,
            @ModelAttribute("numeroHabitacion") Integer numeroHabitacion,
            @ModelAttribute("fechaDesde") String fechaDesde,
            @ModelAttribute("fechaHasta") String fechaHasta,
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


                /*******************
                 * aca algo esta tirando un error
                 *******************/

                // Validaciones
                if (huespedCargado == null) {
                    System.out.println("ERROR: Debe seleccionar un huésped antes de continuar. " + huespedCargado);

                    redirect.addFlashAttribute("errorMessage", "Debe seleccionar un huésped antes de continuar.");
                    return "redirect:/ocupacion/cargar";
                }

                if (numeroHabitacion == null || fechaDesde == null || fechaHasta == null) {
                    System.out.println(
                        "ERROR: Debe seleccionar un huésped antes de continuar." + "\n" + 
                        "numeroHabitacion: " + numeroHabitacion + "\n" +
                        "fechaDesde: " + fechaDesde + "\n" +
                        "fechaHasta: " + fechaHasta + "\n"
                    );

                    redirect.addFlashAttribute("errorMessage", "Faltan datos de habitación o fechas.");
                    return "redirect:/ocupacion/cargar";
                }

                Date fDesde = Date.valueOf(fechaDesde);
                Date fHasta = Date.valueOf(fechaHasta);

                estadia = estadiaService.iniciarCarga(huespedCargado, numeroHabitacion, fDesde, fHasta, reservaId);

                // Guardar ID en sesión
                estadiaId =  estadia.getIdEstadia();
                model.addAttribute("estadiaId",estadiaId);

                System.out.println(">>> Estadia creada con ID: " + estadiaId);

            } else {

                // Si ya existe la estadia -> actualizar acompañantes
                System.out.println("Estadia ya existente. Se debe actualizaran los acmpañantes.");

                estadia = estadiaService.obtenerEstadia(estadiaId);

                if (estadia == null) {
                    redirect.addFlashAttribute("errorMessage", "Error: la estadía no existe.");
                    return "redirect:/ocupacion/cargar";
                }

                // Actualizar acompañantes
                estadiaService.agregarAcompanantes(reservaId, ocupantesCargados);

                System.out.println(">>> Estadia actualizada correctamente.");
            }

            // ---- fin lógica creación/actualización ----
                        
            model.addAttribute("reservaId", reservaId);
            model.addAttribute("estadiaId", estadiaId);
            model.addAttribute("numeroHabitacion", numeroHabitacion);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);
            model.addAttribute("huespedCargado", huespedCargado);
            model.addAttribute("ocupantesCargados", ocupantesCargados);

            System.out.println("\n\n reservaId: " + reservaId + " \n\n");
            System.out.println("\n\n estadiaId: " + estadiaId + " \n\n");
            System.out.println("\n\n numeroHabitacion: " + numeroHabitacion + " \n\n");
            System.out.println("\n\n fechaDesde: " + fechaDesde + " \n\n");
            System.out.println("\n\n fechaHasta: " + fechaHasta + " \n\n");
            System.out.println("\n\n huespedCargado: " + huespedCargado + " \n\n");
            System.out.println("\n\n ocupantesCargados: " + ocupantesCargados + " \n\n");

            System.out.println("\n\n llego al final de resumen() \n\n");

            return "ocupacion/resumen";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());

            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ocupacion/cargar";
        }
    }

}
