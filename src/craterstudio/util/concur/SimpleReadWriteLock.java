/*
 * Created on 19 aug 2010
 */

package craterstudio.util.concur;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleReadWriteLock
{
    private final ReentrantReadWriteLock backing;
    
    public SimpleReadWriteLock()
    {
        backing = new ReentrantReadWriteLock();
    }
    
    public SimpleReadWriteLock(boolean forWriting)
    {
        this();
        
        if (forWriting) beginWrite();
        else
            beginRead();
    }
    
    //
    
    public boolean hasReadAccess()
    {
        return hasWriteAccess() || backing.getReadHoldCount() > 0;
    }
    
    public boolean hasWriteAccess()
    {
        return backing.isWriteLockedByCurrentThread();
    }
    
    //
    
    public void checkAccess(boolean forWriting)
    {
        if (forWriting) checkWriteAccess();
        else
            checkReadAccess();
    }
    
    public void checkReadAccess()
    {
        if (!hasReadAccess())
            throw new IllegalThreadStateException();
    }
    
    public void checkWriteAccess()
    {
        if (!hasWriteAccess())
            throw new IllegalThreadStateException();
    }
    
    //
    
    public void beginRead()
    {
        if (hasReadAccess())
            return;
        
        backing.readLock().lock();
    }
    
    public void beginWrite()
    {
        if (hasWriteAccess())
            return;
        
        if (hasReadAccess())
            throw new IllegalStateException("cannot aquire write-lock when read-lock is held");
        
        backing.writeLock().lock();
    }
    
    //
    
    public void finish()
    {
        while (backing.getReadHoldCount() > 0)
            backing.readLock().unlock();
        
        while (backing.getWriteHoldCount() > 0)
            backing.writeLock().unlock();
    }
}
