
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import net.vpc.jperf.Client;
import net.vpc.jperf.IntLoop;
import net.vpc.jperf.LongLoop;
import net.vpc.jperf.PerfArgs;
import net.vpc.jperf.PerfInfo;
import net.vpc.jperf.PerfProcess;
import net.vpc.jperf.Server;

/**
 * Simple Perf App
 * Taha Ben Salah
 *
 */
public class App {

    public static void main(String[] args) {
        new App().run(args);
    }
    PerfArgs perfArgs = new PerfArgs();
    HashSet<String> okperfs = new HashSet<String>(Arrays.asList("int", "long", "client", "server"));

    public void run(String[] args) {
        int i = 0;
        LinkedHashSet<String> perfs = new LinkedHashSet<String>();
        while (i < args.length) {
            String a = args[i];
            if (a.equals("-help")) {
                help();
                System.exit(0);
            }
            if (a.equals("-version")) {
                version();
                System.exit(0);
            }
            if (!perfArgs.parseOption(a)) {
                if (okperfs.contains(a)) {
                    perfs.add(a);
                } else {
                    System.err.println("Command not found " + a);
                    help();
                    System.exit(1);
                }
            }
            i++;
        }
        if (perfs.size() == 0) {
            System.out.println("Type Help syntax usage. Assuming defaut perf monitoring.");
            perfs.addAll(Arrays.asList("int", "long"));
        }
        
        for (String s : new String[]{
            "java.home   ",
            "java.version",
            "java.vendor ",
            "os.name     ",
            "os.arch     ",
            "os.version  "
        }) {
            System.out.println(s+" = "+System.getProperty(s.trim()));
        }
        
        for (String a : perfs) {
            if ("int".equals(a)) {
                print(new IntLoop());
            } else if ("long".equals(a)) {
                print(new LongLoop());
            } else if ("client".equals(a)) {
                print(new Client());
            } else if ("server".equals(a)) {
                print(new Server());
            }

        }
    }

    private void version() {
        System.out.println("Version 1.0. Taha Ben SALAH");
    }

    private void help() {
        System.out.println("int -maxInt=<INT_VALUE>");
        System.out.println("\tlaunch int");
        
        System.out.println("long -maxLong=<LONG_VALUE>");
        System.out.println("\tlaunch long");
        
        System.out.println("client -host=<HOST> -port=<PORT> -buffer=<BUFFER_SIZE> -packets=<COUNT>");
        System.out.println("\tlaunch client");
        
        System.out.println("server -port=<PORT> -buffer=<BUFFER_SIZE> -packets=<COUNT>");
        System.out.println("\tlaunch server upload test");
    }

    private void print(PerfProcess r) {
        System.out.println("Start " + r.getCommand());
        for (PerfInfo perfInfo : r.run(perfArgs)) {
            System.out.println(perfInfo);
        }
    }
}
