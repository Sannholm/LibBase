/*
 * Created on 30 sep 2008
 */

package craterstudio.streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataStreamFetcher
{
    public static final DataInputStream reverse(byte[] data)
    {
        return new DataInputStream(new ByteArrayInputStream(data));
    }
    
    ByteArrayOutputStream baos;
    private final DataOutputStream dos;
    
    public DataStreamFetcher()
    {
        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
    }
    
    public DataOutputStream getDataOutputStream()
    {
        return dos;
    }
    
    public byte[] toByteArray()
    {
        try
        {
            dos.flush();
        }
        catch (IOException exc)
        {
            throw new IllegalStateException(exc);
        }
        
        return baos.toByteArray();
    }
}