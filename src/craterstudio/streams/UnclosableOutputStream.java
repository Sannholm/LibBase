/*
 * Created on 2 jul 2009
 */

package craterstudio.streams;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

public class UnclosableOutputStream extends OutputStream
{
    private final OutputStream out;
    private boolean closed;
    
    public UnclosableOutputStream(OutputStream out)
    {
        this.out = out;
        closed = false;
    }
    
    @Override
    public void write(int b) throws IOException
    {
        if (closed)
            throw new EOFException();
        out.write(b);
    }
    
    @Override
    public void write(byte[] buf) throws IOException
    {
        if (closed)
            throw new EOFException();
        out.write(buf);
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        if (closed)
            throw new EOFException();
        out.write(buf, off, len);
    }
    
    @Override
    public void flush() throws IOException
    {
        if (closed)
            throw new EOFException();
        out.flush();
    }
    
    @Override
    public void close() throws IOException
    {
        if (closed)
            throw new EOFException();
        closed = true;
        out.flush();
        
        // keep open!
    }
}