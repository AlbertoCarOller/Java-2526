package Tema2.TareaParking;

public class Parking {
    // El parking va a tener 10 espacios para almacenar los vehículos
    static String[] espacios = new String[10];

    /* IMPORTANTE: LAS FUNCIONES AL TENER LA COMBINACIÓN static synchronized LA CLASE MISMA SE CONVIERTE EN LA LLAVE MAESTRA,
     * ESTO ES IMPORTANTE EN ESTE CASO, PORQUE LOS COCHES NO SE PISAN (LOS HILOS), LAS FUNCIONES SON ESTÁTICAS PORQUE EL
     * CERROJO ES ESTÁTICO Y DE ESTA FORMA ES COMO FUNCIONA, SI EL CERROJO ES ESTÁTICO LA FUNCIÓN QUE LA UTILICE TAMBIÉN
     * DEBE DE SERLO */

    /**
     * Creamos una función que va a guardar el identificador de un coche en el array
     *
     * @param identificacion la identificacion (matrícula del coche que se va a guardar)
     */
    public static synchronized void guardarCoche(String identificacion) {
        int posicion = encontrarPrimerNull();
        if (posicion != -1) {
            System.out.println("El coche " + identificacion + " entra al parking y se queda con la plaza  " + posicion);
            espacios[posicion] = identificacion;
        }
    }

    /**
     * Esta función va a eliminar el coche con ese identificador
     * del coche
     *
     * @param identificacion la identicación del coche (matrícula)
     */
    public static synchronized void eliminarCoche(String identificacion) {
        // En caso de que el array no esté vacío y el coche esté dentro
        if (encontrarCoche(identificacion) != -1) {
            // Borramos el coche del parking
            espacios[encontrarCoche(identificacion)] = null;
        }
    }

    /**
     * Esta función va a devolver la posición del primer null encontrado
     *
     * @return la posición donde se encuentra el primer null el espacio libre
     */
    private static int encontrarPrimerNull() {
        for (int i = 0; i < espacios.length; i++) {
            if (espacios[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Esta función va a comprobar si el coche está presente en el parking
     *
     * @param identificacion la identificación del coche a buscar para eliminar
     * @return devuelve si se ha encontrado o no el coche (posición o -1)
     */
    private static int encontrarCoche(String identificacion) {
        for (int i = 0; i < espacios.length; i++) {
            // En caso de que sea null pasamos a la siguiente
            if (espacios[i] == null) {
                continue;
            }
            if (espacios[i].equals(identificacion)) {
                return i;
            }
        }
        return -1;
    }

    //------------ APARTADO DE FURGONETAS ------------

    /**
     * Creamos una función que va a guardar el identificador de una furgoneta en el array
     *
     * @param identificacion la identificacion (matrícula de la furgoneta que se va a guardar)
     */
    public static synchronized void guardarFurgoneta(String identificacion) {
        int posicion = encontrarNulos();
        if (posicion != -1) {
            System.out.println("Ha entrado la furgoneta " + identificacion + "ocupando las plazas " + posicion + " y " + posicion + 1);
            // Ocupa dos plazas la furgoneta
            espacios[posicion] = identificacion;
            espacios[posicion + 1] = identificacion;
        }
    }

    /**
     * Esta función va a eliminar la furgoneta con ese identificador
     * del coche
     *
     * @param identificacion la identicación de la furgoneta (matrícula)
     */
    public static synchronized void eliminarFurgoneta(String identificacion) {
        // En caso de que el array no esté vacío y el coche esté dentro
        if (encontrarFurgoneta(identificacion) != -1) {
            // Borramos el coche del parking
            espacios[encontrarCoche(identificacion)] = null;
            espacios[encontrarCoche(identificacion)] = null;
        }
    }

    /**
     * Esta función va a devolver la posición del primer null encontrado y comprueba que después haya otro sigue
     *
     * @return la posición donde se encuentra el primer null el espacio libre
     */
    private static int encontrarNulos() {
        for (int i = 0; i < espacios.length - 1; i++) {
            if (espacios[i] == null && espacios[i + 1] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Esta función va a comprobar si la furgoneta está presente en el parking
     *
     * @param identificacion la identificación de la furgoneta a buscar para eliminar
     * @return devuelve si se ha encontrado o no la furgoneta (posición o -1)
     */
    private static int encontrarFurgoneta(String identificacion) {
        for (int i = 0; i < espacios.length; i++) {
            // En caso de que sea null pasamos a la siguiente
            if (espacios[i] == null) {
                continue;
            }
            if (espacios[i].equals(identificacion)) {
                return i;
            }
        }
        return -1;
    }
}
