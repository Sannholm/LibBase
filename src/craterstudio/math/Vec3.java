/*
 * Created on 8-jul-2007
 */

package craterstudio.math;

import craterstudio.text.TextValues;

public class Vec3
{
    public static final Vec3 ZERO = new Vec3();
    
    public float x, y, z;
    
    //
    
    public Vec3()
    {
        load(0.0f);
    }
    
    public Vec3(float xyz)
    {
        load(xyz);
    }
    
    public Vec3(float x, float y, float z)
    {
        load(x, y, z);
    }
    
    public Vec3(float[] v, int pos)
    {
        load(v, pos);
    }
    
    public Vec3(Vec3 v)
    {
        load(v);
    }
    
    public boolean isZero()
    {
        return x == 0.0f && y == 0.0f && z == 0.0f;
    }
    
    /**
     * LOAD
     */
    
    public Vec3 load(Vec3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
        
        return this;
    }
    
    public Vec3 load(float val)
    {
        x = val;
        y = val;
        z = val;
        
        return this;
    }
    
    public Vec3 load(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        
        return this;
    }
    
    public Vec3 load(float[] arr, int pos)
    {
        x = arr[pos + 0];
        y = arr[pos + 1];
        z = arr[pos + 2];
        
        return this;
    }
    
    /**
     * STORE
     */
    
    public final float[] store(float[] arr, int off)
    {
        arr[off + 0] = x;
        arr[off + 1] = y;
        arr[off + 2] = z;
        return arr;
    }
    
    /**
     * CALC
     */
    public float squaredDistance(Vec3 that)
    {
        float dx = x - that.x;
        float dy = y - that.y;
        float dz = z - that.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }
    
    public float distance(Vec3 that)
    {
        return (float)Math.sqrt(squaredDistance(that));
    }
    
    public float squaredLength()
    {
        return (x * x) + (y * y) + (z * z);
    }
    
    public float length()
    {
        return (float)Math.sqrt(squaredLength());
    }
    
    private static final float minimal = Math.ulp(0.0f);
    
    public Vec3 length(float val)
    {
        float sqlen = squaredLength();
        if (sqlen <= minimal)
            return this;
        float li = val / (float)Math.sqrt(sqlen);
        
        x *= li;
        y *= li;
        z *= li;
        
        return this;
    }
    
    public Vec3 normalize()
    {
        float li = 1.0f / (float)Math.sqrt(squaredLength());
        
        x *= li;
        y *= li;
        z *= li;
        
        return this;
    }
    
    public Vec3 inv()
    {
        x *= -1.0f;
        y *= -1.0f;
        z *= -1.0f;
        
        return this;
    }
    
    public Vec3 abs()
    {
        if (x < 0.0F)
            x = -x;
        if (y < 0.0F)
            y = -y;
        if (z < 0.0F)
            z = -z;
        
        return this;
    }
    
    //
    
    public Vec3 add(float x, float y, float z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        
        return this;
    }
    
    public Vec3 sub(float x, float y, float z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        
        return this;
    }
    
    public Vec3 mul(float x, float y, float z)
    {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        
        return this;
    }
    
    public Vec3 div(float x, float y, float z)
    {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        
        return this;
    }
    
    //
    
    public Vec3 add(float xyz)
    {
        return add(xyz, xyz, xyz);
    }
    
    public Vec3 sub(float xyz)
    {
        return sub(xyz, xyz, xyz);
    }
    
    public Vec3 mul(float xyz)
    {
        return mul(xyz, xyz, xyz);
    }
    
    public Vec3 div(float xyz)
    {
        return div(xyz, xyz, xyz);
    }
    
    //
    
    public Vec3 add(Vec3 vec)
    {
        return add(vec.x, vec.y, vec.z);
    }
    
    public Vec3 sub(Vec3 vec)
    {
        return sub(vec.x, vec.y, vec.z);
    }
    
    public Vec3 mul(Vec3 vec)
    {
        return mul(vec.x, vec.y, vec.z);
    }
    
    public Vec3 div(Vec3 vec)
    {
        return div(vec.x, vec.y, vec.z);
    }
    
    //
    
    public Vec3 min(Vec3 vec)
    {
        if (vec.x < x)
            x = (vec.x);
        if (vec.y < y)
            y = (vec.y);
        if (vec.z < z)
            z = (vec.z);
        
        return this;
    }
    
    public Vec3 max(Vec3 vec)
    {
        if (vec.x > x)
            x = (vec.x);
        if (vec.y > y)
            y = (vec.y);
        if (vec.z > z)
            z = (vec.z);
        
        return this;
    }
    
    /**
     * EQUALS
     */
    
    public boolean equals(Vec3 vec, float margin)
    {
        boolean bX = EasyMath.equals(x, vec.x, margin);
        boolean bY = EasyMath.equals(y, vec.y, margin);
        boolean bZ = EasyMath.equals(z, vec.z, margin);
        
        return bX && bY && bZ;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Vec3))
            return false;
        return this.equals((Vec3)obj, 0.001f);
    }
    
    @Override
    public int hashCode()
    {
        int xi = (int)(x * 1000);
        int yi = (int)(y * 1000);
        int zi = (int)(z * 1000);
        
        return xi ^ yi ^ zi;
    }
    
    /**
     * TO STRING
     */
    
    @Override
    public String toString()
    {
        String sX = Float.isNaN(x) ? "NaN" : TextValues.formatNumber(x, 3);
        String sY = Float.isNaN(y) ? "NaN" : TextValues.formatNumber(y, 3);
        String sZ = Float.isNaN(z) ? "NaN" : TextValues.formatNumber(z, 3);
        return "Vec3[" + sX + ", " + sY + ", " + sZ + "]";
    }
}
