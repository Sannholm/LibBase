/*
 * Created on 1 jun 2010
 */

package craterstudio.time;

import java.util.concurrent.atomic.AtomicLong;

import craterstudio.text.TextValues;

public class Bandwidth
{
    private final Interval oneSecond;
    private final AtomicLong trafficSecond;
    
    public Bandwidth()
    {
        oneSecond = new Interval(1000L);
        trafficSecond = new AtomicLong();
    }
    
    protected void log(long bandwidth)
    {
        String formatted = TextValues.formatWithMagnitudeBytes(bandwidth, 1);
        System.out.println("bandwidth: " + formatted + "B");
    }
    
    public void traffic(int bytes)
    {
        long bandwidth = trafficSecond.addAndGet(bytes);
        
        if (oneSecond.hasPassedAndStep())
        {
            trafficSecond.addAndGet(-bandwidth);
            
            log(bandwidth);
        }
    }
}
