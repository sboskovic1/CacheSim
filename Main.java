import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Start with generic hardware cache vs SAR cache

        Memory mem = new Memory();
        List<int[]> memPattern = mem.generateHybridMemoryPattern();


        for (int i = 0; i < 3; i++) {
            Cache cache = null;
            if (i == 0) {
                cache = new HardwareCache();
                mem.loadHybridMemoryShuffled(memPattern);
                mem.printAccesses();
            } else if (i == 1) {
                cache = new HardwareCache();
                mem.loadHybridMemory(memPattern);
                mem.printAccesses();
            } else {
                cache = new HybridCache();
                mem.loadHybridMemory(memPattern);
                mem.printAccesses();
            }
            Processor proc = new Processor(cache, mem);
            System.out.println("Starting simulation");
            System.out.println("CACHE: " + cache.name());
            System.out.println("PROCS: " + Hardware.NUM_PROCS);
            proc.run();
            int result = proc.getCycles();
            System.out.println("Result: " + result + " cycles");   
        }
         
    }
}