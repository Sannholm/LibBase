package craterstudio.data;

import java.util.Arrays;

public class ByteList
{
    private byte[] array;
    private int size;
    
    public ByteList()
    {
        array = new byte[16];
    }
    
    public void clear()
    {
        size = 0;
    }
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public int size()
    {
        return size;
    }
    
    public byte removeLast()
    {
        return array[--size];
    }
    
    public void add(byte b)
    {
        if (size == array.length)
        {
            array = Arrays.copyOf(array, array.length * 2);
        }
        array[size++] = b;
    }
    
    public byte get(int index)
    {
        if (index >= size)
        {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }
    
    public byte[] toArray()
    {
        return Arrays.copyOf(array, size);
    }
    
    public void fillArray(byte[] dst, int off, int len)
    {
        if (len != size)
        {
            throw new IllegalStateException();
        }
        System.arraycopy(array, 0, dst, off, len);
    }
}
