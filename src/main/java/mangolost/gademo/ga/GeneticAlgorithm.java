package mangolost.gademo.ga;

import mangolost.gademo.config.GASetting;
import mangolost.gademo.problem.Problem;
import mangolost.gademo.solution.Solution;
import mangolost.gademo.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GeneticAlgorithm {

    public Problem problem;

    public int currentGen = 0;

    public Group group = new Group();

    public List<Double> minMakespanList = new ArrayList<>();

    public List<Double> avgMakespanList = new ArrayList<>();

    public Chromosome bestFoundChrome = null;

    /**
     *
     * @param problem
     * @return
     */
    public Solution solve(Problem problem) {

        this.problem = problem;

        Solution solution = new Solution();

        group.initialGroup();

        for (int i = 1; i <= GASetting.GENERATION_MAXNUM; i++) {
            //轮盘赌 选择
            group.roulette();

            //交叉
            group.crossover();

            //变异
            group.mutation();

            //计算
            group.doStatics();

            currentGen = i;
        }

        solution.makespan = bestFoundChrome.makespan;
        solution.result = bestFoundChrome.sequence;

        return solution;
    }

    /**
     *
     */
    public class Group {

        public List<Chromosome> chromosomes = new ArrayList<>();

        public Chromosome bestChromosome;

        public double avg_makespan;
        public double max_makespan = 0;
        public double min_makespan = Double.MAX_VALUE;

        public void initialGroup() {
            int n = problem.getJobNum();

            //随机生成序列
            for (int i = 0; i < GASetting.GROUP_SIZE; i++) {
                Chromosome chromosome = new Chromosome();
                chromosome.sequence = RandomUtils.genRandomSequence(n);
                chromosome.makespan = chromosome.calMakespan();
                chromosomes.add(chromosome);
            }

            //计算统计信息
            doStatics();
        }

        void doStatics() {
            double sum_makespan = 0;
            for (Chromosome chromosome: chromosomes) {
                sum_makespan += chromosome.makespan;
                if (chromosome.makespan < min_makespan) {
                    min_makespan = chromosome.makespan;
                    bestChromosome = chromosome;
                }
                if (chromosome.makespan > max_makespan) {
                    max_makespan = chromosome.makespan;
                }
            }
            avg_makespan = sum_makespan / chromosomes.size();
            bestChromosome = bestChromosome.deepCopy();
            minMakespanList.add(bestChromosome.makespan);
            avgMakespanList.add(avg_makespan);
            if (bestFoundChrome == null) {
                bestFoundChrome = bestChromosome.deepCopy();
            } else if (bestChromosome.makespan < bestFoundChrome.makespan) {
                bestFoundChrome = bestChromosome.deepCopy();
            }
        }

        /**
         *
         */
        void roulette() {

            int size = chromosomes.size();
            //计算种群中每个个体的适应度
            //这里采取一个非常简单的方式(效果可能非常差)
            //fitness(i) = max_makespan / makespan(i)

            double[] fitnessArr = new double[size];
            for (int i = 0; i < size; i++) {
                fitnessArr[i] = max_makespan / chromosomes.get(i).makespan;
            }

            //不采用线性加速

            //根据适应度确定选择概率

            double sum_fitness = 0;
            for (double fitness: fitnessArr) {
                sum_fitness += fitness;
            }
            double[] p_select = new double[size];//各个染色体的选择概率
            double[] p_select_sum = new double[size];//各个染色体的累计选择概率
            for (int i = 0; i < size; i++) {
                p_select[i] = fitnessArr[i] / sum_fitness;
            }
            p_select_sum[0] = p_select[0];
            for (int i = 1; i < size; i++) {
                p_select_sum[i] = p_select_sum[i - 1] + p_select[i];
            }

            //----------------------------------------------------------------轮盘赌选择

            List<Chromosome> chromosomesNew = new ArrayList<>();
            //1. 当前最优解强制进入下一代种群
            chromosomesNew.add(bestChromosome.deepCopy());
            //2. 其他按照概率选择
            for (int i = 0; i < size; i++) {
                double x = RandomUtils.random.nextDouble();
                for (int j = 0; j < size; j++) {
                    if (x <= p_select_sum[j]) {
                        chromosomesNew.add(chromosomes.get(j));
                        break;
                    }
                }
            }

            chromosomes = chromosomesNew;

        }

        /**
         *
         */
        void crossover() {
            int size = chromosomes.size();
            int num = (int) Math.ceil(size * GASetting.CROSSOVER_PROB);
            List<Integer> aaa = RandomUtils.getRandomNum(num, 0, size);
            for (int i = 0; i < num / 2; i++) {
                crossover_two(chromosomes.get(aaa.get(i)), chromosomes.get(aaa.get(i)));
            }
        }

        /**
         *
         */
        public void mutation() {
            int size = chromosomes.size();
            int num = (int) Math.ceil(size * GASetting.MUTATION_PROB);
            List<Integer> aaa = RandomUtils.getRandomNum(num, 0, size);
            for (int i = 0; i < num; i++) {
                chromosomes.get(aaa.get(i)).mutation_one();
            }
        }

        /**
         * 采用双亲顺序交叉法，采取完全替代（子女完全取代父母）
         * @param a
         * @param b
         */
        void crossover_two(Chromosome a, Chromosome b) {
            int n = problem.getJobNum();
            int index1 = RandomUtils.random.nextInt(n - 2) + 1;
            int index2 = RandomUtils.random.nextInt(n - 2) + 1;
            double ran = RandomUtils.random.nextDouble();
            if (index1 == index2) {
                if (ran >= 0.5) {
                    index2 = index1 + 1;
                } else {
                    index1 = index2 - 1;
                }
            } else if (index1 > index2) {
                int temp = index1;
                index1 = index2;
                index2 = temp;
            }

            int[] part_a = new int[index2 - index1 + 1];
            int[] part_b = new int[index2 - index1 + 1];
            for (int i = 0, xxxtemp = index2 - index1; i <= xxxtemp; i++) {
                part_a[i] = a.sequence.get(i + index1);
                part_b[i] = b.sequence.get(i + index1);
            }

            int b_index = 0;
            for (int i = 0; i < n; i++) {
                if ((i < index1) || (i > index2)) {
                    for (int j = b_index; j < n; j++) {
                        int x = b.sequence.get(j);
                        if (if_exist(x, part_a)) {
                            continue;
                        }
                        a.sequence.remove(i);
                        a.sequence.add(i, x);
                        b_index = j + 1;
                        break;
                    }
                }
            }

            int a_index = 0;
            for (int i = 0; i < n; i++) {
                if ((i < index1) || (i > index2)) {
                    for (int j = a_index; j < n; j++) {
                        int x = a.sequence.get(j);
                        if (if_exist(x, part_b)) {
                            continue;
                        }
                        b.sequence.remove(i);
                        b.sequence.add(i, x);
                        a_index = j + 1;
                        break;
                    }
                }
            }

            a.calMakespan();
            b.calMakespan();

        }

        /**
         * //判断x是否是数组y中的元素；是，返回true；否，返回false
         * @param x
         * @param y
         * @return
         */
        public boolean if_exist(int x, int[] y) {
            boolean exist = false;
            for (int aY : y) {
                if (x == aY) {
                    exist = true;
                    break;
                }
            }
            return exist;
        }

    }

    /**
     *
     */
    public class Chromosome {

        public double makespan = 0;
        public List<Integer> sequence = new ArrayList<>();

        /**
         *
         * @return
         */
        public double calMakespan() {
            int jobNum = problem.getJobNum();
            int[][] sTimes = problem.getsTimes();
            double makespan = 0;
            for (int i = 0; i < jobNum - 1; i++) {
                makespan += sTimes[sequence.get(i)][sequence.get(i+1)];
            }
            return makespan;
        }

        /**
         *
         * @return
         */
        public Chromosome deepCopy() {
            Chromosome chromosome = new Chromosome();
            chromosome.makespan = this.makespan;
            chromosome.sequence.addAll(this.sequence);
            return chromosome;
        }

        /**
         * 采用异位交换
         */
        public void mutation_one() {
            int n = problem.getJobNum();
            int index1 = RandomUtils.random.nextInt(n - 1) + 1;
            int index2 = RandomUtils.random.nextInt(n - 1) + 1;

            if (index1 != index2) {
                int temp1 = sequence.get(index1);
                int temp2 = sequence.get(index2);
                sequence.remove(index1);
                sequence.add(index1, temp2);
                sequence.remove(index2);
                sequence.add(index2, temp1);
                calMakespan();
            }
        }
    }




}
