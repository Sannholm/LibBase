/*
 * Created on 20 aug 2010
 */

package craterstudio.streams;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import craterstudio.io.Streams;

public abstract class SwapOutputStream extends AbstractOutputStream
{
    private long rem;
    private OutputStream swap;
    private final OutputStream target;
    
    public SwapOutputStream(long maxBytes, OutputStream after)
    {
        super(new ByteArrayOutputStream());
        
        rem = maxBytes;
        swap = null;
        target = after;
    }
    
    //
    
    protected abstract File provideSwapFile() throws IOException;
    
    private File swapFile;
    
    //
    
    private OutputStream provideSwapOutputStream() throws IOException
    {
        if (swapFile != null)
            throw new IllegalStateException();
        swapFile = provideSwapFile();
        return new BufferedOutputStream(new FileOutputStream(swapFile));
    }
    
    private void switchStreams() throws IOException
    {
        if (swap != null)
            throw new IllegalStateException();
        swap = provideSwapOutputStream();
        if (swap == null)
            throw new NullPointerException();
        ((ByteArrayOutputStream)super.backing).writeTo(swap);
    }
    
    //
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        if (swap == null && len > rem)
            switchStreams();
        
        if (swap == null)
            rem -= len;
        
        ((swap != null) ? swap : super.backing).write(buf, off, len);
    }
    
    @Override
    public void flush() throws IOException
    {
        ((swap != null) ? swap : super.backing).flush();
    }
    
    @Override
    public void close() throws IOException
    {
        ((swap != null) ? swap : super.backing).close();
        
        if (swap == null)
        {
            ((ByteArrayOutputStream)super.backing).writeTo(target);
            target.close();
        }
        else
        {
            Streams.pump(new FileInputStream(swapFile), target);
            
            swapFile.delete();
        }
    }
}
