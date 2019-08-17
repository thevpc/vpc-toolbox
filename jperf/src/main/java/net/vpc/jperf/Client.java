/**
 * Simple Perf App
 * Taha Ben Salah
 *
 */
package net.vpc.jperf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 *
 * @author vpc
 */
public class Client extends PerfProcess {

    public Client() {
        super("client");
    }

    public PerfInfo[] run(PerfArgs args) {
        try {
            int port = args.getInt("port", 8000);
            String host = args.getString("host", "localhost");
            int buffer = args.getInt("buffer", 1024);
            int count = args.getInt("packets", 1000);
            Socket s = new Socket(InetAddress.getByName(host), port);

            long max;
            long a;
            long b;
            long x;
            byte[] bytes;

            //SEND
            PerfInfo clientUpTime;
            PerfInfo clientUpRate;
            max = count * buffer;
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            System.out.println("Sending : " + max + " bytes");
            out.writeLong(max);
            a = System.currentTimeMillis();
            bytes = new byte[buffer];
            for (int i = 0; i < count; i++) {
                out.write(bytes);
            }
            b = System.currentTimeMillis();
            System.out.println("Rate : " + (buffer * count / (b - a)) + " bytes/s");

            clientUpTime = new PerfInfo("ClientUpTime[" + max + "]", (b - a), (b - a) + "ms");
            clientUpRate = new PerfInfo("ClientUpRate[" + max + "]", (max / (b - a)), Utils.formatDataRate(max * 8 / (b - a)));


            //RECIEVE
            //InetAddress inetAddress = s.getInetAddress();
            DataInputStream in = new DataInputStream(s.getInputStream());

            PerfInfo clientDownTime;
            PerfInfo clientDownRate;

            max = in.readLong();
            System.out.println("Recieving : " + (max) + " bytes");
            a = System.currentTimeMillis();
            x = 0;
            int r = 0;
            bytes = new byte[buffer];
            while (x < max) {
                r = in.read(bytes);
                if (r >= 0) {
                    x += r;
                }
            }
            b = System.currentTimeMillis();
            clientDownTime = new PerfInfo("ClientDownTime[" + max + "]", (b - a), (b - a) + "ms");
            clientDownRate = new PerfInfo("ClientDownRate[" + max + "]", (max / (b - a)), Utils.formatDataRate(max * 8 / (b - a)));



            s.close();
            return new PerfInfo[]{clientUpTime, clientDownTime, clientUpRate, clientDownRate};
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new PerfInfo[0];
    }
}
