package io.github.logprocessor.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Интерфейс для операций с файлами.
 */
public interface FileService {

    List<Path> findLogFiles(Path directory) throws IOException;

    List<String> readAllLines(Path file) throws IOException;

    void createDirectory(Path directory) throws IOException;

    void writeLines(Path file, List<String> lines) throws IOException;
}
