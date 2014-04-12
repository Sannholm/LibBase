/*
 * Created on 2 jul 2009
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;

public class UnclosableInputStream extends InputStream
{
    private final InputStream in;
    
    public UnclosableInputStream(InputStream in)
    {
        this.in = in;
    }
    
    @Override
    public int read() throws IOException
    {
        return in.read();
    }
    
    @Override
    public int read(byte[] b) throws IOException
    {
        return in.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return in.read(b, off, len);
    }
    
    @Override
    public int available() throws IOException
    {
        return in.available();
    }
    
    @Override
    public long skip(long n) throws IOException
    {
        return in.skip(n);
    }
    
    @Override
    public void close() throws IOException
    {
        // noooo!
    }
    
    //
    
    @Override
    public boolean markSupported()
    {
        return in.markSupported();
    }
    
    @Override
    public synchronized void reset() throws IOException
    {
        in.reset();
    }
    
    @Override
    public synchronized void mark(int readlimit)
    {
        in.mark(readlimit);
    }
}