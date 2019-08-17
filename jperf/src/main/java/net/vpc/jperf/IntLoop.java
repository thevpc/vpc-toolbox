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
public class IntLoop extends PerfProcess {

    public IntLoop() {
        super("int");
    }

    public PerfInfo[] run(PerfArgs args) {
        int max = args.getInt("maxInt", Integer.MAX_VALUE);
        long a = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
        }
        long b = System.currentTimeMillis();
        return new PerfInfo[]{new PerfInfo("IntLoop[" + max + "]", b - a, (b - a) + "ms")};
    }
}
