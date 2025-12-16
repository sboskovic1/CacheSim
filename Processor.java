public class Processor {

    private Cache cache;
    private Memory memory;
    int cycles;

    public Processor(Cache c, Memory m) {
        this.cache = c;
        this.memory = m;
        this.cycles = 0;
    }

    void run() {
        while (!memory.done()) {
            if (Hardware.VERBOSE) System.out.println("Starting cycle " + this.cycles);
            for (int i = 0; i < Hardware.NUM_PROCS; i++) {
                if (cache.ready(i, this.cycles)) {
                    int[] instruction = memory.getNext(i);
                    if (instruction != null) {
                        if (instruction[1] == Memory.READ) {
                            cache.read(i, instruction[0]);
                        } else {
                            cache.write(i, instruction[0]);
                        }
                    }
                }
            }
            this.cycles++;
        }
        System.out.println("All instructions served...");
        while (!cache.done(this.cycles)) {
            this.cycles++;
        }
    }

    int getCycles() {
        return this.cycles;
    }

    
}