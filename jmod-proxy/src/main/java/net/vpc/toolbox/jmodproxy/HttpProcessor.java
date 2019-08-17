package net.vpc.toolbox.jmodproxy;

import java.io.*;

/**
 * @author taha.bensalah@gmail.com on 7/27/16.
 */
public interface HttpProcessor {
    public Writer getOut();
    public BufferedReader getIn();
}
