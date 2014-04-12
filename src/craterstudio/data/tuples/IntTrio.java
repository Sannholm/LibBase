/*
 * Created on 31 okt 2008
 */

package craterstudio.data.tuples;

import java.io.Serializable;

public class IntTrio implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int a;
    private final int b;
    private final int c;
    
    public IntTrio(int a, int b, int c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
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
    
    @Override
    public int hashCode()
    {
        return a ^ (b * 37) ^ (c * 37);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof IntTrio))
            return false;
        
        IntTrio that = (IntTrio)obj;
        return (a == that.a) && (b == that.b) && (c == that.c);
    }
}
