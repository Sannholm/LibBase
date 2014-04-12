/*
 * Created on 6-feb-2006
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;

public class NonBlockingInputStream extends InputStream
{
    private final InputStream in;
    
    public NonBlockingInputStream(InputStream in)
    {
        this.in = in;
    }
    
    @Override
    public int available() throws IOException
    {
        return in.available();
    }
    
    private final void check() throws IOException
    {
        if (available() == 0)
        {
            throw new IllegalStateException("not enough bytes available");
        }
    }
    
    @Override
    public int read() throws IOException
    {
        check();
        
        return in.read();
    }
    
    @Override
    public int read(byte[] buf) throws IOException
    {
        return this.read(buf, 0, buf.length);
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException
    {
        check();
        
        return in.read(buf, off, len);
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
        // this.check(); av = -1?
    }
}
