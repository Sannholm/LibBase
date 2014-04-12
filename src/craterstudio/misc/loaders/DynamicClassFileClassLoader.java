package craterstudio.misc.loaders;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DynamicClassFileClassLoader extends DynamicClassLoader
{
    final String className;
    private final File classFile;
    private long lastModified;
    
    public DynamicClassFileClassLoader(ClassLoader parent, File dir, String className)
    {
        super(parent);
        
        this.className = className;
        classFile = new File(dir, className.replace('.', '/') + ".class");
        lastModified = -1L;
    }
    
    @Override
    public boolean isUpdated()
    {
        long classFileLastModified = classFile.lastModified();
        boolean isUpdated = classFileLastModified != lastModified;
        lastModified = classFileLastModified;
        return isUpdated;
    }
    
    @Override
    public ClassLoader createClassLoader()
    {
        final byte[] bytecode;
        
        try
        {
            bytecode = loadClassFile();
        }
        catch (IOException exc)
        {
            throw new IllegalStateException("failed to load classFile: " + classFile.getAbsolutePath(), exc);
        }
        
        ClassLoader loader = new BytesClassLoader(getParent())
        {
            @Override
            public byte[] readBytes(String classname, String path)
            {
                if (path.replace('/', '.').equals(className.concat(".class")))
                    return bytecode;
                return null;
            }
        };
        
        return loader;
    }
    
    private final byte[] loadClassFile() throws IOException
    {
        byte[] buf = null;
        
        buf = new byte[(int)classFile.length()];
        InputStream in = new FileInputStream(classFile);
        int off = 0;
        while (off != buf.length)
        {
            int justRead = in.read(buf, off, buf.length - off);
            if (justRead == -1)
                throw new EOFException("could not fully read class file: " + classFile.getAbsolutePath());
            off += justRead;
        }
        in.close();
        
        return buf;
    }
}