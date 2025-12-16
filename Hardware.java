public class Hardware {

    public static final int NUM_PROCS = 1;

    public static final int BLK_BITS = 5;
    public static final int BLK_SIZE = (int)Math.pow(2, BLK_BITS);

    public static final int CACHE_BITS = 2;
    public static final int CACHE_SIZE = (int)Math.pow(2, CACHE_BITS);

    public static final int MEM_CYCLES = 100;
    public static final int C2C_CYCLES = 10;

    public static final int INVALID = 0;
    public static final int MODIFIED = 1;
    public static final int SHARED = 2;

    public static final int MEM_SIZE = (int)Math.pow(2, 10); // 1 KB
    public static final int INT_SIZE = 4; // 4 Bytes

    public static final boolean VERBOSE = false;

    public static final int SAR_THRESHOLD = 16;

    // When using the generic cache for the network patern, decide whether to assign tasks by address or access time
    public static boolean NETWORK_SHUFFLE = false; 
    public static final int FREQUENT_BLOCKS = 4; // Number of high volume blocks in app cache
    public static final int FREQUENCY_WEIGHT = 10; // 'Weight' of high volume blocks in app cache, regular weight is 1
    public static final double FREQUENT_ACCESS_RATE = 0.6; // Percent chance of an app cache request appearing between flow cache requests
    
}