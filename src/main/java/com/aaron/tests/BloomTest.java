package com.aaron.tests;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * @author liurong
 * @version 1.0
 * @date 2022/8/10 14:59
 */
public class BloomTest {

    public static final int total = 100 * 10000;
    private static BloomFilter<Integer> bf = BloomFilter.create(Funnels.integerFunnel(), total);
    private static BloomFilter<Integer> bf2 = BloomFilter.create(Funnels.integerFunnel(), total, 0.0003);
    /**
     * 用于检索一个元素是否在一个集合中
     * 它的优点是空间效率和查询时间都远远超过一般的算法，缺点是有一定的误识别率（误判）和删除困难。
     * bloom filter之所以能做到在时间和空间上的效率比较高，是因为牺牲了判断的准确率、删除的便利性
     * Bloom Filter跟单哈希函数Bit-Map不同之处在于：
     *      Bloom Filter使用了k个哈希函数，每个字符串跟k个bit对应。从而降低了冲突的概率。
     *
     * 布隆过滤器能够用于解决缓存击穿问题，如果一直拿误判的进行攻击不也缓存击穿了么
     *
     * 布隆过滤器的原理是，当一个元素被加入集合时，通过K个散列函数将这个元素映射成一个位数组中的K个点，把它们置为1。检索时，我们只要看看这些点是不是都是1就（大约）知道集合中有没有它了：如果这些点有任何一个0，则被检元素一定不在；如果都是1，则被检元素很可能在。这就是布隆过滤器的基本思想。
     */


    public static void main(String[] args) {
        test(bf);
        test(bf2);
    }

    public static void test(BloomFilter<Integer> bf) {
        for (int i = 0; i < total; i++) {
            bf.put(i);
        }
        for (int i = 0; i < total; i++) {
            if (!bf.mightContain(i)) {
                System.out.println("有坏人逃脱了~");
            }
        }
        int count = 0;
        for (int i = total; i < total + 10000; i++) {
            if (bf.mightContain(i)) {
                count++;
//                System.out.println(i);
            }
        }
        System.out.println("误伤的数量：" + count);
//        System.out.println(bf.mightContain(1009454));
//        System.out.println(bf.mightContain(1009488));
    }

}

