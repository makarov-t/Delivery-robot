import java.util.*;
import java.util.concurrent.*;

public class RobotRouteAnalyzer {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int threadsCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < threadsCount; i++) {
            futures.add(executorService.submit(() -> {
                String route = generateRoute("RLRFR", 100);
                int rCount = countChar(route, 'R');

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(rCount, 1, Integer::sum);
                }

                return rCount;
            }));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        printStatistics();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    private static int countChar(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }

    private static void printStatistics() {
        if (sizeToFreq.isEmpty()) {
            System.out.println("Нет данных для анализа");
            return;
        }
        
        Map.Entry<Integer, Integer> maxEntry = Collections.max(
                sizeToFreq.entrySet(),
                Map.Entry.comparingByValue()
        );

        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n",
                maxEntry.getKey(), maxEntry.getValue());

        System.out.println("Другие размеры:");
        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (!entry.getKey().equals(maxEntry.getKey())) {
                        System.out.printf("- %d (%d раз)\n", entry.getKey(), entry.getValue());
                    }
                });
    }
}