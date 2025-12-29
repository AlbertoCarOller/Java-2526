import exception.GestorException;
import service.GestorService;

void main() {
    try {
        GestorService gestorService = new GestorService();
        gestorService.cargarDatosPrueba();
        gestorService.darAltaConcesionario("Concesionario BellaVista", "Calle Vista 33");
        gestorService.darAltaCoche("1234BBC", "Hyundai", "Khonda", 20000, 2);
    } catch (GestorException e) {
        throw new RuntimeException(e);
    }
}