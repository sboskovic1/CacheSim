import java.util.HashMap;
import java.util.Map;

public abstract class Cache {

    protected int[][][] cache; // Local block state
    public int[] free; // Cache busy
    protected boolean[] stalled; // Cache waiting for another processor to finish
    protected int[][] instruction; // Instruction in buffer when a cache is stalled

    protected Map<Integer, Integer> readers;
    protected Map<Integer, Integer> writers;
    protected int cycle;
    
    public Cache() {
        cache = new int[Hardware.NUM_PROCS][Hardware.CACHE_SIZE][2];
        free = new int[Hardware.NUM_PROCS];
        stalled = new boolean[Hardware.NUM_PROCS];
        instruction = new int[Hardware.NUM_PROCS][2];
        readers = new HashMap<>();
        writers = new HashMap<>();
    }

    public void read(int proc, int addr) {
        System.out.println("Incorrectly called generic cache read");
    }

    public void write(int proc, int addr) {
        System.out.println("Incorrectly called generic cache write");
    }

    public boolean ready(int proc, int cycle) {
        this.cycle = cycle;
        if (stalled[proc]) {
            int[] instr = instruction[proc];
            int tag = instr[0] / (Hardware.CACHE_SIZE * Hardware.BLK_SIZE);
            if (instr[1] == Memory.READ) {
                if (writers.getOrDefault(tag, -1) < cycle) {
                    stalled[proc] = false;
                    read(proc, instr[0]);
                }
            } else {
                if (writers.getOrDefault(tag, -1) < cycle && readers.getOrDefault(tag, -1) < cycle) {
                    stalled[proc] = false;
                    write(proc, instr[0]);
                }
            }
            return false;
        }
        return free[proc] < cycle;
    }

    public boolean done(int cycles) {
        boolean terminated = true;
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            terminated &= ready(i, cycles);
        }
        return terminated;
    }

    public String name() {
        return "error";
    }
    

}