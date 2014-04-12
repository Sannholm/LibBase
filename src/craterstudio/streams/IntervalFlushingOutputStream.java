/*
 * Created on 6-feb-2006
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.OutputStream;

public class IntervalFlushingOutputStream extends OutputStream
{
    private final OutputStream out;
    private long lastFlushAt;
    private final long interval;
    
    public IntervalFlushingOutputStream(OutputStream out, long interval)
    {
        this.out = out;
        this.interval = interval;
        lastFlushAt = System.currentTimeMillis();
    }
    
    private boolean needsFlush()
    {
        return System.currentTimeMillis() - lastFlushAt > interval;
    }
    
    @Override
    public void write(int b) throws IOException
    {
        out.write(b);
        if (needsFlush())
            out.flush();
    }
    
    @Override
    public void write(byte[] buf) throws IOException
    {
        out.write(buf);
        if (needsFlush())
            out.flush();
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        out.write(buf, off, len);
        if (needsFlush())
            out.flush();
    }
    
    @Override
    public void flush() throws IOException
    {
        out.flush();
        lastFlushAt = System.currentTimeMillis();
    }
    
    @Override
    public void close() throws IOException
    {
        out.close();
    }
}
