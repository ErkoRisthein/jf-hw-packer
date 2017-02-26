package org.zeroturnaround.jf.packer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.*;
import static org.apache.commons.io.IOUtils.copyLarge;

public class UncompressedDataPacker implements Packer {

  private static final Logger log = LoggerFactory.getLogger(UncompressedDataPacker.class);
  private static final int THE_ANSWER_TO_LIFE_THE_UNIVERSE_AND_EVERYTHING = 42;

  @Override
  public void pack(Path inputDir, Path outputArchive) throws IOException {
    log.info("Packing {} into {}", inputDir, outputArchive);
    validatePackArguments(inputDir, outputArchive);
    iterateDirectoryAndPackFiles(inputDir, outputArchive);
  }

  @Override
  public void unpack(Path inputArchive, Path outputDir) throws IOException {
    log.info("Unpacking {} into {}", inputArchive, outputDir);
    validateUnpackArguments(inputArchive, outputDir);
    unpackFilesFromArchive(inputArchive, outputDir);
  }

  private void validatePackArguments(Path inputDir, Path outputArchive) {
    if (inputDir == null || outputArchive == null || !isDirectory(inputDir) || isDirectory(outputArchive)) {
      throw new IllegalArgumentException("Invalid inputDir or outputArchive");
    }
  }

  private void iterateDirectoryAndPackFiles(Path inputDir, Path outputArchive) throws IOException {
    try (Stream<Path> files = walk(inputDir);
         DataOutputStream out = output(outputArchive)) {
      out.writeByte(THE_ANSWER_TO_LIFE_THE_UNIVERSE_AND_EVERYTHING);
      files.forEach(file -> {
        if (isRegularFile(file)) {
          appendFileToArchive(inputDir, out, file);
        }
      });
    }
  }

  private void appendFileToArchive(Path inputDir, DataOutputStream out, Path filePath) {
    File file = filePath.toFile();
    try (DataInputStream in = input(file)) {
      out.writeUTF(relativePath(inputDir, filePath));
      out.writeLong(file.length());
      copyLarge(in, out);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private DataOutputStream output(Path outputArchive) throws FileNotFoundException {
    return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputArchive.toFile())));
  }

  private DataInputStream input(File file) throws FileNotFoundException {
    return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
  }

  private String relativePath(Path path, Path other) {
    return path.relativize(other).toString().replace('\\', '/');
  }

  private void validateUnpackArguments(Path inputArchive, Path outputDir) {
    if (inputArchive == null || outputDir == null || !isRegularFile(inputArchive) || isRegularFile(outputDir)) {
      throw new IllegalArgumentException("Invalid inputArchive or outputDir");
    }
  }

  private void unpackFilesFromArchive(Path inputArchive, Path outputDir) throws IOException {
    try (DataInputStream in = input(inputArchive.toFile())) {
      readMagicNumber(in);
      createDirectory(outputDir);
      unpackFiles(in, outputDir);
    }
  }

  private void readMagicNumber(DataInputStream in) throws IOException {
    byte magicNumber = in.readByte();
    if (magicNumber != THE_ANSWER_TO_LIFE_THE_UNIVERSE_AND_EVERYTHING) {
      throw new IllegalArgumentException("Invalid archive");
    }
  }

  private void unpackFiles(DataInputStream in, Path outputDir) throws IOException {
    while (in.available() > 0) {
      Path file = createParentDirectory(in, outputDir);
      writeFile(in, file);
    }
  }

  private Path createParentDirectory(DataInputStream in, Path outputDir) throws IOException {
    String filePath = in.readUTF();
    Path relativePath = Paths.get(filePath);
    Path absolutePath = outputDir.resolve(relativePath);
    if (relativePath.getParent() != null) {
      createDirectories(absolutePath.getParent());
    }
    return absolutePath;
  }

  private void writeFile(DataInputStream in, Path outputFile) throws IOException {
    long fileSize = in.readLong();
    try (DataOutputStream out = output(outputFile)) {
      copyLarge(in, out, 0, fileSize);
    }
  }

}
