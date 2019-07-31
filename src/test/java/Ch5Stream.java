import org.junit.Test;

import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ch5Stream {

  /**
   * 5.6 数值流应用: 勾股数
   */
  @Test
  public void testPythagoreanTriples2() {
    Stream<double[]> pythagoreanTriples2 = IntStream.rangeClosed(1, 100)
        .boxed()
        .flatMap(a -> IntStream.rangeClosed(a, 100)
            .boxed()
            .map(b -> new double[] {a, b, Math.sqrt(a * a + b * b)}))
        .filter(t -> t[2] % 1 == 0);

    pythagoreanTriples2.forEach(
        doubles -> System.out.println(doubles[0] + ", " + doubles[1] + ", " + doubles[2]));
  }

  /**
   * 5.7 构建流
   */
  @Test
  public void testBuildStream() {

    // // 5.7.3 由文件构建流
    // long uniqueWords = 0;
    // try (
    //     Stream<String> lines = Files.lines(
    //         Paths.get("/Users/liukai/IdeaProjects/java8inaction/src/test/resources/data.txt"),
    //         Charset.defaultCharset())
    // ) {
    //
    //   lines.forEach(s -> System.out.println(s));
    //
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }

    // // 5.7.4 构建无限流
    // Stream.iterate(0, n -> n + 2)
    //     .limit(10)
    //     .forEach(integer -> System.out.println(integer));

    // // 斐波那契元组序列
    // Stream.iterate(new int[]{0,1}, n -> new int[]{n[1], n[0]+n[1]}).limit(20)
    //     .map(ints -> ints[0])
    //     .forEach(System.out::println);

    // // 生成流
    // Stream.generate(Math::random).limit(10).forEach(System.out::println);

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

}
