/*
 * Created on Aug 9, 2005
 */
package craterstudio.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMonitor
{
    private final File dir;
    private final FileFilter filter;
    
    public DirectoryMonitor(File dir)
    {
        this(dir, true);
    }
    
    public DirectoryMonitor(final File dir, final boolean recursive)
    {
        this(dir, new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                if (recursive)
                    return true;
                return pathname.getParentFile().equals(dir);
            }
        });
    }
    
    public DirectoryMonitor(File dir, FileFilter filter)
    {
        this.dir = dir;
        this.filter = filter;
        
        startWithCreateEvents = false;
        updatingIdleTime = 3000;
    }
    
    /**
    * 
    */
    
    private boolean startWithCreateEvents;
    
    public final void setStartWithCreateEvents(boolean startWithCreateEvents)
    {
        this.startWithCreateEvents = startWithCreateEvents;
    }
    
    /**
    * 
    */
    
    private long updatingIdleTime;
    
    public final void setUpdatingTimeout(long updatingTimeout)
    {
        updatingIdleTime = updatingTimeout;
    }
    
    /**
     * LISTENERS
     */
    
    private final List<FileListener> listeners = new ArrayList<FileListener>();
    
    public final void addFileListener(FileListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }
    
    public final void removeFileListener(FileListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }
    
    public final void removeAllDirectoryListeners()
    {
        synchronized (listeners)
        {
            listeners.clear();
        }
    }
    
    /**
     * FIRE EVENTS
     */
    
    private final void fireFileCreatedEvent(File file)
    {
        synchronized (listeners)
        {
            for (FileListener l : listeners)
            {
                l.fileCreated(file);
            }
        }
    }
    
    private final void fireFileUpdatingEvent(File file)
    {
        synchronized (listeners)
        {
            for (FileListener l : listeners)
            {
                l.fileUpdating(file);
            }
        }
    }
    
    private final void fireFileUpdatedEvent(File file)
    {
        synchronized (listeners)
        {
            for (FileListener l : listeners)
            {
                l.fileUpdated(file);
            }
        }
    }
    
    private final void fireFileDeletedEvent(File file, boolean wasDirectory)
    {
        synchronized (listeners)
        {
            for (FileListener l : listeners)
            {
                l.fileDeleted(file, wasDirectory);
            }
        }
    }
    
    MyFile[] previousFiles;
    private boolean firstTime = true;
    
    public boolean check()
    {
        boolean anythingHappened = false;
        
        if (firstTime)
        {
            if (startWithCreateEvents) previousFiles = new MyFile[0];
            else
                previousFiles = wrap(listRec(filter));
            
            firstTime = false;
        }
        
        // get contents of dir
        MyFile[] currentFiles = wrap(listRec(filter));
        
        // compare
        for (MyFile file : currentFiles)
        {
            switch (isNew(file, previousFiles))
            {
                case -1:
                    switch (isUpd(file, previousFiles))
                    {
                        case -1:
                            break;
                        case 0:
                            fireFileUpdatingEvent(file.backing);
                            ignoreUpdate(file, previousFiles);
                            anythingHappened = true;
                            break;
                        case +1:
                            anythingHappened = true;
                            fireFileUpdatedEvent(file.backing);
                            break;
                    }
                    break;
                
                case 0:
                    currentFiles = ignoreCreation(file, currentFiles);
                    break;
                
                case +1:
                    anythingHappened = true;
                    fireFileCreatedEvent(file.backing);
                    break;
            }
        }
        
        // isDel
        for (int i = previousFiles.length - 1; i >= 0; i--)
        {
            MyFile file = previousFiles[i];
            if (!file.backing.exists())
            {
                anythingHappened = true;
                fireFileDeletedEvent(file.backing, file.isDir);
            }
        }
        
        previousFiles = currentFiles;
        
        return anythingHappened;
    }
    
    private final int isNew(MyFile current, MyFile[] prevs)
    {
        if (findPrev(current, prevs) == null)
        {
            long tAgo = System.currentTimeMillis() - current.cachedMod;
            return (tAgo < updatingIdleTime) ? 0 : +1;
        }
        
        return -1;
    }
    
    private final int isUpd(MyFile current, MyFile[] prevs)
    {
        MyFile prev = findPrev(current, prevs);
        if (prev == null)
            throw new IllegalStateException("File not found: " + current.backing.getAbsolutePath());
        
        long tDiff = prev.cachedMod - current.cachedMod;
        if (tDiff == 0)
            return -1;
        long tAgo = System.currentTimeMillis() - current.cachedMod;
        return (tAgo < updatingIdleTime) ? 0 : +1;
    }
    
    private final MyFile findPrev(MyFile current, MyFile[] prevs)
    {
        for (MyFile prev : prevs)
            if (prev.backing.getAbsolutePath().equals(current.backing.getAbsolutePath()))
                return prev;
        
        return null;
    }
    
    private final MyFile[] ignoreCreation(MyFile current, MyFile[] currentFiles)
    {
        MyFile[] newCurrentFiles = new MyFile[currentFiles.length - 1];
        int p = 0;
        
        for (MyFile curr : currentFiles)
            if (!curr.backing.getAbsolutePath().equals(current.backing.getAbsolutePath()))
                newCurrentFiles[p++] = curr;
        
        return newCurrentFiles;
    }
    
    private final void ignoreUpdate(MyFile current, MyFile[] prevs)
    {
        MyFile prev = findPrev(current, prevs);
        if (prev == null)
            throw new IllegalStateException("File not found: " + current.backing.getAbsolutePath());
        
        current.cachedMod = prev.cachedMod;
    }
    
    private class MyFile
    {
        MyFile(File file)
        {
            isDir = file.isDirectory();
            backing = file;
            cachedMod = file.lastModified();
        }
        
        final boolean isDir;
        final File backing;
        long cachedMod;
    }
    
    private final File[] listRec(FileFilter ff)
    {
        List<File> files = new ArrayList<File>();
        
        listRecImpl(dir, ff, files);
        
        return files.toArray(new File[files.size()]);
    }
    
    private final void listRecImpl(File dir, FileFilter ff, List<File> files)
    {
        for (File file : dir.listFiles(ff))
        {
            files.add(file);
            if (file.isDirectory())
                listRecImpl(file, ff, files);
        }
    }
    
    private final MyFile[] wrap(File[] file)
    {
        MyFile[] wrapped = new MyFile[file.length];
        for (int i = 0; i < wrapped.length; i++)
            wrapped[i] = new MyFile(file[i]);
        return wrapped;
    }
}
