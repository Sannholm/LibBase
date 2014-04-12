/*
 * Created on 16-mei-2005
 */
package craterstudio.time;

import craterstudio.util.HighLevel;

public class Interval
{
    public static Interval create(long delay, long interval)
    {
        return new Interval(interval, Clock.now() + delay - interval);
    }
    
    private long timestamp;
    private final long interval;
    
    public Interval(long interval)
    {
        this(interval, Clock.now());
    }
    
    public Interval(long interval, long timestamp)
    {
        this.interval = interval;
        this.timestamp = timestamp;
    }
    
    public final long getInterval()
    {
        return interval;
    }
    
    public final long getTimeLeft()
    {
        long next = timestamp + interval;
        long left = next - Clock.now();
        return left;
    }
    
    public final void stepOver()
    {
        long now = Clock.now();
        while (timestamp < now)
            timestamp += interval;
    }
    
    public final boolean hasPassedAndStep()
    {
        boolean passed = Clock.now() >= (timestamp + interval);
        if (passed)
            timestamp += interval;
        return passed;
    }
    
    public final boolean hasPassedAndStepOver()
    {
        boolean passed = hasPassedAndStep();
        if (passed)
            stepOver();
        return passed;
    }
    
    public final void waitFor()
    {
        while (!hasPassedAndStep())
        {
            HighLevel.sleep(1);
        }
    }
}