/*
 * Created on 24 dec 2007
 */

package craterstudio.data;

import craterstudio.bytes.Arcfour;

public class EncryptedByteArray
{
    private final Arcfour encoder;
    private Arcfour decoder;
    private final byte[] data;
    
    public EncryptedByteArray()
    {
        this(0);
    }
    
    public EncryptedByteArray(int bytes)
    {
        encoder = new Arcfour();
        data = new byte[bytes];
    }
    
    //
    
    public final void set(int index, byte value)
    {
        decoder = encoder.copy();
        encoder.skip(index - 1);
        data[index] = encoder.crypt(value);
    }
    
    public final byte get(int index)
    {
        decoder = decoder.copy();
        encoder.skip(index - 1);
        return decoder.crypt(data[index]);
    }
    
    public final void renew()
    {
        decoder = encoder.copy();
        encoder.crypt(data, 0, data.length);
    }
}