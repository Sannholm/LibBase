/*
 * Created on 21 jan 2008
 */

package craterstudio.util.concur;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import craterstudio.util.HighLevel;

public class TaskProcessor
{
    private static int instanceCounter;
    
    public TaskProcessor()
    {
        this(Runtime.getRuntime().availableProcessors());
    }
    
    public TaskProcessor(int threadCount)
    {
        this("TaskProcessor#" + (instanceCounter++), Thread.NORM_PRIORITY, threadCount);
    }
    
    public TaskProcessor(String name, int threadPriority)
    {
        this(name, threadPriority, Runtime.getRuntime().availableProcessors());
    }
    
    public TaskProcessor(String name, int threadPriority, int threadCount)
    {
        this.name = name;
        this.threadPriority = threadPriority;
        
        queue = new ConcurrentQueue<Runnable>(false);
        
        for (int i = 0; i < threadCount; i++)
            addThread();
    }
    
    private final String name;
    private final int threadPriority;
    final ConcurrentQueue<Runnable> queue;
    final AtomicInteger workerCount = new AtomicInteger();
    final AtomicInteger addedWorkers = new AtomicInteger();
    
    final AtomicInteger addedTaskCount = new AtomicInteger();
    final AtomicInteger startedTaskCount = new AtomicInteger();
    final AtomicInteger finishedTaskCount = new AtomicInteger();
    
    public void addThread()
    {
        workerCount.incrementAndGet();
        
        TaskProcessorHandler tph = new TaskProcessorHandler();
        Thread thread = new Thread(tph);
        thread.setName(name + "[#" + addedWorkers.incrementAndGet() + "]");
        thread.setPriority(threadPriority);
        thread.setDaemon(false);
        thread.start();
    }
    
    public void removeThread()
    {
        put(new ShutdownTask());
    }
    
    //
    
    public int threadCount()
    {
        return workerCount.get();
    }
    
    public int busyCount()
    {
        return startedTaskCount.get() - finishedTaskCount.get();
    }
    
    public int idleCount()
    {
        return threadCount() - busyCount();
    }
    
    public int queueCount()
    {
        return queue.size();
    }
    
    //
    
    public Task putAsTask(Runnable task)
    {
        Task t = new Task(task);
        put(t);
        return t;
    }
    
    public void put(Runnable task)
    {
        addedTaskCount.incrementAndGet();
        queue.produce(task);
    }
    
    //
    
    public void shutdown()
    {
        this.shutdown(false);
    }
    
    public void shutdown(boolean waitFor)
    {
        int awc = workerCount.get() * 10;
        for (int i = 0; i < awc; i++)
        {
            removeThread();
        }
        
        if (waitFor)
        {
            while (workerCount.get() != 0)
            {
                HighLevel.sleep(10);
            }
        }
    }
    
    public void waitFor()
    {
        while (addedTaskCount.get() != finishedTaskCount.get())
        {
            HighLevel.sleep(10);
        }
    }
    
    public void yieldFor()
    {
        while (addedTaskCount.get() != finishedTaskCount.get())
        {
            Thread.yield();
        }
    }
    
    class TaskProcessorHandler implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                Runnable task = queue.consume();
                
                startedTaskCount.incrementAndGet();
                
                try
                {
                    task.run();
                }
                catch (Throwable exc)
                {
                    exc.printStackTrace();
                }
                finally
                {
                    finishedTaskCount.incrementAndGet();
                }
                
                if (task instanceof ShutdownTask)
                {
                    break;
                }
            }
            
            workerCount.decrementAndGet();
        }
    }
    
    public class Task implements Runnable
    {
        final Runnable task;
        final SimpleCountDownLatch started, done;
        Throwable error;
        
        public Task(Runnable task)
        {
            this.task = task;
            started = new SimpleCountDownLatch();
            done = new SimpleCountDownLatch();
        }
        
        @Override
        public void run()
        {
            started.countDown();
            
            try
            {
                task.run();
            }
            catch (Throwable t)
            {
                error = t;
                t.printStackTrace();
            }
            finally
            {
                done.countDown();
            }
        }
        
        public void waitForStart()
        {
            started.await();
        }
        
        public void waitForDone()
        {
            done.await();
        }
        
        public boolean isDone()
        {
            return isDone();
        }
        
        public boolean hasError()
        {
            return error != null;
        }
        
        public Throwable getError()
        {
            if (!hasError())
                throw new NoSuchElementException();
            return error;
        }
    }
    
    class ShutdownTask implements Runnable
    {
        public SimpleCountDownLatch exec = new SimpleCountDownLatch();
        
        @Override
        public void run()
        {
            exec.countDown();
        }
    }
}