package com.look.java8inaction;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 5.7 构建流
 */
public class Ch5UseStream {

  public static void main(String[] args) {

    // 5.7.3 由文件构建流
    buildStreamFromFile();

    // 5.7.4.1 构建无限流
    buildLimitlessStreamFromIterate();

    // 5.7.4.2 构建无限流
    buildLimitlessStreamFromGenerate();

  }

  private static void buildLimitlessStreamFromGenerate() {
    Stream.generate(Math::random).limit(10).forEach(System.out::println);
    // 5.7.4
    // 斐波那契元组序列
    Supplier<Long> fib = new Supplier<Long>() {
      private long previous = 0L;

      private long current = 1L;

      @Override
      public Long get() {
        long oldPrevious = this.current;
        long newValue = this.previous + this.current;
        this.previous = this.current;
        this.current = newValue;
        return oldPrevious;
      }
    };
    Stream.generate(fib).limit(100).forEach(System.out::println);
  }

  /**
   * 5.7.4 构建无限流
   */
  private static void buildLimitlessStreamFromIterate() {
    Stream.iterate(0, n -> n + 2).limit(10).forEach(integer -> System.out.println(integer));

    // 斐波那契元组序列
    Stream.iterate(new int[] {0, 1}, n -> new int[] {n[1], n[0] + n[1]})
        .limit(20)
        .map(ints -> ints[0])
        .forEach(System.out::println);

  }

  /**
   * 5.7.3 由文件构建流
   */
  private static void buildStreamFromFile() {
    long uniqueWords = 0;
    try (
        Stream<String> lines = Files.lines(
            Paths.get("/Users/liukai/IdeaProjects/java8inaction/src/test/resources/data.txt"),
            Charset.defaultCharset())
    ) {

      lines.forEach(s -> System.out.println(s));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
