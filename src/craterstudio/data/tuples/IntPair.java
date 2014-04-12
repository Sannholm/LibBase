/*
 * Created on 31 okt 2008
 */

package craterstudio.data.tuples;

import java.io.Serializable;

public class IntPair implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int a;
    private final int b;
    
    public IntPair(int a, int b)
    {
        this.a = a;
        this.b = b;
    }
    
    public int a()
    {
        return a;
    }
    
    public int b()
    {
        return b;
    }
    
    @Override
    public int hashCode()
    {
        return a ^ (b * 37);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof IntPair))
            return false;
        
        IntPair that = (IntPair)obj;
        return (a == that.a) && (b == that.b);
    }
}
