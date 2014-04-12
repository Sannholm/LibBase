/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import java.util.concurrent.atomic.AtomicInteger;

public class Barrier
{
    private final AtomicInteger pending, cumulative;
    private final SimpleCountDownLatch latch;
    
    public Barrier()
    {
        this(0);
    }
    
    public Barrier(int registrations)
    {
        pending = new AtomicInteger(registrations);
        cumulative = new AtomicInteger(registrations);
        latch = new SimpleCountDownLatch();
    }
    
    public void register()
    {
        if (latch.isDone())
        {
            throw new IllegalStateException("already reached barrier");
        }
        
        pending.incrementAndGet();
        cumulative.incrementAndGet();
    }
    
    public void notifyAwait()
    {
        if (!notifyDone())
        {
            latch.await();
        }
    }
    
    public boolean notifyDone()
    {
        int got = pending.decrementAndGet();
        if (got < 0)
            throw new IllegalStateException();
        if (got != 0)
            return false;
        
        latch.countDown();
        return true;
    }
    
    //
    
    public boolean isDone()
    {
        return latch.isDone();
    }
    
    public void waitForAll()
    {
        if (pending.intValue() > 0)
        {
            latch.await();
        }
    }
    
    public int countPending()
    {
        return Math.max(0, pending.intValue());
    }
    
    public int countTotal()
    {
        return cumulative.intValue();
    }
}
