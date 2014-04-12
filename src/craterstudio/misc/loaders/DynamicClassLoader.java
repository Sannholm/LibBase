package craterstudio.misc.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public abstract class DynamicClassLoader extends ClassLoader
{
    private ClassLoader currentLoader;
    
    public DynamicClassLoader()
    {
        super();
        currentLoader = null;
    }
    
    public DynamicClassLoader(ClassLoader parent)
    {
        super(parent);
        currentLoader = null;
    }
    
    //
    
    public abstract boolean isUpdated();
    
    public abstract ClassLoader createClassLoader();
    
    //
    
    @Override
    public URL getResource(String name)
    {
        ensureLatestClassLoader();
        
        URL url = getParent().getResource(name);
        if (url != null)
            return url;
        
        return currentLoader.getResource(name);
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException
    {
        ensureLatestClassLoader();
        
        Enumeration<URL> urls = getParent().getResources(name);
        if (urls != null)
            return urls;
        
        return currentLoader.getResources(name);
    }
    
    @Override
    public InputStream getResourceAsStream(String name)
    {
        ensureLatestClassLoader();
        
        InputStream in = getParent().getResourceAsStream(name);
        if (in != null)
            return in;
        
        return currentLoader.getResourceAsStream(name);
    }
    
    @Override
    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException
    {
        ensureLatestClassLoader();
        
        return currentLoader.loadClass(name);
    }
    
    //
    
    private long lastChecked;
    private long minCheckInterval = 0;
    
    public void setMinimalCheckInterval(long interval)
    {
        minCheckInterval = interval;
    }
    
    private final boolean checkForUpdate()
    {
        long now = System.currentTimeMillis();
        long elapsed = now - lastChecked;
        
        if (elapsed < minCheckInterval)
        {
            // if we checked less than N ms ago,
            // just assume the loader is not updated.
            // otherwise we put a major strain on
            // the file system (?) for no real gain
            return false;
        }
        
        lastChecked = now;
        
        return isUpdated();
    }
    
    //
    
    public void ensureLatestClassLoader()
    {
        if (checkForUpdate())
        {
            replaceClassLoader();
        }
    }
    
    protected void replaceClassLoader()
    {
        currentLoader = createClassLoader();
        
        // protected, so do stuff, if you wish
    }
}