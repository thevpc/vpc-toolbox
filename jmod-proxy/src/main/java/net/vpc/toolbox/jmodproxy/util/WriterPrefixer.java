package net.vpc.toolbox.jmodproxy.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com on 7/27/16.
 */
public class WriterPrefixer extends Writer{
    Writer writer;
    String prefix;
    boolean wasNewLine=true;
    public WriterPrefixer(Writer writer,String prefix) {
        this.writer = writer;
        this.prefix = prefix;
    }


    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < len; i++) {
            if(wasNewLine){
                writer.write(prefix);
            }
            char c=cbuf[i+off];
            if(c=='\n'){
                if(sb.length()>0){
                    writer.write(sb.toString());
                }
                sb.delete(0,sb.length());
                writer.write("\n");
                wasNewLine=true;
            }else{
                wasNewLine=false;
                sb.append(c);
            }
        }
        if(sb.length()>0){
            writer.write(sb.toString());
            sb.delete(0,sb.length());
        }
   }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
