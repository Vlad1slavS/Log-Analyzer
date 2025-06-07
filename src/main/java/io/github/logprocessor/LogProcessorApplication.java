package io.github.logprocessor;

import io.github.logprocessor.Service.LogProcessorService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Главный класс приложения для обработки логов транзакций.
 * Использование: java -jar log-processor.jar <путь-к-входной-директории>
 * Или запуск с последующим вводом пути к входной директории.
 */
public class LogProcessorApplication {

    public static void main(String[] args) {
        Path inputDirectory = null;
        try {
            switch (args.length) {
                case 0:
                    System.out.println("Enter the path to the input directory:");
                    Scanner scanner = new Scanner(System.in);
                    inputDirectory = Paths.get(scanner.nextLine());
                    scanner.close();
                    break;
                case 1:
                    inputDirectory = Paths.get(args[0]);
                    break;
                default:
                    System.err.println("Error: Invalid number of arguments.");
                    System.exit(1);
            }

            LogProcessorService processor = new LogProcessorService();

            System.out.println("Starting log processing...");
            System.out.println("Input directory: " + inputDirectory.toAbsolutePath());

            processor.processLogs(inputDirectory);

            System.out.println("Log processing completed successfully!");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Unexpected error occurred");
            e.printStackTrace(); // Для отладки
            System.exit(1);
        }

    }
}
