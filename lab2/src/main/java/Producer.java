import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

public class Producer implements Runnable {
    final private BlockingQueue<Integer> queue;
    final private List<Integer> firstVector;
    final private List<Integer> secondVector;

    public Producer(BlockingQueue<Integer> queue, List<Integer> firstVector, List<Integer> secondVector) {
        this.queue = queue;
        this.firstVector = firstVector;
        this.secondVector = secondVector;
    }

    public BlockingQueue<Integer> getQueue() {
        return queue;
    }

    public List<Integer> getFirstVector() {
        return firstVector;
    }

    public List<Integer> getSecondVector() {
        return secondVector;
    }

    @Override
    public void run() {
        IntStream.range(0, firstVector.size())
                .forEach(index -> {
                    try {
                        queue.put(this.firstVector.get(index) * this.secondVector.get(index));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        try {
            queue.put(Integer.MIN_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
