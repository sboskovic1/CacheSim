public class Main {
    public static void main(String[] args) {
        // Start with generic hardware cache vs SAR cache
        Cache cache = new SARCache();

        Memory mem = new Memory();
        mem.loadSARMAtrixSpecialized();

        mem.printAccesses();

        Processor proc = new Processor(cache, mem);
        System.out.println("Starting simulation");
        proc.run();
        int result = proc.getCycles();

        System.out.println("CACHE: " + cache.name());
        System.out.println("PROCS: " + Hardware.NUM_PROCS);
        System.out.println(result + " cycles");            
    }
}