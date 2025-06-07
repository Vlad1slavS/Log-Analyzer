package io.github.logprocessor.Service.Impl;

import io.github.logprocessor.Service.FileService;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileServiceImpl implements FileService {

    @Override
    public List<Path> findLogFiles(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            throw new IOException("Папка не найдена: " + directory);
        }

        if (!Files.isDirectory(directory)) {
            throw new IOException("Это не папка: " + directory);
        }

        List<Path> logFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString().toLowerCase();
                    if (fileName.endsWith(".log")) {
                        logFiles.add(file);
                    }
                }
            }
        }

        return logFiles;
    }

    @Override
    public List<String> readAllLines(Path file) throws IOException {
        return Files.readAllLines(file);
    }

    @Override
    public void createDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectory(directory);
        }
    }

    @Override
    public void writeLines(Path file, List<String> lines) throws IOException {
        Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
