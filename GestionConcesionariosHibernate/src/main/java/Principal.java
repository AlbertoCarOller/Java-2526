import exception.GestorException;
import jakarta.persistence.PersistenceException;
import service.GestorService;

void main() {
    try {
        GestorService gestorService = new GestorService();
        gestorService.cargarDatosPrueba();
        gestorService.darAltaConcesionario("Concesionario BellaVista", "Calle Vista 33");
        gestorService.darAltaCoche("1234BBC", "Hyundai", "Khonda", 20000, 2);
        System.out.println(gestorService.instalarExtra("1234BBC", 1) + " EUR");
        gestorService.registrarReparacion("7865ZYX", 1, "02/01/2026", 300,
                "Cambio de rueda");
        gestorService.venderCoche("17253971M", "Carla Rivas", "5519PUF", 1,
                5345);
    } catch (GestorException | PersistenceException | DateTimeParseException e) {
        System.out.println(e.getMessage());
    }
}