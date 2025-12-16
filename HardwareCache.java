// Generic abstracted MSI style hardware cache
public class HardwareCache extends Cache {

    @Override
    public void read(int proc, int addr) {
        if (Hardware.VERBOSE) System.out.println("Proc " + proc + " reads from " + addr + " at cycle " + cycle);
        int idx = (addr / Hardware.BLK_SIZE) % Hardware.CACHE_SIZE;
        int tag = addr / (Hardware.BLK_SIZE * Hardware.CACHE_SIZE);
        if (writers.getOrDefault(idx + tag * Hardware.CACHE_SIZE, -1) >= cycle) { // Block is in use, stall
            this.instruction[proc] = new int[]{addr, Memory.READ};
            this.stalled[proc] = true;
            return;
        }
        
        int cyclesToAdd = 0;
        if ((cache[proc][idx][1] == Hardware.MODIFIED || cache[proc][idx][1] == Hardware.SHARED) && cache[proc][idx][0] == tag) {
            // Cache hit
            return;
        } else if (cache[proc][idx][1] == Hardware.MODIFIED) { // Writeback dirty block
            cyclesToAdd += Hardware.MEM_CYCLES;
            writers.put(cache[proc][idx][0], cycle + cyclesToAdd);
        }
        cache[proc][idx][1] = Hardware.SHARED;
        cache[proc][idx][0] = tag;
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            if (i == proc) continue;
            if (cache[i][idx][1] == Hardware.MODIFIED && cache[i][idx][0] == tag) {
                cyclesToAdd += Hardware.MEM_CYCLES + Hardware.C2C_CYCLES; // Requires a writeback, will be done by this processor for simplicity
                readers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
                free[proc] = cycle + cyclesToAdd;
                cache[i][idx][1] = Hardware.SHARED;
                return;
            } else if (cache[i][idx][1] == Hardware.SHARED && cache[i][idx][0] == tag) {
                cyclesToAdd += Hardware.C2C_CYCLES;
                readers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
                free[proc] = cycle + cyclesToAdd;
                cache[i][idx][1] = Hardware.SHARED;
                return;
            }
        }
        // Cache miss and no transfer
        cyclesToAdd += Hardware.MEM_CYCLES;
        readers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
        free[proc] = cycle + cyclesToAdd;
        return;
        
    }

    @Override
    public void write(int proc, int addr) {
        if (Hardware.VERBOSE) System.out.println("Proc " + proc + " writes to " + addr + " at cycle " + cycle);
        int idx = (addr / Hardware.BLK_SIZE) % Hardware.CACHE_SIZE;
        int tag = addr / (Hardware.BLK_SIZE * Hardware.CACHE_SIZE);
        if (writers.getOrDefault(idx + tag * Hardware.CACHE_SIZE, -1) >= cycle || readers.getOrDefault(idx + tag * Hardware.CACHE_SIZE, -1) >= cycle) { // Block is in use, stall
            this.instruction[proc] = new int[]{addr, Memory.WRITE};
            this.stalled[proc] = true;
            return;
        }

        int cyclesToAdd = 0;
        if ((cache[proc][idx][1] == Hardware.MODIFIED || cache[proc][idx][1] == Hardware.SHARED) && cache[proc][idx][0] == tag) {
            if (cache[proc][idx][1] == Hardware.SHARED) { // Invalidate
                for (int i = 0; i < Hardware.NUM_PROCS; i++) {
                    if (i != proc) cache[proc][idx][1] = Hardware.INVALID;
                }
                cache[proc][idx][1] = Hardware.MODIFIED;
            }
            // Cache hit
            return;
        } else if (cache[proc][idx][1] == Hardware.MODIFIED) { // Writeback dirty block
            cyclesToAdd += Hardware.MEM_CYCLES;
            writers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
        }
        cache[proc][idx][1] = Hardware.MODIFIED;
        cache[proc][idx][0] = tag;
        for (int i = 0; i < Hardware.NUM_PROCS; i++) {
            if (i == proc) continue;
            if (cache[i][idx][1] == Hardware.MODIFIED && cache[i][idx][0] == tag) {
                cyclesToAdd += Hardware.MEM_CYCLES + Hardware.C2C_CYCLES; // Requires a writeback, will be done by this processor for simplicity
                writers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
                free[proc] = cycle + cyclesToAdd;
                cache[i][idx][1] = Hardware.INVALID;
                return;
            } else if (cache[i][idx][1] == Hardware.SHARED && cache[i][idx][0] == tag) {
                cyclesToAdd += Hardware.C2C_CYCLES;
                writers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
                free[proc] = cycle + cyclesToAdd;
                cache[i][idx][1] = Hardware.INVALID;
                return;
            }
        }
        // Cache miss and no transfer
        cyclesToAdd += Hardware.MEM_CYCLES;
        writers.put(idx + tag * Hardware.CACHE_SIZE, cycle + cyclesToAdd);
        free[proc] = cycle + cyclesToAdd;
        return;

    }

    @Override
    public String name() {
        return "Hardware Cache";
    }

}