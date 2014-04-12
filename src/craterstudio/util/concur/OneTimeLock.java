/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class OneTimeLock
{
    private volatile boolean locked;
    private final Object lock;
    
    public OneTimeLock()
    {
        locked = true;
        lock = new Object();
    }
    
    public void waitFor()
    {
        synchronized (lock)
        {
            while (locked)
            {
                HighLevel.wait(lock);
            }
        }
    }
    
    public void yieldFor()
    {
        while (locked)
        {
            Thread.yield();
        }
    }
    
    public boolean isLocked()
    {
        synchronized (lock)
        {
            return locked;
        }
    }
    
    public void release()
    {
        synchronized (lock)
        {
            if (!locked)
                throw new IllegalStateException("already released");
            locked = false;
            
            lock.notifyAll();
        }
    }
}
