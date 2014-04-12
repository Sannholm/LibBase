/*
 * Created on 27 feb 2008
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.OutputStream;

import craterstudio.data.ByteList;

public class LineEventOutputStream extends OutputStream
{
    private final OutputStream out;
    
    private final byte[] insertBefore;
    private final byte[] insertAfter;
    
    public LineEventOutputStream(OutputStream out)
    {
        this(out, null, null);
    }
    
    public LineEventOutputStream(OutputStream out, byte[] before, byte[] after)
    {
        this.out = out;
        matchLength = 0;
        insertBefore = before;
        insertAfter = after;
    }
    
    protected void beforeLine(OutputStream out) throws IOException
    {
        if (insertBefore != null)
            out.write(insertBefore);
    }
    
    protected void afterLine(OutputStream out) throws IOException
    {
        if (insertAfter != null)
            out.write(insertAfter);
    }
    
    private int matchLength;
    private final ByteList currentLine = new ByteList();
    
    @Override
    public void write(int b) throws IOException
    {
        currentLine.add((byte)b);
        
        switch (matchLength)
        {
            case 0:
                if (b != '\r')
                {
                    matchLength = 0;
                    break;
                }
                matchLength++;
                break;
            
            case 1:
                if (b != '\n')
                {
                    matchLength = 0;
                    break;
                }
                
                byte[] line = currentLine.toArray();
                currentLine.clear();
                
                beforeLine(out);
                out.write(line);
                afterLine(out);
                
                matchLength = 0;
                break;
        }
    }
    
    @Override
    public void write(byte[] buf) throws IOException
    {
        this.write(buf, 0, buf.length);
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        for (int i = 0; i < len; i++)
            this.write(buf[off + i]);
    }
    
    @Override
    public void flush() throws IOException
    {
        out.flush();
    }
    
    @Override
    public void close() throws IOException
    {
        if (currentLine.size() > 0)
        {
            byte[] tail = currentLine.toArray();
            currentLine.clear();
            
            beforeLine(out);
            out.write(tail);
            afterLine(out);
        }
        
        out.close();
    }
}
