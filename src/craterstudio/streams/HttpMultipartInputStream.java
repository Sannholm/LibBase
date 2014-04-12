/*
 * Created on 12 nov 2010
 */

package craterstudio.streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import craterstudio.io.Streams;
import craterstudio.text.Text;

public class HttpMultipartInputStream extends InputStream
{
    private final InputStream in;
    private final byte[] lastBoundary;
    private long receivedBytes;
    
    public HttpMultipartInputStream(InputStream in, String boundary)
    {
        this.in = in;
        lastBoundary = Text.ascii("--" + Text.remove(boundary, '"') + "--\r\n");
    }
    
    private final byte[] one = new byte[1];
    
    @Override
    public int read() throws IOException
    {
        int got = this.read(one);
        if (got == -1)
            return -1;
        return one[0] & 0xFF;
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
            if (currentStuff == null)
                if ((currentStuff = readNextBinaryLine()) == null)
                    return -1;
            
            int got = currentStuff.read(buf, off, len);
            if (got != -1)
            {
                receivedBytes += got;
                return got;
            }
            
            if (foundLastBoundary)
                return -1;
            
            // try again
            currentStuff = null;
        } while (true);
    }
    
    @Override
    public void close() throws IOException
    {
        in.close();
        currentStuff = null;
        foundLastBoundary = true;
    }
    
    //
    
    private ByteArrayInputStream currentStuff = null;
    private boolean foundLastBoundary = false;
    
    private ByteArrayInputStream readNextBinaryLine()
    {
        if (foundLastBoundary)
            return null;
        
        byte[] line = Streams.binaryReadLineIncluded(in, 4 * 1024);
        if (line == null)
            return null;
        
        foundLastBoundary = Arrays.equals(line, lastBoundary);
        return currentStuff = new ByteArrayInputStream(line);
    }
}
