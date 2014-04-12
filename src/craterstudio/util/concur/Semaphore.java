/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class Semaphore
{
    private int available;
    private int poolSize;
    private final Object lock;
    
    public Semaphore(int poolSize)
    {
        if (poolSize < 0)
        {
            throw new IllegalStateException();
        }
        
        available = poolSize;
        this.poolSize = poolSize;
        lock = new Object();
    }
    
    //
    
    public Semaphore aquire()
    {
        return this.aquire(1);
    }
    
    public Semaphore aquire(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalStateException();
        }
        
        synchronized (lock)
        {
            while (available < amount)
            {
                HighLevel.wait(lock);
            }
            
            available -= amount;
            
            lock.notifyAll();
        }
        
        return this;
    }
    
    public Semaphore release()
    {
        return this.release(1);
    }
    
    public Semaphore release(int amount)
    {
        if (amount < 0)
        {
            throw new IllegalStateException();
        }
        
        synchronized (lock)
        {
            if (available + amount > poolSize)
                throw new IllegalStateException("released more than aquired");
            available += amount;
            
            lock.notifyAll();
        }
        
        return this;
    }
    
    //
    
    public int peekAvailable()
    {
        synchronized (lock)
        {
            return available;
        }
    }
    
    //
    
    public void setPoolSize(int poolSize)
    {
        synchronized (lock)
        {
            if (poolSize <= 0)
            {
                throw new IllegalStateException();
            }
            
            adjustPoolSize(poolSize - this.poolSize);
        }
    }
    
    public int getPoolSize()
    {
        synchronized (lock)
        {
            return poolSize;
        }
    }
    
    public int adjustPoolSize(int amount)
    {
        synchronized (lock)
        {
            // shrink once there are enough resources available
            if (amount < 0)
            {
                int shrinkAmount = -amount;
                while (shrinkAmount > available)
                {
                    HighLevel.wait(lock);
                }
            }
            
            available += amount;
            poolSize += amount;
            
            lock.notifyAll();
            
            return poolSize;
        }
    }
}