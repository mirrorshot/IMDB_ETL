package it.mirrorshot.imdb_etl;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

@Slf4j
public class Tokenizer {
    /* to easily hide this main from application * /
    public static void main(String... args) throws Exception {
        try (Stream<Path> paths = Files.find(
                Path.of("data"),
                5,
                (p, a) -> p.endsWith("data.tsv"))
        ) {
            paths.forEach(path -> tokenize(path, 5, 1));
        }
    }
    /**/

    static void tokenize(Path path, int tokenSize, int tokenLimit) {
        final String p = path.toString();
        final int end = p.lastIndexOf('.');
        final String filename = p.substring(0, end);
        final String extension = p.substring(end);

        try (LineIterator inLines = IOUtils.lineIterator(new BufferedReader(new FileReader(FileUtils.getFile(filename + extension))))) {
            log.info("tokenization of {} at chunk size {} started...", p, tokenSize);
            final String header = inLines.nextLine();
            int i = 0;
            while (canContinue(tokenLimit, i) && inLines.hasNext()) {
                final String destination = filename + "_" + i + extension;
                try (Writer out = new BufferedWriter(new FileWriter(FileUtils.getFile(destination)))) {
                    int l = 0;
                    IOUtils.writeLines(Collections.singleton(header), "\n", out);
                    while (l < tokenSize && inLines.hasNext()) {
                        IOUtils.writeLines(Collections.singleton(inLines.nextLine()), "\n", out);
                        l++;
                    }
                    out.flush();
                }
                log.info("chunk done: {}", destination);
                i++;
            }
        } catch (Exception e) {
            log.error("failed to tokenize: {}", path, e);
            throw new RuntimeException("%s failed!".formatted(path), e);
        }
    }

    static boolean canContinue(int tokenLimit, int tokenNumber) {
        return tokenLimit < 0 || tokenNumber < tokenLimit;
    }
}
