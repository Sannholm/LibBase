/*
 * Created on 31 okt 2008
 */

package craterstudio.data.tuples;

import java.io.Serializable;

public class LongPair implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final long a;
    private final long b;
    
    public LongPair(long a, long b)
    {
        this.a = a;
        this.b = b;
    }
    
    public long a()
    {
        return a;
    }
    
    public long b()
    {
        return b;
    }
    
    @Override
    public int hashCode()
    {
        return (int)(a ^ (b * 37));
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LongPair))
            return false;
        
        LongPair that = (LongPair)obj;
        return (a == that.a) && (b == that.b);
    }
}
