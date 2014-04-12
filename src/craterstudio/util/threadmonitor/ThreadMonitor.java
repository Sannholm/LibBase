/*
 * Created on 10 jul 2008
 */

package craterstudio.util.threadmonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ThreadMonitor
{
    private static ThreadMXBean tmxb;
    
    static
    {
        tmxb = ManagementFactory.getThreadMXBean();
        tmxb.setThreadCpuTimeEnabled(true);
    }
    
    //
    
    private final long tid;
    private final CyclicUsageHistory cpuTimeHistory;
    private final CyclicUsageHistory userTimeHistory;
    private final CyclicUsageHistory cpuUsageHistory;
    private final CyclicUsageHistory userUsageHistory;
    
    public ThreadMonitor(long tid, int slots)
    {
        this.tid = tid;
        cpuTimeHistory = new CyclicUsageHistory(slots);
        userTimeHistory = new CyclicUsageHistory(slots);
        cpuUsageHistory = new CyclicUsageHistory(slots);
        userUsageHistory = new CyclicUsageHistory(slots);
    }
    
    public long getId()
    {
        return tid;
    }
    
    private double totalCpuTime;
    private double totalUserTime;
    
    public double getTotalCpuTime()
    {
        return totalCpuTime;
    }
    
    public double getTotalUserTime()
    {
        return totalUserTime;
    }
    
    public void poll()
    {
        // a time of -1 means not alive
        
        double cpuTime = tmxb.getThreadCpuTime(tid) / 1000000000.0;
        totalCpuTime += cpuTime < 0 ? 0 : cpuTime;
        cpuTimeHistory.log(cpuTime < 0 ? 0 : cpuTime);
        cpuUsageHistory.log(cpuTimeHistory.previous(0) - cpuTimeHistory.previous(1));
        
        double userTime = tmxb.getThreadUserTime(tid) / 1000000000.0;
        totalUserTime += userTime < 0 ? 0 : userTime;
        userTimeHistory.log(userTime < 0 ? 0 : userTime);
        userUsageHistory.log(userTimeHistory.previous(0) - userTimeHistory.previous(1));
    }
    
    public CyclicUsageHistory getCpuTimeStats()
    {
        return cpuUsageHistory;
    }
    
    public CyclicUsageHistory getUserTimeStats()
    {
        return userUsageHistory;
    }
}