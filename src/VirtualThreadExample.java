
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newVirtualThreadExecutor();

        executor.submit(() -> {
            System.out.println("Hello from a virtual thread!");
        });

        executor.shutdown();
    }
}
