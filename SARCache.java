public class SARCache extends HardwareCache {

    int[][] bufs;
    int[] idx;
    boolean[] writing;

    public SARCache() {
        super();
        bufs = new int[Hardware.NUM_PROCS][Hardware.SAR_THRESHOLD];
        idx = new int[Hardware.SAR_THRESHOLD];
        writing = new boolean[Hardware.NUM_PROCS];
    }

    @Override
    public void write(int proc, int addr) {
        if (idx[proc] == Hardware.SAR_THRESHOLD) {
            writing[proc] = !writing[proc];
            idx[proc] = 0;
        }
        if (writing[proc] && super.ready(proc, this.cycle)) {
            super.write(proc, bufs[proc][idx[proc]]);
            idx[proc]++;
        } else if (!writing[proc] && idx[proc] != Hardware.SAR_THRESHOLD) {
            bufs[proc][idx[proc]] = addr;
            idx[proc]++;
        }
    }

    @Override
    public boolean ready(int proc, int cycle) {
        if (proc == 0) return super.ready(proc, this.cycle);
        if (this.cycle < Hardware.MEM_CYCLES) return false;
        if (writing[proc]) {
            write(proc, 0);
        }
        return writing[proc];
    }

    @Override
    public String name() {
        return "SAR Cache";
    }

}