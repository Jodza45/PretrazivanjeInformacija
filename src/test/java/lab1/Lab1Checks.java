package lab1;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Lab1Checks {
    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("lab1-checks-");
        try {
            verifiesSplitterCreatesRequestedNumberOfParts(temp);
            verifiesDirectorySizeCalculation(temp);
            System.out.println("All Lab1Checks passed.");
        } finally {
            deleteRecursively(temp);
        }
    }

    private static void verifiesSplitterCreatesRequestedNumberOfParts(Path temp) throws Exception {
        Path input = temp.resolve("sample.txt");
        Path output = temp.resolve("parts");
        String text = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Files.writeString(input, text, StandardCharsets.UTF_8);

        List<Path> parts = FileSplitter.splitFile(input, output, 10);

        require(parts.size() == 10, "Expected 10 split files, got " + parts.size());
        for (Path part : parts) {
            require(Files.exists(part), "Missing split file: " + part);
            require(Files.size(part) > 0, "Split file is empty: " + part);
        }
    }

    private static void verifiesDirectorySizeCalculation(Path temp) throws Exception {
        Path dir = temp.resolve("size");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("a.txt"), "abc", StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("b.txt"), "12345", StandardCharsets.UTF_8);

        long size = FileUtils.directorySize(dir);

        require(size == 8, "Expected directory size 8, got " + size);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void deleteRecursively(Path root) throws Exception {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
