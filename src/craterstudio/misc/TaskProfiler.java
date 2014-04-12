/*
 * Created on Aug 12, 2009
 */

package craterstudio.misc;

import craterstudio.util.concur.OneThreadAccess;

public class TaskProfiler
{
    private final OneThreadAccess access;
    private boolean measuring;
    private long started;
    private long total;
    
    public TaskProfiler()
    {
        access = new OneThreadAccess();
    }
    
    private long now()
    {
        return System.nanoTime();
    }
    
    public void reset()
    {
        access.check();
        
        total = 0L;
    }
    
    public void start()
    {
        access.check();
        
        if (measuring)
            throw new IllegalStateException();
        measuring = true;
        
        started = now();
    }
    
    public void stop()
    {
        access.check();
        
        if (!measuring)
            throw new IllegalStateException();
        measuring = false;
        
        total += (now() - started);
    }
    
    public long total()
    {
        return total;
    }
}