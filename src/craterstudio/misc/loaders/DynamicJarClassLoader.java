package craterstudio.misc.loaders;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import craterstudio.io.Logger;

public class DynamicJarClassLoader extends DynamicClassLoader
{
    private final File jar;
    private long prevLastModified;
    private final Set<String> resourceNames;
    
    public DynamicJarClassLoader(ClassLoader parent, File jar)
    {
        super(parent);
        
        this.jar = jar;
        prevLastModified = -1L;
        resourceNames = new HashSet<String>();
        
        ensureLatestClassLoader();
    }
    
    public File getJar()
    {
        return jar;
    }
    
    public Set<String> getResourceNames()
    {
        return Collections.unmodifiableSet(resourceNames);
    }
    
    private static final long file_idle_timeout = 3 * 1000;
    
    @Override
    public boolean isUpdated()
    {
        long jarLastModified = jar.lastModified();
        
        boolean willBeUpdated = jarLastModified != prevLastModified;
        
        if (willBeUpdated && prevLastModified != -1L)
        {
            if (jar.lastModified() > System.currentTimeMillis() - file_idle_timeout)
            {
                Logger.notification("Pending new JAR file: %s", jar.getAbsolutePath());
                willBeUpdated = false;
            }
        }
        
        if (willBeUpdated)
        {
            Logger.notification("Loading new JAR file: %s", jar.getAbsolutePath());
            prevLastModified = jarLastModified;
        }
        
        return willBeUpdated;
    }
    
    @Override
    public ClassLoader createClassLoader()
    {
        final Map<String, byte[]> resources;
        
        resourceNames.clear();
        
        try
        {
            resources = loadCompleteJarFile();
        }
        catch (IOException exc)
        {
            throw new IllegalStateException("Failed to load JAR file: " + jar.getAbsolutePath(), exc);
        }
        
        resourceNames.addAll(resources.keySet());
        
        ClassLoader loader = new BytesClassLoader(getParent())
        {
            @Override
            public byte[] readBytes(String classname, String name)
            {
                return resources.get(name);
            }
        };
        
        return loader;
    }
    
    private final Map<String, byte[]> loadCompleteJarFile() throws IOException
    {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        
        JarFile jf = new JarFile(jar);
        Enumeration<JarEntry> entries = jf.entries();
        while (entries.hasMoreElements())
        {
            byte[] buf = null;
            
            JarEntry entry = entries.nextElement();
            
            if (!entry.isDirectory())
            {
                buf = new byte[(int)entry.getSize()];
                InputStream in = jf.getInputStream(entry);
                int off = 0;
                while (off != buf.length)
                {
                    int justRead = in.read(buf, off, buf.length - off);
                    if (justRead == -1)
                        throw new EOFException("Could not fully read JAR file entry: " + entry.getName());
                    off += justRead;
                }
                in.close();
            }
            
            map.put(entry.getName(), buf);
        }
        
        jf.close();
        
        return map;
    }
}