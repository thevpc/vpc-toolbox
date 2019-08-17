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
public class PerfInfo {

    private String id;
    private long value;
    private String formattedValue;

    public PerfInfo(String id, long value, String formattedValue) {
        this.id = id;
        this.value = value;
        this.formattedValue = formattedValue;
    }

    public String getId() {
        return id;
    }

    public long getValue() {
        return value;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    @Override
    public String toString() {
        return "PerfInfo{" + "id=" + id + ", value=" + value + ", formattedValue=" + formattedValue + '}';
    }
}
