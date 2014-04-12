package craterstudio.misc.loaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public abstract class BytesClassLoader extends ClassLoader
{
    private final boolean overrideParent;
    
    public BytesClassLoader()
    {
        super();
        overrideParent = false;
    }
    
    public BytesClassLoader(ClassLoader parent)
    {
        this(parent, false);
    }
    
    public BytesClassLoader(boolean overrideParent)
    {
        super();
        this.overrideParent = overrideParent;
    }
    
    public BytesClassLoader(ClassLoader parent, boolean overrideParent)
    {
        super(parent);
        this.overrideParent = overrideParent;
    }
    
    protected abstract byte[] readBytes(String classname, String path);
    
    @Override
    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (!overrideParent)
        {
            try
            {
                return getParent().loadClass(name);
            }
            catch (Throwable t)
            {
                // ignore
            }
        }
        
        Class<?> found = findLoadedClass(name);
        if (found != null)
        {
            return found;
        }
        
        String path = name.replace('.', '/').concat(".class");
        
        byte[] raw = readBytes(name, path);
        
        if (raw == null)
        {
            return getParent().loadClass(name);
        }
        
        return super.defineClass(name, raw, 0, raw.length);
    }
    
    @Override
    public InputStream getResourceAsStream(String path)
    {
        byte[] raw = readBytes(null, path);
        if (raw == null)
            return null;
        return new ByteArrayInputStream(raw);
    }
    
    @Override
    public URL getResource(String name)
    {
        // who uses this anyway?
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException
    {
        // who uses this anyway?
        throw new UnsupportedOperationException();
    }
}