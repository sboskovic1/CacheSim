import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Memory {

    public static final int READ = 0;
    public static final int WRITE = 1;

    public Queue<int[]>[] accesses;
    public int terminated;
    public boolean[] running;

    public Memory() {
        accesses = new LinkedList[Hardware.NUM_PROCS];
        running = new boolean[Hardware.NUM_PROCS];
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            accesses[i] = new LinkedList<>();
            running[i] = true;
        }
        terminated = 0;
    }

    void reset() {
        running = new boolean[Hardware.NUM_PROCS];
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            running[i] = true;
        }
        terminated = 0;
    }

    int[] getNext(int proc) {
        if (accesses[proc].isEmpty() && running[proc] == true) {
            System.out.println("Process " + proc + " terminated");
            terminated++;
            running[proc] = false;
            return null;
        }
        if (running[proc] == false) return null;
        return accesses[proc].poll();
    } 

    boolean done() {
        return terminated == Hardware.NUM_PROCS;
    }

    void loadSharedArrayReadOnly() {
        int proc = 0;
        for (int i = 0; i < Hardware.MEM_SIZE; i += Hardware.INT_SIZE) {
            if (accesses[proc].size() == Hardware.MEM_SIZE / Hardware.INT_SIZE / Hardware.NUM_PROCS) proc++;
            accesses[proc].add(new int[]{i, READ});
        }
    }

    void loadSharedArrayReadWrite() {
        int proc = 0;
        for (int i = 0; i < Hardware.MEM_SIZE; i += Hardware.INT_SIZE) {
            if (accesses[proc].size() == 2 * (Hardware.MEM_SIZE / Hardware.INT_SIZE / Hardware.NUM_PROCS)) proc++;
            accesses[proc].add(new int[]{i, Memory.READ});
            accesses[proc].add(new int[]{i, Memory.WRITE});
        }
    }

    void loadSARMatrixGeneric() {
        List<Integer> reads = new ArrayList<>();
        List<Integer> writes = new ArrayList<>();
        for (int i = 0; i < Hardware.MEM_SIZE / 2; i += Hardware.INT_SIZE) {
           reads.add(i);
           writes.add(Hardware.MEM_SIZE / 2 + i);
        }
        Collections.shuffle(reads);
        int proc = 0;
        int idx = 0;
        for (int i = 0; i < Hardware.MEM_SIZE / 2; i += Hardware.INT_SIZE) {
            if (accesses[proc].size() == 2 * (Hardware.MEM_SIZE / Hardware.INT_SIZE / Hardware.NUM_PROCS / 2)) proc++;
            accesses[proc].add(new int[]{reads.get(idx), Memory.READ});
            accesses[proc].add(new int[]{writes.get(idx), Memory.WRITE});
            idx++;
        }

    }

    void loadSARMAtrixSpecialized() {
        List<Integer> reads = new ArrayList<>();
        List<Integer> writes = new ArrayList<>();
        for (int i = 0; i < Hardware.MEM_SIZE / 2; i += Hardware.INT_SIZE) {
           reads.add(i);
           writes.add(Hardware.MEM_SIZE / 2 + i);
        }
        Collections.shuffle(writes);
        int proc = 0;
        int idx = 0;
        int split = Hardware.NUM_PROCS == 1 ? 1 : Hardware.MEM_SIZE / 2 / (Hardware.NUM_PROCS - 1) - Hardware.MEM_SIZE / 2 % (Hardware.NUM_PROCS - 1) + Hardware.INT_SIZE;
        for (int i = 0; i < Hardware.MEM_SIZE / 2; i += Hardware.INT_SIZE) {
            if (accesses[proc].size() == 2 * (Hardware.MEM_SIZE / Hardware.INT_SIZE / Hardware.NUM_PROCS)) proc++;
            accesses[0].add(new int[]{reads.get(idx), Memory.READ});
            if (Hardware.NUM_PROCS != 1) accesses[(writes.get(idx) - Hardware.MEM_SIZE / 2) / split + 1].add(new int[]{writes.get(idx), Memory.WRITE});
            idx++;
        }
    }

    public List<int[]> generateHybridMemoryPattern() {
        int[][] appBlocks = new int[Hardware.MEM_SIZE / 2 / Hardware.BLK_SIZE][2];
        for (int i = 0; i < Hardware.FREQUENT_BLOCKS; i++) {
            int idx = (int)(Math.random() * appBlocks.length);
            appBlocks[idx][1] += Hardware.FREQUENCY_WEIGHT;
        }
        List<Integer> flowBlocks = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        for (int i = 0; i < appBlocks.length; i++) {
            int addr = Hardware.MEM_SIZE / 2 + i * Hardware.BLK_SIZE;
            flowBlocks.add(i * Hardware.BLK_SIZE);
            weights.add(addr);
            for (int j = 0; j < appBlocks[i][1]; j++) {
                weights.add(addr);
            }
        }
        List<int[]> memAccesses = new ArrayList<>();
        Collections.shuffle(flowBlocks);
        for (Integer addr : flowBlocks) {
            for (int i = 0; i < Hardware.BLK_SIZE; i += Hardware.INT_SIZE) {
                memAccesses.add(new int[]{addr + i, Memory.READ});
                while (Math.random() < Hardware.FREQUENT_ACCESS_RATE) {
                    memAccesses.add(new int[]{weights.get((int)(Math.random() * weights.size())), Memory.READ});
                }
                memAccesses.add(new int[]{addr + i, Memory.WRITE});
                while (Math.random() < Hardware.FREQUENT_ACCESS_RATE) {
                    memAccesses.add(new int[]{weights.get((int)(Math.random() * weights.size())), Memory.READ});
                }
            }
        }
        return memAccesses;
    }

    public void loadHybridMemory(List<int[]> memoryPattern) {
        int appIdx = 0;
        for (int i = 0; i < memoryPattern.size(); i++) {
            if (memoryPattern.get(i)[0] < Hardware.MEM_SIZE / 2) accesses[memoryPattern.get(i)[0] / (Hardware.MEM_SIZE / Hardware.NUM_PROCS)].add(memoryPattern.get(i));
            else {
                accesses[Hardware.NUM_PROCS / 2 + (appIdx % Hardware.NUM_PROCS / 2)].add(memoryPattern.get(i));
                appIdx++;
            }
        }
    }

    public void loadHybridMemoryShuffled(List<int[]> memoryPattern) {
        for (int i = 0; i < memoryPattern.size(); i++) {
            accesses[i % Hardware.NUM_PROCS].add(memoryPattern.get(i));
        }
    }

    public void printAccesses() {
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            System.out.println("PROC " + i + ": " + accesses[i].size());
            for (int[] j : accesses[i]) {
                System.out.print(Arrays.toString(j) + " ");
            }
            System.out.println();
        }
    }
    
}