import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Memory {

    public static final int READ = 0;
    public static final int WRITE = 1;

    Queue<int[]>[] accesses;
    int terminated;

    public Memory() {
        accesses = new LinkedList[Hardware.NUM_PROCS];
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            accesses[i] = new LinkedList<>();
        }
        terminated = 0;
    }

    int[] getNext(int proc) {
        if (accesses[proc].isEmpty()) {
            terminated++;
            return null;
        }
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
        int split = Hardware.MEM_SIZE / 2 / (Hardware.NUM_PROCS - 1) - Hardware.MEM_SIZE / 2 % (Hardware.NUM_PROCS - 1) + Hardware.BLK_SIZE;
        System.out.println(split);
        for (int i = 0; i < Hardware.MEM_SIZE / 2; i += Hardware.INT_SIZE) {
            System.out.println(i);
            if (accesses[proc].size() == 2 * (Hardware.MEM_SIZE / Hardware.INT_SIZE / Hardware.NUM_PROCS)) proc++;
            accesses[0].add(new int[]{reads.get(idx), Memory.READ});
            accesses[(writes.get(idx) - Hardware.MEM_SIZE / 2) / split + 1].add(new int[]{writes.get(idx), Memory.WRITE});
            idx++;
        }
    }

    public void printAccesses() {
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            System.out.println("PROC " + i + ": ");
            for (int[] j : accesses[i]) {
                System.out.print(Arrays.toString(j) + " ");
            }
            System.out.println();
        }
    }
    
}