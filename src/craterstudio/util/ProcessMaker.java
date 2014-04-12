/*
 * Created on 18 nov 2008
 */

package craterstudio.util;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import craterstudio.io.Streams;
import craterstudio.util.concur.Future;

public class ProcessMaker
{
    public static final int launchDefaultApplication(String argument)
    {
        try
        {
            String exec = "cmd /c \"start " + argument + "\"";
            Process p = Runtime.getRuntime().exec(exec);
            p.waitFor();
            return p.exitValue();
        }
        catch (Exception exc)
        {
            throw new IllegalStateException(exc);
        }
    }
    
    private final List<String> cmds;
    private File workdir;
    
    public ProcessMaker()
    {
        cmds = new ArrayList<String>();
        workdir = null;
    }
    
    public void setWorkingDirectory(File workdir)
    {
        this.workdir = workdir;
    }
    
    public void clearCommands()
    {
        cmds.clear();
    }
    
    public void addCommands(String... cmds)
    {
        for (String cmd : cmds)
            this.cmds.add(cmd);
    }
    
    public Future<Integer> start()
    {
        return this.start(System.out, System.err, false);
    }
    
    public Future<Integer> start(OutputStream out, OutputStream err, boolean closeStreamsOnExit)
    {
        final Future<Integer> future = new Future<Integer>();
        
        String[] cmds = this.cmds.toArray(new String[this.cmds.size()]);
        
        final Process p;
        
        try
        {
            p = Runtime.getRuntime().exec(cmds, null, workdir);
        }
        catch (Exception exc)
        {
            throw new IllegalStateException("cmds=" + Arrays.toString(cmds), exc);
        }
        
        Streams.asynchronousTransfer(p.getInputStream(), out, true, closeStreamsOnExit);
        Streams.asynchronousTransfer(p.getErrorStream(), err, true, closeStreamsOnExit);
        
        new Thread()
        {
            @Override
            public void run()
            {
                int exitValue = -1;
                boolean exited = false;
                
                do
                {
                    try
                    {
                        p.waitFor();
                    }
                    catch (InterruptedException exc)
                    {
                        // ignore
                    }
                    
                    try
                    {
                        exitValue = p.exitValue();
                        exited = true;
                    }
                    catch (IllegalThreadStateException exc)
                    {
                        // ignore
                    }
                } while (!exited);
                
                future.set(Integer.valueOf(exitValue));
            }
        }.start();
        
        return future;
    }
}
