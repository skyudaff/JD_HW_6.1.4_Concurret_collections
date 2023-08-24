import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {
    static final BlockingQueue<String> queueForA = new ArrayBlockingQueue<>(100);
    static final BlockingQueue<String> queueForB = new ArrayBlockingQueue<>(100);
    static final BlockingQueue<String> queueForC = new ArrayBlockingQueue<>(100);
    static int maxCountA = 0;
    static int maxCountB = 0;
    static int maxCountC = 0;
    static String maxTextA = "";
    static String maxTextB = "";
    static String maxTextC = "";


    public static void main(String[] args) {
        System.out.println("Начало работы");

        Thread textGeneratorFillQueue = new Thread(() -> {
            System.out.println("Генерация текстов и заполнение очередей:");
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueForA.put(text);
                    queueForB.put(text);
                    queueForC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.println("Генерация текстов и заполнение очередей окончены");
        });
        textGeneratorFillQueue.start();

        Thread threadA = new Thread(() -> getMaxChars(queueForA, 'a', "Поток A"));
        Thread threadB = new Thread(() -> getMaxChars(queueForB, 'b', "Поток B"));
        Thread threadC = new Thread(() -> getMaxChars(queueForC, 'c', "Поток С"));
        threadA.start();
        threadB.start();
        threadC.start();

        try {
            textGeneratorFillQueue.join();
            threadA.interrupt();
            threadB.interrupt();
            threadC.interrupt();
        } catch (InterruptedException e) {
            return;
        }


        System.out.println("\nМаксимальное количество символов 'a' - " + maxCountA + "\nв тексте: " + maxTextA);
        System.out.println("\nМаксимальное количество символов 'b' - " + maxCountB + "\nв тексте: " + maxTextB);
        System.out.println("\nМаксимальное количество символов 'c' - " + maxCountC + "\nв тексте: " + maxTextC);
        System.out.println("Программа завершена");
    }

    public static int getCharCount(String text, char ch) {
        return (int) text.chars().filter(c -> c == ch).count();
    }

    public static synchronized void getMaxCount(String text, char ch, int count, String threadName) {
        switch (ch) {
            case 'a' -> {
                if (count > maxCountA) {
                    maxCountA = count;
                    maxTextA = text;
                    System.out.println(threadName + " получил новый максимум 'a' - " + maxCountA);
                }
            }
            case 'b' -> {
                if (count > maxCountB) {
                    maxCountB = count;
                    maxTextB = text;
                    System.out.println(threadName + " получил новый максимум 'b' - " + maxCountB);
                }
            }
            case 'c' -> {
                if (count > maxCountC) {
                    maxCountC = count;
                    maxTextC = text;
                    System.out.println(threadName + " получил новый максимум 'c' - " + maxCountC);
                }
            }
        }
    }

    public static void getMaxChars(BlockingQueue<String> queue, char ch, String threadName) {
        while (true) {
            try {
                String text = queue.take();
                int count = getCharCount(text, ch);
                getMaxCount(text, ch, count, threadName);
            } catch (InterruptedException e) {
                return;
            }
        }
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
