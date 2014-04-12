/*
 * Created on 1 jun 2010
 */

package craterstudio.streams;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import craterstudio.data.tuples.Pair;
import craterstudio.io.Streams;

public abstract class TunnelingInputStream
{
    private class InputOutput extends Pair<InputStream, OutputStream>
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        public InputOutput(InputStream in, OutputStream out)
        {
            super(in, out);
        }
    }
    
    private final DataInputStream backing;
    private final Map<Integer, InputOutput> idToStreams;
    
    public TunnelingInputStream(InputStream in)
    {
        backing = new DataInputStream(new BufferedInputStream(in));
        idToStreams = new HashMap<Integer, InputOutput>();
    }
    
    public void close()
    {
        Streams.safeClose(backing);
    }
    
    public void stream() throws IOException
    {
        while (true)
        {
            processImpl();
        }
    }
    
    private void processImpl() throws IOException
    {
        final int id = backing.readInt();
        final int len = backing.readInt();
        final Integer key = Integer.valueOf(id);
        
        switch (len)
        {
            case TunnelingOutputStream.OPEN:
                System.out.println("opening #" + id);
                if (idToStreams.containsKey(key))
                {
                    onInvalidOpenId(id);
                    return;
                }
                
                final ReadbackOutputStream out = new ReadbackOutputStream();
                final InputStream in = out.createInputStream();
                onNewInputStream(id, in);
                idToStreams.put(key, new InputOutput(in, out));
                break;
            
            case TunnelingOutputStream.CLOSE:
                System.out.println("closing #" + id);
                InputOutput io1 = idToStreams.remove(key);
                if (io1 == null) onInvalidCloseId(id);
                else
                    io1.second().close(); // inputstream will close once EOF is reached
                break;
            
            default:
                InputOutput io2 = idToStreams.get(key);
                if (io2 == null)
                {
                    onInvalidUseId(id);
                    return;
                }
                byte[] payload = new byte[len];
                backing.readFully(payload);
                io2.second().write(payload);
                io2.second().flush();
                break;
        }
    }
    
    protected abstract void onNewInputStream(int id, InputStream input);
    
    protected void onInvalidOpenId(int id)
    {
        throw new IllegalStateException("cannot open id: " + id);
    }
    
    protected void onInvalidUseId(int id)
    {
        throw new IllegalStateException("cannot use id: " + id);
    }
    
    protected void onInvalidCloseId(int id)
    {
        throw new IllegalStateException("cannot close id: " + id);
    }
}