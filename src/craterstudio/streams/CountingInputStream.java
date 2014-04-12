/*
 * Created on 6-feb-2006
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream
{
    private final InputStream in;
    private long bytes;
    
    public CountingInputStream(InputStream in)
    {
        this.in = in;
        bytes = 0;
    }
    
    public long bytes()
    {
        return bytes;
    }
    
    @Override
    public int read() throws IOException
    {
        int b = in.read();
        if (b == -1)
            return -1;
        bytes += 1;
        return b;
    }
    
    @Override
    public int read(byte[] buf) throws IOException
    {
        return this.read(buf, 0, buf.length);
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException
    {
        int b = in.read(buf, off, len);
        if (b == -1)
            return -1;
        bytes += b;
        return b;
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
    }
}
