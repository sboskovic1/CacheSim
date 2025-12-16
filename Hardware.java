public class Hardware {

    public static final int NUM_PROCS = 4;

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

    public static final int SAR_THRESHOLD = 32;

    
}