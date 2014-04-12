/*
 * Created on 5-aug-2006
 */

package craterstudio.math;

public class Sphere
{
    public Sphere()
    {
        this(0, 0, 0, 0);
    }
    
    public Sphere(float x, float y, float z, float radius)
    {
        origin.load(x, y, z);
        this.radius = radius;
    }
    
    public Sphere(Sphere s)
    {
        origin.load(s.origin);
        radius = s.radius;
    }
    
    public Vec3 origin = new Vec3();
    public float radius;
    
    public final void load(float x, float y, float z, float radius)
    {
        origin.load(x, y, z);
        this.radius = radius;
    }
    
    public final void load(Sphere that)
    {
        origin.load(that.origin);
        radius = that.radius;
    }
    
    @Override
    public String toString()
    {
        return "Sphere[origin=" + origin + ",radius=" + radius + "]";
    }
}