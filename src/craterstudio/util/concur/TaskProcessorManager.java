/*
 * Created on 7 mei 2009
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class TaskProcessorManager implements Runnable
{
    private final TaskProcessor tp;
    
    public TaskProcessorManager(TaskProcessor tp)
    {
        this.tp = tp;
        minThreads = 0;
        maxThreads = Runtime.getRuntime().availableProcessors();
        interval = 250;
        maxQueueEmpty = 10 * 1000;
        maxQueueFilled = 1 * 1000;
    }
    
    public void launch()
    {
        Thread t = new Thread(this, "TaskProcessorManager");
        t.setDaemon(true);
        t.start();
    }
    
    //
    
    private volatile boolean verbose;
    
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }
    
    //
    
    private volatile long interval;
    
    public void setInterval(long interval)
    {
        this.interval = interval;
    }
    
    //
    
    private volatile int minThreads;
    private volatile int maxThreads;
    
    public void setThreadCountRange(int min, int max)
    {
        if ((min | max) < 0)
            throw new IllegalArgumentException();
        if (max == 0)
            throw new IllegalArgumentException();
        if (min > max)
            throw new IllegalArgumentException();
        
        minThreads = min;
        maxThreads = max;
    }
    
    //
    
    private volatile long maxQueueEmpty;
    private volatile long maxQueueFilled;
    
    public void setQueueTimeoutRange(long maxQueueEmpty, long maxQueueFilled)
    {
        this.maxQueueEmpty = maxQueueEmpty;
        this.maxQueueFilled = maxQueueFilled;
    }
    
    //
    
    private long queueFilledSince = Long.MAX_VALUE;
    private long queueEmptySince = Long.MAX_VALUE;
    
    @Override
    public void run()
    {
        while (true)
        {
            HighLevel.sleep(interval);
            
            int total = tp.threadCount();
            int queue = tp.queueCount();
            
            if (total > maxThreads)
            {
                tp.removeThread();
                
                if (verbose)
                {
                    System.out.println(this.getClass().getSimpleName() + " :: " + toString());
                }
            }
            else if (total < minThreads)
            {
                tp.addThread();
                
                if (verbose)
                {
                    System.out.println(this.getClass().getSimpleName() + " :: " + toString());
                }
            }
            else if (queue == 0)
            {
                if (handleEmptyQueue())
                {
                    if (verbose)
                    {
                        System.out.println(this.getClass().getSimpleName() + " :: " + toString());
                    }
                }
            }
            else
            {
                if (handleFilledQueue())
                {
                    if (verbose)
                    {
                        System.out.println(this.getClass().getSimpleName() + " :: " + toString());
                    }
                }
            }
        }
    }
    
    private boolean handleFilledQueue()
    {
        queueEmptySince = Long.MAX_VALUE;
        
        // it appears to be the first time the queue is filled
        if (queueFilledSince == Long.MAX_VALUE)
        {
            queueFilledSince = System.currentTimeMillis();
            
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - queueFilledSince;
        
        // not filled long enough
        if (elapsed < maxQueueFilled)
        {
            return false;
        }
        
        // we reached the maximum thread count
        if (tp.threadCount() >= maxThreads)
        {
            return false;
        }
        
        tp.addThread();
        
        queueFilledSince = Long.MAX_VALUE;
        
        return true;
    }
    
    private boolean handleEmptyQueue()
    {
        queueFilledSince = Long.MAX_VALUE;
        
        // it appears to be the first time the queue is empty
        if (queueEmptySince == Long.MAX_VALUE)
        {
            queueEmptySince = System.currentTimeMillis();
            
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - queueEmptySince;
        
        // not idle long enough
        if (elapsed < maxQueueEmpty)
        {
            return false;
        }
        
        // we reached the minimum thread count
        if (tp.threadCount() <= minThreads)
        {
            return false;
        }
        
        // don't remove if we're busy
        if (tp.busyCount() == tp.threadCount())
        {
            return false;
        }
        
        tp.removeThread();
        
        queueEmptySince = Long.MAX_VALUE;
        
        return true;
    }
    
    @Override
    public String toString()
    {
        return tp.busyCount() + "/" + tp.threadCount() + " (+" + tp.queueCount() + ")";
    }
}
