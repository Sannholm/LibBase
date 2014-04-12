/*
 * Created on 4 jun 2010
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;

public class HttpLengthInputStream extends InputStream
{
    private final InputStream in;
    
    public HttpLengthInputStream(InputStream in, long length)
    {
        if (length < 0L)
            throw new IllegalArgumentException("invalid length: " + length);
        this.in = in;
        remaining = length;
    }
    
    private final byte[] oneByte = new byte[1];
    private long remaining;
    private boolean reachedEnd = false;
    
    @Override
    public int read() throws IOException
    {
        int got = this.read(oneByte);
        return (got == -1) ? -1 : (oneByte[0] & 0xFF);
    }
    
    @Override
    public int read(byte[] buf) throws IOException
    {
        return this.read(buf, 0, buf.length);
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException
    {
        if (reachedEnd)
        {
            return -1;
        }
        
        if (remaining == 0)
        {
            return -1;
        }
        
        len = (int)Math.min(remaining, len);
        int got = in.read(buf, off, len);
        if (got == -1)
            return -1;
        
        remaining -= got;
        if (remaining < 0)
            throw new IllegalStateException();
        return got;
    }
    
    @Override
    public void close() throws IOException
    {
        reachedEnd = true;
        in.close();
    }
    
    //
    
    @Override
    public long skip(long n) throws IOException
    {
        for (long i = 0; i < n; i++)
            if (this.read() == -1)
                return i;
        return n;
    }
    
    @Override
    public int available() throws IOException
    {
        return (int)Math.min(Integer.MAX_VALUE, remaining);
    }
    
    @Override
    public synchronized void reset() throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized void mark(int readlimit)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean markSupported()
    {
        return false;
    }
}