import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Start with generic hardware cache vs SAR cache
        Cache[] caches = new Cache[1];
        caches[0] = new HardwareCache();
        // caches[1] = new SARCache();
        int[] results = new int[caches.length];
        for (int i = 0; i < caches.length; i++) {
            Memory mem = new Memory();
            mem.loadSharedArrayReadWrite();
            Processor proc = new Processor(caches[i], mem);
            proc.run();
            results[i] = proc.getCycles();
        }

        System.out.println("PROCS: " + Hardware.NUM_PROCS);
        System.out.println(Arrays.toString(results));            
    }
}