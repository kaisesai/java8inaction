package com.look.java8inaction;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 第六章: 用流收集数据
 */
public class Ch6UseStreamCollectData {

  public static void main(String[] args) {

    // 6.2 归约与汇总

    // 6.2.4 广义的归约汇总
    // 将多个 List 合并为一个 List
    // mergeMultipleListToOneList();

    // 6.3 分组
    // groupbyList();

    // 6.4 分区
    // 6.4.2 将数字按质数和非质数分区
    long start = System.currentTimeMillis();

    int candidateNum = 1_000_000_0;
    Map<Boolean, List<Integer>> candidateMap = partitionPrimes(candidateNum);
    // System.out.println(candidateMap);
    System.out.println("partitionPrimes 方法耗时: " + (System.currentTimeMillis() - start));

    Map<Boolean, List<Integer>> booleanListMap = partitionPrimesWithCustomCollector(candidateNum);
    // System.out.println(booleanListMap);
    System.out.println(
        "partitionPrimesWithCustomCollector 方法耗时: " + (System.currentTimeMillis() - start));

  }

  public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n)
        .boxed()
        .collect(Collectors.partitioningBy(Ch6UseStreamCollectData::isPrime));
  }

  public static boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate);
    return IntStream.range(2, candidateRoot).noneMatch(i -> candidate % i == 0);
  }

  private static void groupbyList() {
    Random random = new Random();
    Map<Integer, Optional<Integer>> collect = random.ints(100)
        .boxed()
        .collect(Collectors.groupingBy(integer -> integer % 10,
            Collectors.maxBy(Comparator.comparing(integer -> integer))));
    System.out.println(collect);
  }

  private static void mergeMultipleListToOneList() {
    List<String> strings1 = Arrays.asList(
        new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "A"});
    List<String> strings2 = Arrays.asList(
        new String[] {"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V"});
    List<List<String>> lists = Arrays.asList(strings1, strings2);

    List<String> collect = lists.stream()
        .collect(Collectors.reducing(new ArrayList<String>(), (strings, strings21) -> {
          strings.addAll(strings21);
          return strings;
        }));
    System.out.println(collect);

    Map<String, Map<String, List<String>>> collect1 = strings1.stream()
        .collect(Collectors.groupingBy(s -> s, Collectors.groupingBy(String::toUpperCase)));
    System.out.println(collect1);

  }

  /**
   * 自定义质数收集器
   *
   * @param n
   * @return
   */
  public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
    return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
  }

  /**
   * 是否为质数
   *
   * <p>
   * 优化版本
   * </p>
   *
   * @param primes
   * @param candidate
   * @return
   */
  public static boolean isPrime(List<Integer> primes, int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate);
    return takeWhile(primes, i -> i <= candidateRoot).stream().noneMatch(i -> candidate % i == 0);
  }

  public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
    int i = 0;
    for (A item : list) {
      // 检查项目中是否满足谓词
      if (!p.test(item)) {
        // 如果不满足,则返回该项目之前的前缀列表
        return list.subList(0, i);
      }
      i++;
    }
    // 列表中的项目都满足谓词,则返回该列表本身
    return list;
  }

  /**
   * 质数分区收集器
   */
  public static class PrimeNumbersCollector
      implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
      // 这是创建一个类后,添加代码块来进行初始化数据的写法
      return () -> new HashMap<Boolean, List<Integer>>() {{
        // 从一个有两个空 list 的 map 开始收集
        put(true, new ArrayList<Integer>());
        put(false, new ArrayList<Integer>());
      }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
      return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
        // 根据 isPrime 的结果,获取质数或非质数列表
        acc.get(isPrime(acc.get(true), candidate))
            // 将被测试的书添加相应的列表中
            .add(candidate);
      };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
      // 实际上这个收集器是不能并行使用的.因为算法本身是顺序的,这意味永远不会调用 combiner 方法.
      // 更好的做法是返回空或者抛出一个 Unsupported- OperationException异常
      return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
        map1.get(true).addAll(map2.get(true));
        map1.get(false).addAll(map2.get(false));
        return map1;
      };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
      return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
  }

  /**
   * 简单的收集器类
   *
   * @param <T>
   */
  public static class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    /**
     * 提供者
     * <p>
     * 1. 建立新的结果容器
     *
     * @return
     */
    @Override
    public Supplier<List<T>> supplier() {
      // 创建集合操作的起点
      return ArrayList::new;
    }

    /**
     * 累加器
     * 2. 将元素添加到结果容器汇总
     *
     * @return
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
      return List::add;
    }

    /**
     * 结合器
     * 4. 合并两个结果容器
     * <p>
     * 定义了对各个字部分进行并行处理时,各个字部分归约处理所得到的累加器要如何合并
     * </p>
     *
     * @return
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
      return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
      };
    }

    /**
     * 3. 对结果容器应用最终转换
     *
     * @return
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
      return Function.identity();
    }

    /**
     * 5. 定义了收集器的行为----关于流是否可以进行并行,以及可以使用哪些优化的提示
     * 这里我们设定为 IDENTITY_FINISH 和 CONCURRENT 标识
     *
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
      return Collections.unmodifiableSet(
          EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.CONCURRENT));
    }
  }

}
