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
public abstract class PerfProcess {
    private String command;

    public PerfProcess(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
    
    public abstract PerfInfo[] run(PerfArgs args);
}
