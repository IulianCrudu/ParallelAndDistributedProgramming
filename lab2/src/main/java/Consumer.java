import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    final private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int sum = 0;
        while(true) {
            try {
                Integer number = queue.take();
                if(number == Integer.MIN_VALUE)
                    break;
                sum += number;
            } catch(Exception e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("The sum is: " + sum);
    }
}
