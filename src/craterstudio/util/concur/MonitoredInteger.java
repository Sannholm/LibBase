/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class MonitoredInteger
{
    private int value;
    private final Object lock;
    
    public MonitoredInteger()
    {
        value = 0;
        lock = new Object();
    }
    
    //
    
    public void set(int value)
    {
        synchronized (lock)
        {
            this.value = value;
            
            lock.notifyAll();
        }
    }
    
    public int get()
    {
        synchronized (lock)
        {
            return value;
        }
    }
    
    public int adjust(int amount)
    {
        synchronized (lock)
        {
            value += amount;
            
            lock.notifyAll();
            
            return value;
        }
    }
    
    //
    
    public int waitForModification()
    {
        synchronized (lock)
        {
            int valueAtStart = value;
            
            while (value != valueAtStart)
            {
                HighLevel.wait(lock);
            }
            
            return value;
        }
    }
    
    public void waitForEqual(int value)
    {
        synchronized (lock)
        {
            while (this.value != value)
            {
                HighLevel.wait(lock);
            }
        }
    }
    
    public void waitForNotEqual(int value)
    {
        synchronized (lock)
        {
            while (this.value == value)
            {
                HighLevel.wait(lock);
            }
        }
    }
    
    public int waitForLessThan(int value)
    {
        synchronized (lock)
        {
            while (this.value >= value)
            {
                HighLevel.wait(lock);
            }
            
            return this.value;
        }
    }
    
    public int waitForGreaterThan(int value)
    {
        synchronized (lock)
        {
            while (this.value <= value)
            {
                HighLevel.wait(lock);
            }
            
            return this.value;
        }
    }
    
    //
    
    public int waitForEqualAndAdjust(int equal, int adjust)
    {
        synchronized (lock)
        {
            waitForEqual(equal);
            adjust(adjust);
            return value;
        }
    }
    
    public int waitForNotEqualAndAdjust(int notEqual, int adjust)
    {
        synchronized (lock)
        {
            waitForNotEqual(notEqual);
            adjust(adjust);
            return value;
        }
    }
    
    public int waitForLessThanAndAdjust(int lessThan, int adjust)
    {
        synchronized (lock)
        {
            waitForLessThan(lessThan);
            adjust(adjust);
            return value;
        }
    }
    
    public int waitForGreaterThanAndAdjust(int greaterThan, int adjust)
    {
        synchronized (lock)
        {
            waitForGreaterThan(greaterThan);
            adjust(adjust);
            return value;
        }
    }
    
    //
    
    public int waitForEqualAndSet(int equal, int set)
    {
        synchronized (lock)
        {
            waitForEqual(equal);
            set(set);
            return value;
        }
    }
    
    public int waitForNotEqualAndSet(int notEqual, int set)
    {
        synchronized (lock)
        {
            waitForNotEqual(notEqual);
            set(set);
            return value;
        }
    }
    
    public int waitForLessThanAndSet(int lessThan, int set)
    {
        synchronized (lock)
        {
            waitForLessThan(lessThan);
            set(set);
            return value;
        }
    }
    
    public int waitForGreaterThanAndSet(int greaterThan, int set)
    {
        synchronized (lock)
        {
            waitForGreaterThan(greaterThan);
            set(set);
            return value;
        }
    }
}