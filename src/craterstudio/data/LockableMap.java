package craterstudio.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockableMap extends HashMap<String, String>
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final AtomicBoolean isLocked = new AtomicBoolean(false);
    
    public void lock()
    {
        isLocked.set(true);
    }
    
    public boolean isLocked()
    {
        return isLocked.get();
    }
    
    private void checkLocked()
    {
        if (isLocked.get())
        {
            throw new IllegalStateException("locked");
        }
    }
    
    @Override
    public void clear()
    {
        checkLocked();
        super.clear();
    }
    
    @Override
    public String put(String key, String value)
    {
        checkLocked();
        return super.put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends String> m)
    {
        checkLocked();
        super.putAll(m);
    }
    
    @Override
    public String remove(Object key)
    {
        checkLocked();
        return super.remove(key);
    }
}