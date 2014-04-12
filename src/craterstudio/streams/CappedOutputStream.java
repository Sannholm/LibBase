/*
 * Created on 25 mei 2010
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.OutputStream;

import craterstudio.util.Bottleneck;
import craterstudio.util.ThroughputBottleneck;

public class CappedOutputStream extends OutputStream
{
    private final OutputStream out;
    private final Bottleneck bottleneck;
    
    public CappedOutputStream(int maxBytesPerSecond)
    {
        this(new NullOutputStream(), maxBytesPerSecond);
    }
    
    public CappedOutputStream(OutputStream out, int maxBytesPerSecond)
    {
        this(out, new ThroughputBottleneck(maxBytesPerSecond));
        if (maxBytesPerSecond <= 0)
            throw new IllegalStateException();
    }
    
    public CappedOutputStream(OutputStream out, Bottleneck bottleneck)
    {
        if (out == null)
            throw new NullPointerException();
        if (bottleneck == null)
            throw new NullPointerException();
        this.out = out;
        this.bottleneck = bottleneck;
    }
    
    @Override
    public void write(int b) throws IOException
    {
        bottleneck.feed(1);
        out.write(b);
    }
    
    @Override
    public void write(byte[] buf) throws IOException
    {
        this.write(buf, 0, buf.length);
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        while (len != 0)
        {
            int send = bottleneck.feed(len);
            out.write(buf, off, send);
            off += send;
            len -= send;
        }
    }
    
    @Override
    public void flush() throws IOException
    {
        out.flush();
    }
    
    @Override
    public void close() throws IOException
    {
        out.close();
    }
}