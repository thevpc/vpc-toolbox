/**
 * Simple Perf App
 * Taha Ben Salah
 *
 */
package net.vpc.jperf;

/**
 *
 * @author vpc
 */
public class LongLoop extends PerfProcess {

    public LongLoop() {
        super("long");
    }

    public PerfInfo[] run(PerfArgs args) {
        long max = args.getLong("maxLong", Integer.MAX_VALUE);
        long a = System.currentTimeMillis();
        for (long i = 0; i < max; i++) {
        }
        long b = System.currentTimeMillis();
        return new PerfInfo[]{new PerfInfo("LongLoop[" + max + "]", b - a, (b - a) + "ms")};
    }
}
