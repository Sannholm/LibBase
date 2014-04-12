/*
 * Created on 8 jan 2010
 */

package craterstudio.util.concur;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AwaitZeroLatch
{
    private final AtomicInteger value;
    private final CountDownLatch latch;
    
    public AwaitZeroLatch()
    {
        this(0);
    }
    
    public AwaitZeroLatch(int initialValue)
    {
        value = new AtomicInteger(initialValue);
        latch = new CountDownLatch(1);
    }
    
    public int increment()
    {
        return value.incrementAndGet();
    }
    
    public int decrement()
    {
        int v = value.decrementAndGet();
        
        if (v == 0)
        {
            latch.countDown();
        }
        else if (v < 0)
        {
            throw new IllegalStateException();
        }
        
        return v;
    }
    
    public void await() throws InterruptedException
    {
        latch.await();
    }
    
    public void await(long time, TimeUnit unit) throws InterruptedException
    {
        latch.await(time, unit);
    }
}