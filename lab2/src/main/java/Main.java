import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final List<Integer> firstVector = Arrays.asList(1, 3, 3, 4, 5);
        final List<Integer> secondVector = Arrays.asList(1, 2, 3, 4, 5);

        int expectedSum = IntStream.range(0, firstVector.size()).map(index -> firstVector.get(index) * secondVector.get(index)).sum();
        System.out.println("Expected sum is: " + expectedSum);

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(firstVector.size());
        Producer producer = new Producer(queue, firstVector, secondVector);
        Consumer consumer = new Consumer(queue);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
    }
}
