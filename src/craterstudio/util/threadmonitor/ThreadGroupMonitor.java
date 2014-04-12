/*
 * Created on 10 jul 2008
 */

package craterstudio.util.threadmonitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import craterstudio.util.ArrayUtil;

public class ThreadGroupMonitor
{
    public ThreadGroupMonitor()
    {
        this(Thread.currentThread().getThreadGroup());
    }
    
    public ThreadGroupMonitor(ThreadGroup group)
    {
        this.group = group;
        lastThreadIds = new long[0];
        aliveId2mon = new HashMap<Long, ThreadMonitor>();
        deadId2mon = new HashMap<Long, ThreadMonitor>();
    }
    
    //
    
    private final ThreadGroup group;
    
    public ThreadGroup getThreadGroup()
    {
        return group;
    }
    
    //
    
    private int totalDeadThreadCount = 0;
    
    public synchronized int getTotalDeadThreadCount()
    {
        return totalDeadThreadCount;
    }
    
    //
    
    private int regularThreadCount = 0;
    
    public synchronized int getRegularThreadCount()
    {
        return regularThreadCount;
    }
    
    //
    
    private int deamonThreadCount = 0;
    
    public synchronized int getDeamonThreadCount()
    {
        return deamonThreadCount;
    }
    
    //
    
    private static final int default_slots = 3600;
    
    private long[] lastThreadIds;
    private final Map<Long, ThreadMonitor> aliveId2mon;
    private final Map<Long, ThreadMonitor> deadId2mon;
    
    public synchronized void poll()
    {
        Thread[] threads = findAllThreads();
        
        long[] currThreadIds = findAllThreadIds(threads);
        long[] newIds = findNewThreadIds(lastThreadIds, currThreadIds);
        long[] deadIds = findDeadThreadIds(lastThreadIds, currThreadIds);
        
        totalDeadThreadCount += deadIds.length;
        
        for (long newId : newIds)
            aliveId2mon.put(Long.valueOf(newId), new ThreadMonitor(newId, default_slots));
        for (long deadId : deadIds)
            deadId2mon.put(Long.valueOf(deadId), aliveId2mon.remove(Long.valueOf(deadId)));
        
        for (ThreadMonitor mon : aliveId2mon.values())
            mon.poll();
        for (ThreadMonitor mon : deadId2mon.values())
            mon.poll();
        
        analyzeThreads(threads);
        
        lastThreadIds = currThreadIds;
    }
    
    public synchronized double getAvgCpuTimeStats(int pollCount)
    {
        double sum = 0.0;
        for (ThreadMonitor mon : aliveId2mon.values())
            sum += mon.getCpuTimeStats().avg(pollCount);
        return sum;
    }
    
    public synchronized double getAvgUserTimeStats(int pollCount)
    {
        double sum = 0.0;
        for (ThreadMonitor mon : aliveId2mon.values())
            sum += mon.getUserTimeStats().avg(pollCount);
        return sum;
    }
    
    public Collection<ThreadMonitor> getAliveThreadMonitors()
    {
        return Collections.unmodifiableCollection(aliveId2mon.values());
    }
    
    public Collection<ThreadMonitor> getDeadThreadMonitors()
    {
        return Collections.unmodifiableCollection(deadId2mon.values());
    }
    
    private void analyzeThreads(Thread[] threads)
    {
        int deamonThreadCount = 0;
        int regularThreadCount = 0;
        
        for (Thread thread : threads)
        {
            if (!thread.isAlive())
                continue;
            if (thread.isDaemon()) deamonThreadCount++;
            else
                regularThreadCount++;
        }
        
        this.deamonThreadCount = deamonThreadCount;
        this.regularThreadCount = regularThreadCount;
    }
    
    public Thread[] findAllThreads()
    {
        int threadCount;
        
        Thread[] tempThreadArray = new Thread[8];
        while ((threadCount = group.enumerate(tempThreadArray)) == tempThreadArray.length)
            tempThreadArray = ArrayUtil.growTo(tempThreadArray, tempThreadArray.length * 2);
        
        Thread[] threadArray = new Thread[threadCount];
        System.arraycopy(tempThreadArray, 0, threadArray, 0, threadCount);
        return threadArray;
    }
    
    private long[] findAllThreadIds(Thread[] threads)
    {
        long[] allThreadIds = new long[threads.length];
        for (int i = 0; i < allThreadIds.length; i++)
            allThreadIds[i] = threads[i].getId();
        return allThreadIds;
    }
    
    private long[] findNewThreadIds(long[] lastThreads, long[] currThreads)
    {
        long[] newThreadIds = new long[currThreads.length];
        int newThreadIndex = 0;
        
        outer: for (int i = 0; i < currThreads.length; i++)
        {
            for (int k = 0; k < lastThreads.length; k++)
                if (currThreads[i] == lastThreads[k])
                    continue outer;
            newThreadIds[newThreadIndex++] = currThreads[i];
        }
        
        long[] ids = new long[newThreadIndex];
        System.arraycopy(newThreadIds, 0, ids, 0, newThreadIndex);
        return ids;
    }
    
    private long[] findDeadThreadIds(long[] lastThreads, long[] currThreads)
    {
        long[] deadThreadIds = new long[lastThreads.length];
        int deadThreadIndex = 0;
        
        outer: for (int i = 0; i < lastThreads.length; i++)
        {
            for (int k = 0; k < currThreads.length; k++)
                if (lastThreads[i] == currThreads[k])
                    continue outer;
            deadThreadIds[deadThreadIndex++] = lastThreads[i];
        }
        
        long[] ids = new long[deadThreadIndex];
        System.arraycopy(deadThreadIds, 0, ids, 0, deadThreadIndex);
        return ids;
    }
}
