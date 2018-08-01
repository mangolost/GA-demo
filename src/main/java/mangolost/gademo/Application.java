package mangolost.gademo;

import mangolost.gademo.config.ProblemSetting;
import mangolost.gademo.ga.GeneticAlgorithm;
import mangolost.gademo.problem.Problem;
import mangolost.gademo.solution.Solution;

public class Application {

    /**
     *
     */
    private static void doSolve() {
        Problem problem = new Problem();
        problem.genProblem(ProblemSetting.SEEDNO, ProblemSetting.JOBNUM,
                ProblemSetting.STIMEMIN, ProblemSetting.STIMEMAX); //生成问题：种子1

        int calTimes = 10;
        for (int i = 1; i <= calTimes; i++) {
            Solution solution = new GeneticAlgorithm().solve(problem);
            System.out.println("第" + i + "次计算结果为: " + solution.makespan);
        }

        //。，这个纯原始、没有任何特殊优化的遗传算法求解有够烂的，解不稳定，10次差别好大，太依赖随机初始值，过早陷入局部最优
        // 。，后面再优化
    }

    public static void main(String[] args) {

        doSolve();

    }
}
