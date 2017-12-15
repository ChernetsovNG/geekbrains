package ru.geekbrains.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.common.dto.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Comparator;
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

    public static List<FileInfo> getFileList(String pathToFolder) {
        try (Stream<Path> paths = Files.walk(Paths.get(pathToFolder))) {
            return paths
                .filter(Files::isRegularFile)
                .map(path -> {
                    File file = path.toFile();
                    return new FileInfo(file.getName(), file.length(), Instant.ofEpochMilli(file.lastModified()));
                })
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static boolean deleteDirectory(String pathToFolder) {
        Path pathFolderToDelete = Paths.get(pathToFolder);
        try {
            Files.walk(pathFolderToDelete)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
            return !Files.exists(pathFolderToDelete);  // если папка всё ещё не удалена, то false
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static boolean renameFile(String pathToFolder, String oldName, String newName) {
        Path fileToRenamePath = Paths.get(pathToFolder, oldName);
        Path fileAfterRenamePath = Paths.get(pathToFolder, newName);
        try {
            Files.move(fileToRenamePath, fileAfterRenamePath);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static boolean changeFileContent(String pathToFolder, String fileName, byte[] filePayload) {
        Path path = Paths.get(pathToFolder, fileName);
        try {
            Files.write(path, filePayload, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    public static boolean deleteFile(String pathToFolder, String fileName) {
        try {
            Files.delete(Paths.get(pathToFolder, fileName));
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }
}
