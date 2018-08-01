package mangolost.gademo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    public static Random random = new Random();

    /**
     *
     * @param n
     * @return
     */
    public static List<Integer> genRandomSequence(int n) {
        return getRandomNum(n, 0, n);
    }

    /**
     * 在区间[a,b)之间随机选择num个互不相同的随机整数, num <= b - a
     * @param num
     * @param a
     * @param b
     * @return
     */
    public static List<Integer> getRandomNum(int num, int a, int b) {
        List<Integer> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (int i = a; i < b; i++) {
            list.add(i);
        }
        for (int j = 0; j < num; j++) {
            int k = random.nextInt(list.size());
            result.add(list.get(k));
            list.remove(k);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> list = genRandomSequence(10);
        System.out.println(Arrays.toString(list.toArray()));
    }

}
