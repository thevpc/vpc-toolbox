package net.vpc.toolbox.jmodproxy.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com on 7/27/16.
 */
public class MultipleWriter extends Writer{
    List<Writer> writers=new ArrayList<>();

    public MultipleWriter add(Writer writer){
        writers.add(writer);
        return this;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (Writer writer : writers) {
            writer.write(cbuf,off,len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (Writer writer : writers) {
            writer.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (Writer writer : writers) {
            writer.close();
        }
    }
}
