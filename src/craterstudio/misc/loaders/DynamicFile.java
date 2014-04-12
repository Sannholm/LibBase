/*
 * Created on 9 apr 2009
 */

package craterstudio.misc.loaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import craterstudio.io.FileUtil;

public class DynamicFile
{
    private final File file;
    private long lastMod;
    private byte[] cached;
    
    public DynamicFile(File file) throws FileNotFoundException
    {
        if (!file.exists())
        {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        
        this.file = file;
        lastMod = -1L;
        cached = null;
    }
    
    public File getFile()
    {
        return file;
    }
    
    public boolean exists()
    {
        return file.exists();
    }
    
    public boolean isOutOfDate()
    {
        long curr = file.lastModified();
        long prev = lastMod;
        
        return curr != prev;
    }
    
    public long timeSinceLastMod()
    {
        return System.currentTimeMillis() - file.lastModified();
    }
    
    public void sync()
    {
        lastMod = file.lastModified();
    }
    
    public InputStream newInputStream() throws FileNotFoundException
    {
        if (cached == null || isOutOfDate())
        {
            if (!file.exists())
            {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
            
            cached = FileUtil.readFile(file);
            lastMod = file.lastModified();
        }
        
        return new ByteArrayInputStream(cached);
    }
}
