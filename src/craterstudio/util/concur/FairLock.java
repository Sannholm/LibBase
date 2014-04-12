/*
 * Created on Nov 16, 2008
 */

package craterstudio.util.concur;

import craterstudio.util.HighLevel;

public class FairLock
{
    private final ConcurrentQueue<OneTimeLock> queue;
    
    public FairLock()
    {
        queue = new ConcurrentQueue<OneTimeLock>(true);
    }
    
    public OneTimeLock aquire()
    {
        OneTimeLock oneTimeLock = new OneTimeLock();
        
        // wait for turn
        synchronized (queue.mutex())
        {
            queue.produce(oneTimeLock);
            
            while (queue.peek() != oneTimeLock)
            {
                HighLevel.wait(queue.mutex());
            }
            
            if (queue.poll() != oneTimeLock)
            {
                throw new IllegalStateException("paranoid");
            }
        }
        
        return oneTimeLock;
    }
}