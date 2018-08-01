package mangolost.gademo.problem;

import java.util.Random;

/**
 *
 */
public class Problem {

    private int jobNum;

    private int[][] sTimes;

    public Problem() {

    }

    /**
     *
     * @param seedNo
     * @param jobNum
     * @param sMinTime
     * @param sMaxTime
     * @return
     */
    public void genProblem(long seedNo, int jobNum, int sMinTime, int sMaxTime) {
        int[][] sTimes = new int[jobNum][jobNum];
        Random random = new Random(seedNo);
        for (int i = 0; i < jobNum; i++) {
            for (int j = 0; j < jobNum; j++) {
                sTimes[i][j] = (int) Math.ceil(sMinTime + (sMaxTime - sMinTime) * random.nextDouble());
            }
        }

        for (int i = 0; i < jobNum; i++) {
            for (int j = 0; j < jobNum; j++) {
                System.out.print(sTimes[i][j] + " ");
            }
            System.out.println();
        }

        this.sTimes = sTimes;
        this.jobNum = jobNum;
    }

    public int getJobNum() {
        return jobNum;
    }

    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }

    public int[][] getsTimes() {
        return sTimes;
    }

    public void setsTimes(int[][] sTimes) {
        this.sTimes = sTimes;
    }
}
