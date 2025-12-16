import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class HybridCache extends HardwareCache {
    
    private LRUCache[] appCache;

    public HybridCache() {
        super();

        appCache = new LRUCache[Hardware.NUM_PROCS];
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            appCache[i] = new LRUCache();
        }
    }

    @Override
    public void read(int proc, int addr) {
        if (Hardware.VERBOSE && proc >= Hardware.NUM_PROCS / 2) System.out.println("Proc " + proc + " reads from " + addr + " at cycle " + cycle);
        if (proc < Hardware.NUM_PROCS / 2) {
            super.read(proc, addr);
            return;
        }
        else if (appCache[proc - Hardware.NUM_PROCS / 2].query(addr)) return;
        for (LRUCache c : appCache) {
            if (c != appCache[proc] && c.contains(addr)) {
                free[proc] = this.cycle + Hardware.C2C_CYCLES;
                return;
            }
        }
        free[proc] = this.cycle + Hardware.MEM_CYCLES;
    }

    private class LRUCache {

        private Queue<Integer> cache;
        private int size;

        public LRUCache() {
            cache = new LinkedList<>();
            size = Hardware.CACHE_SIZE;
        }

        public boolean query(int addr) {
            if (cache.remove(addr)) {
                cache.add(addr);
                return true;
            } else {
                if (cache.size() >= size) {
                    cache.poll();
                }
                cache.add(addr);
                return false;
            }
        }

        public boolean contains(int addr) {
            return cache.contains(addr);
        }

        public String name() {
            return "Hybrid Cache";
        }
    }
}