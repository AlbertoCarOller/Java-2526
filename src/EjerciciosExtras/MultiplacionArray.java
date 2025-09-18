package EjerciciosExtras;

public class MultiplacionArray {
    public static void main(String[] args) {
        System.out.println("Mayor multiplicación: " + multiplicarAdyscente(new int[] {1,7,3,4,5,13,7,3,9,11}));
    }

    public static int multiplicarAdyscente(int[] array) {
        int maximo = 0;
        for (int i = 0; i < array.length; i++) {
            int mult = 0;
            if ((i + 1) < array.length - 1) {
                mult = array[i] * (array[i + 1]);
                if (mult > maximo) {
                    maximo = mult;
                }
            }
        }
        return maximo;
    }
}