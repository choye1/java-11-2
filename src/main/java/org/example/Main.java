import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // создаем пул для выполнения наших задач
        //   максимальное количество созданных задач - 3
        //   ограничиваем размер очереди задач до 3
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        // сколько задач выполнилось
        AtomicInteger count = new AtomicInteger(0);

        // сколько задач выполняется
        AtomicInteger inProgress = new AtomicInteger(0);

        // отправляем задачи на выполнение
        for (int i = 0; i < 30; i++) {
            final int number = i;

            System.out.println("creating #" + number);

            // ждем, пока в очереди освободится место
            while (executor.getQueue().remainingCapacity() == 0) {
                Thread.sleep(10); // небольшая задержка, чтобы избежать активного ожидания
            }

            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                System.out.println("start #" + number + ", in progress: " + working);
                try {
                    // тут какая-то полезная работа
                    Thread.sleep(Math.round(1000 + Math.random() * 2000));
                } catch (InterruptedException e) {
                    // ignore
                }
                working = inProgress.decrementAndGet();
                System.out.println("end #" + number + ", in progress: " + working + ", done tasks: " + count.incrementAndGet());
                return null;
            });
        }

        // завершаем пул и ждем завершения всех задач
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
