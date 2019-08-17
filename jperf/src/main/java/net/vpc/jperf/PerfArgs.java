/**
 * Simple Perf App
 * Taha Ben Salah
 *
 */
package net.vpc.jperf;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public class PerfArgs {

    private Map<String, String> map = new HashMap<String, String>();

    public boolean parseOption(String a) {
        if (a.startsWith("-")) {
            a = a.substring(1);
            int i = a.indexOf("=");
            if (i >= 0) {
                map.put(a.substring(0, i), a.substring(i + 1));
            } else {
                map.put(a, "");
            }
            return true;
        }
        return false;
    }

    public int getInt(String name, int defaultValue) {
        String s = getString(name, String.valueOf(defaultValue));
        if (s.length() != 0) {
            return Integer.parseInt(s);
        }
        return defaultValue;
    }

    public long getLong(String name, long defaultValue) {
        String s = getString(name, String.valueOf(defaultValue));
        if (s.length() != 0) {
            return Long.parseLong(s);
        }
        return defaultValue;
    }

    public String getString(String name, String defaultValue) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        System.out.println("Missing argument. Assuming \"-" + name + "="+defaultValue+"\"");
        return defaultValue;
    }
}
