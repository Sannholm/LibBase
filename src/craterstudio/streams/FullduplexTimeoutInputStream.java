/*
 * Created on 25 mei 2010
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

/**
 * Only timeouts if both input and output did timeout
 */

public class FullduplexTimeoutInputStream extends InputStream
{
    private final InputStream in;
    private final CountingOutputStream out;
    
    //public FullduplexTimeoutInputStream(Socket socket) throws IOException
    //{
    //this(socket.getInputStream(), socket.getOutputStream());
    //}
    
    public FullduplexTimeoutInputStream(InputStream in, OutputStream out)
    {
        this.in = in;
        
        this.out = new CountingOutputStream(out);
    }
    
    public OutputStream getOutputStream()
    {
        return out;
    }
    
    @Override
    public int read() throws IOException
    {
        do
        {
            long written = out.bytes();
            
            try
            {
                return in.read();
            }
            catch (SocketTimeoutException exc)
            {
                if (written == out.bytes())
                {
                    throw exc;
                }
            }
        } while (true);
    }
    
    @Override
    public int read(byte[] buf) throws IOException
    {
        return this.read(buf, 0, buf.length);
    }
    
    @Override
    public int read(byte[] buf, int off, int len) throws IOException
    {
        do
        {
            long written = out.bytes();
            
            try
            {
                return in.read(buf, off, len);
            }
            catch (SocketTimeoutException exc)
            {
                if (written == out.bytes())
                {
                    throw exc;
                }
            }
        } while (true);
    }
    
    @Override
    public long skip(long n) throws IOException
    {
        return in.skip(n);
    }
    
    @Override
    public int available() throws IOException
    {
        return in.available();
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
    }
}