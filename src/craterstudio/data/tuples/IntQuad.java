/*
 * Created on 31 okt 2008
 */

package craterstudio.data.tuples;

import java.io.Serializable;

public class IntQuad implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int a;
    private final int b;
    private final int c;
    private final int d;
    
    public IntQuad(int a, int b, int c, int d)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    
    public int a()
    {
        return a;
    }
    
    public int b()
    {
        return b;
    }
    
    public int c()
    {
        return c;
    }
    
    public int d()
    {
        return d;
    }
    
    @Override
    public int hashCode()
    {
        return (a * 7) ^ (b * 17) ^ (c * 27) ^ (d * 37);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof IntQuad))
            return false;
        
        IntQuad that = (IntQuad)obj;
        return (a == that.a) && (b == that.b) && (c == that.c) && (d == that.d);
    }
}
