Java Fundamentals - I/O Homework
===========

Description
----------

Your task is to finish a Java program which packs and unpacks files without compression. Complete the implementation of `UncompressedDataPacker` that implements the following interface:

```
public interface Packer {
  void pack(Path inputDir, Path outputArchive) throws IOException;
  void unpack(Path inputArchive, Path outputDir) throws IOException;
}
```

The project has a set of unit tests. To get the maximum points all those tests must pass even with limited memory (run `./mvnw clean test` from command line). Existing tests cannot be altered.

Requirements
----------

1. Support both relative and absolute paths.
2. Support directories recursively.
3. Create missing parent directories automatically.
4. Only pack files (i.e. an input directory tree consisting of only empty folders is packed as the minimum archive consisting of just the Archive Type as there are no File Chunks).
5. Use `DataInputSteam` and `DataOutputStream`.
6. Support big files that don’t fit into memory at once.
7. Buffer data for better performance.
8. Close all resources properly.
9. Use the newer java.nio.file API.

File Format of the Archive
----------

* Whole Archive = Archive Type + File Chunk(1) + File Chunk(2) + … + File Chunk(n)
* Archive Type = 42 (1 fixed byte)
* File Chunk = File Path + File Length + File Contents
* File Path – bytes of a String of a relative path in the archive separated by / characters (use `readUTF()`/`writeUTF()` methods)
* File Length – 8 bytes showing how many bytes does the File Contents take (big endian long)
* File Contents – actual file in the archive

Various tips
-------------

If you want to build the distribution zip without fixing the tests, then you can do that by skipping tests in the build by adding -DskipTests to the command:
```shell
./mvnw clean deploy -DskipTests
```
However, note that to get the full score for this assignment, you still need to make the tests pass correctly.

Using Eclipse
-------------

If you are an Eclipse user then you can import the project to your workspace. *File* - *Import* - *Existing Maven Projects*.

To run the project in Eclipse you should ..... oh wait! You are already familiar with your IDE and I'm not supposed to explain this!

Submitting your assignment
--------------------------

For your convenience, we have set up the Maven project to ZIP up all files in your project folder so it is easy for you to attach it to an e-mail and send it our way. All you need to do is to execute the following command in your project folder:

```
./mvnw clean deploy
```

It will ask you for your full name, Student Book Number (also known as *matrikli number*), homework number and a comment (optional).

Example:

```bash
./mvnw clean deploy

#...skipping building, testing and packaging output from Maven...

[INFO] --- maven-antrun-plugin:1.8:run (package homework ZIP) @ homework4 ---
[INFO] Executing tasks

main:
Your full name (e.g. John Smith):
Jane Smith
Your Student Book Number (matrikli number, e.g. B12345):
B12345
Comment:
Java IO
      [zip] Building zip: /Users/jane/workspace/jf-hw-packer/target/jf-homework4-B12345.zip
   [delete] Deleting: /Users/jane/workspace/jf-hw-packer/homework.properties
[INFO] Executed tasks
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 20.041 s
[INFO] Finished at: 2017-02-03T11:35:11+02:00
[INFO] Final Memory: 21M/283M
[INFO] ------------------------------------------------------------------------
```

After Maven has finished, you can find the generated ZIP file in *target* folder with name such as 
*jf-homework4-B12345.zip* (it contains your Student Book Number/matrikli number and homework number).

The only thing left to do now is to send the ZIP file as an attachment to an e-mail with subject **"Homework 4 - *your Student Code/maktrikli number*"** to *jf@zeroturnaround.com*.
