/*
 * Created on 30 aug 2010
 */

package craterstudio.util.concur;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LockSet implements Lock
{
    private final Lock[] locks;
    
    public LockSet(Lock... locks)
    {
        this.locks = locks;
    }
    
    @Override
    public Condition newCondition()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void lock()
    {
        while (!this.tryLock())
        {
            // spin!
        }
    }
    
    public void lock(long retryDelay)
    {
        while (!this.tryLock())
        {
            try
            {
                Thread.sleep(retryDelay);
            }
            catch (InterruptedException exc)
            {
                // ignore
            }
        }
    }
    
    @Override
    public boolean tryLock()
    {
        checkNotLocked();
        
        for (int i = 0; i < locks.length; i++)
            if (!locks[i].tryLock())
                return rollback(i);
        return success();
    }
    
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
        checkNotLocked();
        
        long nanos = unit.toNanos(time);
        long started = System.nanoTime();
        long expires = started + nanos;
        
        for (int i = 0; i < locks.length; i++)
        {
            try
            {
                long remaining = expires - System.nanoTime();
                if (!locks[i].tryLock(remaining, TimeUnit.NANOSECONDS))
                    return rollback(i);
            }
            catch (InterruptedException exc)
            {
                rollback(i);
                throw exc;
            }
        }
        
        return success();
    }
    
    @Override
    public void lockInterruptibly() throws InterruptedException
    {
        checkNotLocked();
        
        for (int i = 0; i < locks.length; i++)
        {
            try
            {
                locks[i].lockInterruptibly();
            }
            catch (InterruptedException exc)
            {
                rollback(i);
                throw exc;
            }
        }
        
        success();
    }
    
    @Override
    public void unlock()
    {
        checkLocked();
        
        for (Lock lock : locks)
            lock.unlock();
        isLocked = false;
    }
    
    // state
    
    private boolean isLocked;
    
    private final void checkLocked()
    {
        if (!isLocked)
            throw new IllegalStateException();
    }
    
    private final void checkNotLocked()
    {
        if (isLocked)
            throw new IllegalStateException();
    }
    
    private final boolean success()
    {
        return (isLocked = true); // assignment is correct
    }
    
    private final boolean rollback(int i)
    {
        while (--i >= 0)
            locks[i].unlock();
        return false;
    }
}