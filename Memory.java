import java.util.Queue;
import java.util.LinkedList;


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
    
}