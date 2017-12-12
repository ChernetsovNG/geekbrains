package ru.geekbrains.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static boolean isFolderExists(String pathToFolder) {
        Path path = Paths.get(pathToFolder);
        return Files.exists(path);
    }

    public static boolean isFileExists(String pathToFolder, String fileName) {
        Path path = Paths.get(pathToFolder, fileName);
        return Files.exists(path);
    }

    public static boolean createNewFile(String pathToFolder, String fileName, byte[] filePayload) {
        Path path = Paths.get(pathToFolder, fileName);
        try {
            Files.write(path, filePayload, StandardOpenOption.CREATE_NEW);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static List<String> getFileList(String pathToFolder) {
        try (Stream<Path> paths = Files.walk(Paths.get(pathToFolder))) {
            return paths
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
