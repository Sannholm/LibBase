/*
 * Created on 23 apr 2010
 */

package craterstudio.time;

import java.util.Arrays;

public class Measurements
{
    private static final long self_time;
    
    static
    {
        final int runs = 8;
        final int iterations = 1024;
        
        long selftime = -1L;
        for (int k = 0; k < runs; k++)
        {
            long t0 = System.nanoTime();
            for (int i = 0; i < iterations; i++)
                System.nanoTime();
            long t1 = System.nanoTime();
            selftime = (t1 - t0) / (iterations + 2);
        }
        System.out.println(Measurements.class.getSimpleName() + ".selftime: " + selftime);
        self_time = selftime;
    }
    
    private final long[] slots;
    private int offset;
    private boolean dirty;
    
    public Measurements(int slotCount)
    {
        slots = new long[slotCount];
        offset = 0;
        dirty = true;
    }
    
    public void addNanos(long time)
    {
        time -= self_time * 2; // assuming System.nanoTime() is called twice
        slots[offset % slots.length] = time;
        offset += 1;
        dirty = true;
    }
    
    public void addMicros(long time)
    {
        addNanos(time * 1000L);
    }
    
    public void addMillis(long time)
    {
        addNanos(time * 1000000L);
    }
    
    public long min()
    {
        update();
        return slots[0];
    }
    
    public long max()
    {
        update();
        return slots[lastIndex()];
    }
    
    public long avg()
    {
        update();
        int end = lastIndex();
        long sum = 0;
        for (int i = 0; i < end; i++)
            sum += slots[i];
        return (long)(sum / (double)end);
    }
    
    public double avg95()
    {
        update();
        int end = Math.max(1, (int)(lastIndex() * 0.95));
        long sum = 0;
        for (int i = 0; i < end; i++)
            sum += slots[i];
        return (double)sum / end;
    }
    
    public long typical()
    {
        update();
        return slots[lastIndex() / 2];
    }
    
    //
    
    private void update()
    {
        if (offset == 0)
            throw new IllegalStateException();
        if (!dirty)
            return;
        dirty = false;
        
        Arrays.sort(slots, 0, lastIndex() + 1);
    }
    
    private int lastIndex()
    {
        return Math.min(offset, slots.length) - 1;
    }
    
    @Override
    public String toString()
    {
        long min = min();
        long max = max();
        long avg = avg();
        long typ = typical();
        
        int shifts = 0;
        while (min > 3 * 1000L)
        {
            min /= 1000L;
            max /= 1000L;
            avg /= 1000L;
            typ /= 1000L;
            shifts++;
        }
        return this.getClass().getSimpleName() + "[" + names[shifts] + ": typical=" + typ + ", avg=" + avg + ", min=" + min + ", max=" + max + "]";
    }
    
    private static final String[] names = new String[]{"nanos", "miscros", "millis", "seconds"};
}
