Java Fundamentals - I/O Homework
===========

Description
----------

Your task is to write a Java program which packs and unpacks files without compression. Implement the following interface

```
public interface Packer {
  void pack(Path inputDir, Path outputArchive) throws IOException;
  void unpack(Path inputArchive, Path outputDir) throws IOException;
}
```

The project has a set of unit tests. To get the maximum points all those tests must pass even with limited memory (run `mvn clean test` from command line). Existing tests cannot be altered.

Requirements
----------

1. Support both relative and absolute paths.
2. Support directories recursively.
3. Create missing parent directories automatically.
4. Only pack files (no support for empty directories).
5. Use `DataInputSteam` and `DataOutputStream`.
6. Support big files that don’t fit into memory at once.
7. Buffer data for better performance.
8. Close all resources properly.

File Format of the Archive
----------

* Whole Archive = Archive Type + File Chunk(1) + File Chunk(2) + … + File Chunk(n)
* Archive Type = 42 (1 fixed byte)
* File Chunk = File Path + File Length + File Contents
* File Path – bytes of a String of a relative path in the archive separated by / characters (use `readUTF()`/`writeUTF()` methods)
* File Length – 8 bytes showing how many bytes does the File Contents take (big endian long)
* File Contents – actual file in the archive
