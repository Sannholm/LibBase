/*
 * Created on 23 jun 2011
 */

package craterstudio.func;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectStreamIterable
{
    static final Object UNDEFINED = new Object();
    
    public static <T> Stream<T> stream2(final Iterable<T> iterable)
    {
        return stream2(iterable.iterator());
    }
    
    public static <T> Stream<T> stream2(final Iterator<T> iterator)
    {
        return new Stream<T>()
        {
            private boolean eof;
            
            @Override
            public T poll()
            {
                if (eof || !iterator.hasNext())
                {
                    eof = true;
                    return null;
                }
                return iterator.next();
            }
            
            @Override
            public boolean reachedEnd()
            {
                return eof;
            }
        };
    }
    
    public static <T> ObjectStream<T> stream(final Iterable<T> iterable)
    {
        return stream(iterable.iterator());
    }
    
    public static <T> ObjectStream<T> stream(final Iterator<T> iterator)
    {
        return new ObjectStream<T>()
        {
            @Override
            public T next()
            {
                if (iterator.hasNext())
                {
                    return iterator.next();
                }
                return (T)ObjectStream.END_OF_STREAM;
            }
            
            @Override
            public void remove()
            {
                iterator.remove();
            }
        };
    }
    
    public static <T> Iterable<T> iterable(final ObjectStream<T> stream)
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return ObjectStreamIterable.iterator(stream);
            }
        };
    }
    
    public static <T> Iterator<T> iterator(final ObjectStream<T> stream)
    {
        return new Iterator<T>()
        {
            private T current = (T)UNDEFINED;
            private T next = (T)UNDEFINED;
            
            @Override
            public boolean hasNext()
            {
                if (next == UNDEFINED)
                {
                    next = stream.next();
                }
                return next != ObjectStream.END_OF_STREAM;
            }
            
            @Override
            public T next()
            {
                if (next == UNDEFINED)
                {
                    next = stream.next();
                }
                if (next == ObjectStream.END_OF_STREAM)
                {
                    throw new NoSuchElementException();
                }
                current = next;
                next = (T)UNDEFINED;
                return current;
            }
            
            @Override
            public void remove()
            {
                if (current == UNDEFINED)
                {
                    throw new IllegalStateException();
                }
                stream.remove();
                current = (T)UNDEFINED;
            }
        };
    }
}
