package parking1;
import java.util.concurrent.Semaphore;

class Principal {
    public static void main(String[] args) {
        Semaphore plaza= new Semaphore(5);
        for(int x=0;x<10;x++)
        {
            Coche hiloCoche= new Coche("coche numero: "+(x+1),plaza);
            Thread th= new Thread(hiloCoche);
            th.start();
        }
    }
}
class Coche implements Runnable {
    String numeroCoche;
    Semaphore sem, pintar;
    int aleParking=(int)(Math.random()*100);
    int aleCirculando=(int) (Math.random()*50);
    public Coche(String numeroCoche, Semaphore sem)
    {
        this.numeroCoche=numeroCoche;
        this.sem=sem;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(100+aleCirculando*100);
            sem.acquire(); // para que solo entren los coches que caben
            System.out.println("------------"+sem.getQueueLength()+" coches en la cola");
            System.out.println(numeroCoche+" entra al parking");
            System.out.println(sem.availablePermits()+" plazas libres");
            Thread.sleep(1000+aleParking*100);
            System.out.println(numeroCoche+" sale del parking");
            System.out.println(sem.availablePermits()+" plazas libres");
            System.out.println("------------"+sem.getQueueLength()+" coches en la cola");
            sem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}