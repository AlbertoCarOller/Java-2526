import exception.GestorException;
import service.GestorService;

void main() {
    try {
        GestorService gestorService = new GestorService();
        gestorService.cargarDatosPrueba();
    } catch (GestorException e) {
        throw new RuntimeException(e);
    }
}