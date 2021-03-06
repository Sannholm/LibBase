/*
 * Created on 10 jul 2008
 */

package craterstudio.util.threadmonitor;

import java.util.Arrays;

public class CyclicUsageHistory
{
    private final double[] values;
    
    public CyclicUsageHistory(int slots)
    {
        values = new double[slots];
    }
    
    private int addIndex;
    
    public void log(double value)
    {
        values[addIndex++ % values.length] = value;
    }
    
    //
    
    public double previous()
    {
        return this.previous(0);
    }
    
    public double previous(int age)
    {
        int len = values.length;
        return values[(((addIndex - 1 - age) % len) + len) % len];
    }
    
    //
    
    public double max()
    {
        return this.max(values.length);
    }
    
    public double max(int slots)
    {
        int count = Math.min(values.length, Math.min(slots, addIndex - 1));
        
        double max = 0.0;
        for (int i = 0; i < count; i++)
            if (this.previous(i) > max)
                max = this.previous(i);
        return max;
    }
    
    //
    
    public double sum()
    {
        return this.sum(values.length);
    }
    
    public double sum(int slots)
    {
        int count = Math.min(values.length, Math.min(slots, addIndex - 1));
        
        double sum = 0.0;
        for (int i = 0; i < count; i++)
            sum += this.previous(i);
        return sum;
    }
    
    //
    
    public double avg()
    {
        return this.avg(values.length);
    }
    
    public double avg(int slots)
    {
        int count = Math.min(values.length, Math.min(slots, addIndex - 1));
        
        return this.sum(slots) / count;
    }
    
    //
    
    public double nom()
    {
        return this.nom(values.length);
    }
    
    public double nom(int slots)
    {
        int count = Math.min(values.length, Math.min(slots, addIndex - 1));
        if (count == 0)
            return 0.0;
        
        double[] arr = new double[count];
        for (int i = 0; i < count; i++)
            arr[i] = this.previous(i);
        Arrays.sort(arr);
        return arr[arr.length / 2];
    }
}