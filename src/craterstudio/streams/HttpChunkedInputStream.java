/*
 * Created on 4 jun 2010
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.InputStream;

import craterstudio.io.Streams;

public class HttpChunkedInputStream extends InputStream
{
    private static final int INACTIVE = -1;
    
    private final InputStream in;
    
    public HttpChunkedInputStream(InputStream in)
    {
        this.in = in;
        oneByte = new byte[1];
        chunkSize = -1;
        remaining = 0;
    }
    
    private final byte[] oneByte;
    private int chunkSize;
    private int remaining;
    
    private int readNextChunk()
    {
        String line = Streams.binaryReadLineAsString(in);
        int chunkSize = Integer.parseInt(line.trim(), 16);
        if (chunkSize < 0)
            throw new IllegalStateException("chunked transfer encoding corrupted");
        return chunkSize;
    }
    
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
        if (chunkSize == 0)
        {
            return -1;
        }
        
        if (chunkSize == INACTIVE || remaining == 0)
        {
            chunkSize = readNextChunk();
            if (chunkSize == 0)
                return -1;
            remaining = chunkSize;
        }
        
        len = Math.min(remaining, len);
        if (len == 0)
            throw new IllegalStateException("chunked transfer encoding corrupted");
        int got = in.read(buf, off, len);
        if (got == 0)
            throw new IllegalStateException("chunked transfer encoding corrupted");
        if (got == -1)
            return -1;
        
        remaining -= got;
        if (remaining < 0)
            throw new IllegalStateException();
        
        if (remaining == 0 && !Streams.binaryReadLineAsString(in).equals(""))
            throw new IllegalStateException("chunked transfer encoding corrupted");
        
        return got;
    }
    
    @Override
    public void close() throws IOException
    {
        try
        {
            verifyEndState();
        }
        // catch (IllegalStateException exc)
        // {
        // System.err.println(this.getClass().getSimpleName() + ".verifyEndState() failed: " + exc.getMessage());
        // }
        finally
        {
            in.close();
        }
    }
    
    //
    
    public void verifyEndState()
    {
        if (chunkSize == INACTIVE)
        {
            return;
        }
        
        if (chunkSize != 0)
        {
            if (remaining != 0)
                throw new IllegalStateException("chunk active:" + remaining + " bytes remaining");
            
            chunkSize = readNextChunk();
            if (chunkSize != 0)
                throw new IllegalStateException("next chunk pending: " + chunkSize + " bytes");
        }
        
        if (!Streams.binaryReadLineAsString(in).isEmpty())
            throw new IllegalStateException("chunked transfer encoding corrupted");
        chunkSize = INACTIVE;
    }
    
    //
    
    @Override
    public long skip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException();
        if (n == 0)
            return 0;
        
        byte[] buf = new byte[1024];
        
        long skipped = 0;
        while (n > 0)
        {
            int got = this.read(buf);
            if (got == -1)
                return skipped;
            skipped += got;
        }
        return skipped;
    }
    
    @Override
    public int available() throws IOException
    {
        return remaining;
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