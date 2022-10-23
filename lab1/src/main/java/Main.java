import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final int NUMBER_OF_THREADS = 1000;
    private static final int MAXIMUM_NUMBER_OF_BILLS = 300;
    private static final int CHECK_DELAY = 20;
    private static final int CHECK_COUNT = 30;

    public static void main(String[] args) {
        System.out.println("Starting the program...");
        final LocalTime start = LocalTime.now();
        final Inventory inventory = new Inventory();
        final Random random = new Random();
        final List<Thread> threads = new ArrayList<>();

        final Thread checkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < CHECK_COUNT; i++) {
                    try {
                        Thread.sleep(CHECK_DELAY);
                        System.out.println("Start check " + i);
                        inventory.audit();
                        System.out.println("End check " + i);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        for(int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> inventory.generateBills(random.nextInt(MAXIMUM_NUMBER_OF_BILLS))));
        }

        checkThread.start();
        threads.forEach(Thread::start);

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            checkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.audit();

        final LocalTime finish = LocalTime.now();
        System.out.println("Finished: " + finish);
        System.out.println("Lasted: " + start.until(finish, ChronoUnit.MILLIS));
    }
}
