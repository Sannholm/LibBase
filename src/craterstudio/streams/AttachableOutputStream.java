/*
 * Created on 2 jul 2009
 */

package craterstudio.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class AttachableOutputStream extends OutputStream
{
    public AttachableOutputStream()
    {
        streams = new HashSet<OutputStream>();
    }
    
    //
    
    private final Set<OutputStream> streams;
    
    public void attach(OutputStream out)
    {
        synchronized (streams)
        {
            streams.add(out);
        }
    }
    
    public void detach(OutputStream out)
    {
        synchronized (streams)
        {
            streams.remove(out);
        }
    }
    
    //
    
    @Override
    public void write(int b) throws IOException
    {
        synchronized (streams)
        {
            Set<OutputStream> remove = null;
            for (OutputStream out : streams)
            {
                try
                {
                    out.write(b);
                }
                catch (IOException exc)
                {
                    if (remove == null)
                        remove = new HashSet<OutputStream>();
                    remove.add(out);
                }
            }
            if (remove != null)
                streams.remove(remove);
        }
    }
    
    @Override
    public void write(byte[] buf) throws IOException
    {
        synchronized (streams)
        {
            Set<OutputStream> remove = null;
            for (OutputStream out : streams)
            {
                try
                {
                    out.write(buf);
                }
                catch (IOException exc)
                {
                    if (remove == null)
                        remove = new HashSet<OutputStream>();
                    remove.add(out);
                }
            }
            if (remove != null)
                streams.remove(remove);
        }
    }
    
    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        synchronized (streams)
        {
            Set<OutputStream> remove = null;
            for (OutputStream out : streams)
            {
                try
                {
                    out.write(buf, off, len);
                }
                catch (IOException exc)
                {
                    if (remove == null)
                        remove = new HashSet<OutputStream>();
                    remove.add(out);
                }
            }
            if (remove != null)
                streams.remove(remove);
        }
    }
    
    @Override
    public void flush() throws IOException
    {
        synchronized (streams)
        {
            Set<OutputStream> remove = null;
            for (OutputStream out : streams)
            {
                try
                {
                    out.flush();
                }
                catch (IOException exc)
                {
                    if (remove == null)
                        remove = new HashSet<OutputStream>();
                    remove.add(out);
                }
            }
            if (remove != null)
                streams.remove(remove);
        }
    }
    
    @Override
    public void close() throws IOException
    {
        synchronized (streams)
        {
            Set<OutputStream> remove = null;
            for (OutputStream out : streams)
            {
                try
                {
                    out.close();
                }
                catch (IOException exc)
                {
                    if (remove == null)
                        remove = new HashSet<OutputStream>();
                    remove.add(out);
                }
            }
            if (remove != null)
                streams.remove(remove);
        }
    }
}
