package craterstudio.data;

import java.util.Arrays;

public class IntList
{
    private int[] array;
    private int size;
    
    public IntList()
    {
        array = new int[16];
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
    
    public int removeLast()
    {
        return array[--size];
    }
    
    public void add(int b)
    {
        if (size == array.length)
        {
            array = Arrays.copyOf(array, array.length * 2);
        }
        array[size++] = b;
    }
    
    public int get(int index)
    {
        if (index >= size)
        {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }
    
    public int[] toArray()
    {
        return Arrays.copyOf(array, size);
    }
    
    public void fillArray(int[] dst, int off, int len)
    {
        if (len != size)
        {
            throw new IllegalStateException();
        }
        System.arraycopy(array, 0, dst, off, len);
    }
}
