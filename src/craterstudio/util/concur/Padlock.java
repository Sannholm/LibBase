/*
 * Created on 18-sep-2007
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class Padlock
{
    private volatile Key key = null;
    private final Object mutex = new Object();
    
    /**
     * Waits for this lock to become unlocked (if necessary), and locks it.
     */
    
    public Key lock()
    {
        synchronized (mutex)
        {
            while (key != null)
            {
                HighLevel.wait(mutex);
            }
            
            return key = new Key();
        }
    }
    
    public Key tryLock()
    {
        synchronized (mutex)
        {
            if (key != null)
            {
                return null;
            }
            
            return key = new Key();
        }
    }
    
    public Object mutex()
    {
        return mutex;
    }
    
    public boolean isLocked()
    {
        synchronized (mutex)
        {
            return key != null;
        }
    }
    
    public void unlock(Key key)
    {
        synchronized (mutex)
        {
            if (key == null || this.key != key)
                throw new IllegalArgumentException("invalid key");
            
            this.key = null;
            mutex.notifyAll();
        }
    }
    
    public void forceUnlock()
    {
        synchronized (mutex)
        {
            unlock(key);
        }
    }
    
    public void waitFor()
    {
        unlock(lock());
    }
    
    public class Key
    {
        Key()
        {
            //
        }
    }
}