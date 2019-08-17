/**
 * Simple Perf App
 * Taha Ben Salah
 *
 */
package net.vpc.jperf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author vpc
 */
public class Server extends PerfProcess {

    public Server() {
        super("server");
    }

    public PerfInfo[] run(PerfArgs args) {
        try {
            int port = args.getInt("port", 8000);
            int buffer = args.getInt("buffer", 1024);
            int count = args.getInt("packets", 1000);
            ServerSocket ss = new ServerSocket(port);
            Socket s = ss.accept();
            //InetAddress inetAddress = s.getInetAddress();
            DataInputStream in = new DataInputStream(s.getInputStream());

            PerfInfo serverDownTime;
            PerfInfo serverDownRate;

            long max = in.readLong();
            System.out.println("Recieving : " + (max) + " bytes");
            long a = System.currentTimeMillis();
            long x = 0;
            int r = 0;
            byte[] bytes = new byte[buffer];
            while (x<max) {
                r = in.read(bytes);
                if (r >= 0) {
                    x += r;
                }
            }
            long b = System.currentTimeMillis();
            serverDownTime = new PerfInfo("ServerDownTime[" + max + "]", (b - a), (b - a) + "ms");
            serverDownRate = new PerfInfo("ServerDownRate[" + max + "]", (max / (b - a)), Utils.formatDataRate(max / (b - a)));

            //SEND
            PerfInfo serverUpTime;
            PerfInfo serverUpRate;
            max = count * buffer;
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            System.out.println("Sending : " + (buffer * count) + " bytes");
            out.writeLong(count * buffer);
            a = System.currentTimeMillis();
            bytes = new byte[buffer];
            for (int i = 0; i < count; i++) {
                out.write(bytes);
            }
            b = System.currentTimeMillis();
            System.out.println("Rate : " + (buffer * count / (b - a)) + " bytes/s");

            serverUpTime = new PerfInfo("ServerUpTime[" + max + "]", (b - a), (b - a) + "ms");
            serverUpRate = new PerfInfo("ServerUpRate[" + max + "]", (max / (b - a)), Utils.formatDataRate(max / (b - a)));


            s.close();
            ss.close();
            return new PerfInfo[]{serverUpTime,serverDownTime,serverUpRate,serverDownRate};
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new PerfInfo[0];
    }
}
