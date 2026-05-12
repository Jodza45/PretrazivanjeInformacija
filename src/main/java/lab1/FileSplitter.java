package lab1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileSplitter {
    private FileSplitter() {
    }

    public static List<Path> splitCollection(Path sourceDirectory, Path targetDirectory, int partsPerFile) throws IOException {
        FileUtils.recreateDirectory(targetDirectory);
        List<Path> created = new ArrayList<>();
        try (Stream<Path> files = Files.list(sourceDirectory)) {
            List<Path> originals = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .sorted()
                    .toList();

            for (Path original : originals) {
                String baseName = removeExtension(original.getFileName().toString());
                Path fileTarget = targetDirectory.resolve(baseName);
                created.addAll(splitFile(original, fileTarget, partsPerFile));
            }
        }
        return created;
    }

    public static List<Path> splitFile(Path sourceFile, Path targetDirectory, int numberOfParts) throws IOException {
        if (numberOfParts <= 0) {
            throw new IllegalArgumentException("numberOfParts must be greater than zero");
        }

        Files.createDirectories(targetDirectory);
        String text = Files.readString(sourceFile, StandardCharsets.UTF_8);
        int length = text.length();
        int partSize = Math.max(1, (int) Math.ceil(length / (double) numberOfParts));

        List<Path> created = new ArrayList<>();
        String baseName = removeExtension(sourceFile.getFileName().toString());
        for (int i = 0; i < numberOfParts; i++) {
            int start = Math.min(i * partSize, length);
            int end = Math.min(start + partSize, length);
            String part = text.substring(start, end);
            if (part.isEmpty()) {
                part = " ";
            }
            Path partPath = targetDirectory.resolve(String.format("%s_part_%03d.txt", baseName, i + 1));
            Files.writeString(partPath, part, StandardCharsets.UTF_8);
            created.add(partPath);
        }
        return created;
    }

    private static String removeExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot <= 0) {
            return filename;
        }
        return filename.substring(0, dot);
    }
}
