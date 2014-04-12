/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class IntFuture
{
    private boolean waiting;
    private int result;
    private Exception error;
    private final Object lock;
    
    public IntFuture()
    {
        waiting = true;
        lock = new Object();
    }
    
    public boolean isDone()
    {
        synchronized (lock)
        {
            return !waiting;
        }
    }
    
    public int peek(int defaultValue)
    {
        synchronized (lock)
        {
            if (waiting)
            {
                return defaultValue;
            }
            
            if (error != null)
                throw new IllegalStateException("future error", error);
            return result;
        }
    }
    
    public int get()
    {
        synchronized (lock)
        {
            while (waiting)
            {
                HighLevel.wait(lock);
            }
            
            if (error != null)
                throw new IllegalStateException("future error", error);
            return result;
        }
    }
    
    public void set(int result)
    {
        synchronized (lock)
        {
            if (!waiting)
                throw new IllegalStateException("future already set");
            
            this.result = result;
            waiting = false;
            lock.notifyAll();
        }
    }
    
    public void error(Exception error)
    {
        if (error == null)
            throw new NullPointerException();
        
        synchronized (lock)
        {
            if (!waiting)
                throw new IllegalStateException("future already set");
            
            this.error = error;
            waiting = false;
            lock.notifyAll();
        }
    }
}