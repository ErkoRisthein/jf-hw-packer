package org.zeroturnaround.jf.packer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ee.ut.jf2016.packer.PackerTestUtil;

public class UnpackTest {

  @Rule
  public LoggingRule loggingRule = new LoggingRule();

  private final Packer packer = new LoggingPacker(new UncompressedDataPacker());

  private Path tempDir;

  private Path inputDir;

  private Path inputArchive;

  private Path outputDir;

  @Before
  public void init() throws IOException {
    tempDir = Files.createTempDirectory("jf-hw-packer");
    inputDir = Files.createDirectory(tempDir.resolve("input"));
    inputArchive = tempDir.resolve("archive");
    outputDir = tempDir.resolve("output");
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(tempDir.toFile());
  }

  // Failures

  @Test(expected = Exception.class)
  public void testNullArchive() throws IOException {
    packer.unpack(null, outputDir);
  }

  @Test(expected = Exception.class)
  public void testNullDir() throws IOException {
    packer.unpack(inputArchive, null);
  }

  @Test(expected = Exception.class)
  public void testMissingArchive() throws IOException {
    packer.unpack(tempDir.resolve("dir"), outputDir);
  }

  @Test(expected = Exception.class)
  public void testDirInsteadOfFile() throws IOException {
    packer.unpack(Files.createDirectory(inputArchive), outputDir);
  }

  @Test(expected = Exception.class)
  public void testFileInsteadOfDir() throws IOException {
    packer.unpack(inputArchive, Files.createFile(tempDir.resolve("foo")));
  }

  @Test(expected = Exception.class)
  public void testEmptyInput() throws IOException {
    packer.unpack(Files.createFile(inputArchive), outputDir);
  }

  @Test(expected = Exception.class)
  public void testInvalidArchive() throws IOException {
    packer.unpack(Files.write(inputArchive, new byte[] { 23 }), outputDir);
  }

  // Success

  @Test
  public void testEmptyArchive() throws IOException {
    unpackAndVerify();
  }

  @Test
  public void testSingleFile() throws IOException {
    Files.write(inputDir.resolve("foo"), "bar".getBytes(StandardCharsets.UTF_8));
    unpackAndVerify();
  }

  @Test
  public void testSingleFileInSubDir() throws IOException {
    Files.write(Files.createDirectory(inputDir.resolve("sub")).resolve("foo"), "123".getBytes(StandardCharsets.UTF_8));
    unpackAndVerify();
  }

  @Test
  public void testTwoSmallFiles() throws IOException {
    Files.write(inputDir.resolve("foo"), "bar".getBytes(StandardCharsets.UTF_8));
    Files.write(Files.createDirectory(inputDir.resolve("sub")).resolve("hello"), "abcdef".getBytes(StandardCharsets.UTF_8));
    unpackAndVerify();
  }

  @Test
  public void testTwoBigFilesWithSimpleSizes() throws IOException {
    RandomFile.create(inputDir.resolve("random1"), 4L * FileUtils.ONE_MB);
    RandomFile.create(inputDir.resolve("random2"), 4L * FileUtils.ONE_MB);
    unpackAndVerify();
  }

  @Test
  public void testTwoBigFilesWithCustomSizes() throws IOException {
    RandomFile.create(inputDir.resolve("random1"), 10L * FileUtils.ONE_MB + 53243);
    RandomFile.create(inputDir.resolve("random2"), 7L * FileUtils.ONE_MB + 3412);
    unpackAndVerify();
  }

  @Test
  public void testBigFileWithSimpleSize() throws IOException {
    RandomFile.create(inputDir.resolve("random"), 100L * FileUtils.ONE_MB);
    unpackAndVerify();
  }

  @Test
  public void testBiggerFileWithCustomSize() throws IOException {
    RandomFile.create(inputDir.resolve("random"), 10L * FileUtils.ONE_MB + 123);
    unpackAndVerify();
  }

  private void unpackAndVerify() throws IOException {
    PackerTestUtil.unpackAndVerify(inputDir, tempDir, packer::unpack);
  }

}
