package net.vpc.toolbox.jmodproxy.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author taha.bensalah@gmail.com on 7/27/16.
 */
public class ReaderLogger extends Reader {
    private Reader reader;
    private Writer writer;

    public ReaderLogger(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int x=reader.read(cbuf,off,len);
        if(x>0){
            writer.write(cbuf, off,x);
        }
        return x;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
