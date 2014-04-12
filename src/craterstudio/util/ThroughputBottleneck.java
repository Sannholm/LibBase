/*
 * Created on 25 mei 2010
 */

package craterstudio.util;

import craterstudio.time.Clock;

public class ThroughputBottleneck implements Bottleneck
{
    private final int maxTrafficPerTimeUnit;
    private final int timeUnitInMillis;
    private int timeUnitFeedCounter;
    private long timeUnitIdentifier;
    
    public ThroughputBottleneck(int maxTrafficPerSecond)
    {
        this(maxTrafficPerSecond, 1000);
    }
    
    public ThroughputBottleneck(int maxTrafficPerTimeUnit, int timeUnitInMillis)
    {
        this.maxTrafficPerTimeUnit = maxTrafficPerTimeUnit;
        this.timeUnitInMillis = timeUnitInMillis;
    }
    
    @Override
    public int feed(int bytes)
    {
        while (true)
        {
            int allow = feedImpl(bytes);
            if (allow == 0)
                continue;
            timeUnitFeedCounter += allow;
            return allow;
        }
    }
    
    public void cancel(int amount)
    {
        timeUnitFeedCounter -= amount;
    }
    
    private int feedImpl(int amount)
    {
        long now = Clock.now();
        long currentSecondIdentifier = now / timeUnitInMillis;
        
        if (timeUnitIdentifier != currentSecondIdentifier)
        {
            timeUnitIdentifier = currentSecondIdentifier;
            timeUnitFeedCounter = 0;
        }
        
        amount = Math.max(0, Math.min(amount, maxTrafficPerTimeUnit - timeUnitFeedCounter));
        
        if (amount == 0)
        {
            long delay = timeUnitInMillis - (now % timeUnitInMillis);
            
            try
            {
                Thread.sleep(delay);
            }
            catch (InterruptedException exc)
            {
                // ignore
            }
        }
        
        return amount;
    }
}