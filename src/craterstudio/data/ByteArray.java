/*
 * Created on 14 nov 2008
 */

package craterstudio.data;

import java.util.Arrays;

public class ByteArray
{
    private final byte[] arr;
    private final int off, len;
    
    public ByteArray(int len)
    {
        this(new byte[len]);
    }
    
    public ByteArray(byte[] arr)
    {
        this(arr, 0, arr.length);
    }
    
    public ByteArray(byte[] arr, int off, int len)
    {
        if (arr == null || ((off | len) < 0) || (off + len > arr.length))
            throw new IllegalArgumentException();
        
        this.arr = arr;
        this.off = off;
        this.len = len;
    }
    
    //
    
    @Override
    public int hashCode()
    {
        int hash = 0;
        for (int i = 0; i < len; i++)
        {
            hash ^= (arr[off + i]);
            hash *= 13;
        }
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (!(obj instanceof ByteArray))
            return false;
        
        ByteArray that = (ByteArray)obj;
        
        byte[] a = Arrays.copyOfRange(arr, off, off + len);
        byte[] b = Arrays.copyOfRange(that.arr, that.off, that.off + that.len);
        return Arrays.equals(a, b);
    }
    
    //
    
    public int length()
    {
        return len;
    }
    
    public ByteArray subarray(int off)
    {
        return this.subarray(off, len);
    }
    
    public ByteArray subarray(int from, int to)
    {
        return new ByteArray(arr, off + off, to - from);
    }
    
    public ByteArray copy()
    {
        byte[] copy = new byte[len];
        System.arraycopy(arr, off, copy, 0, copy.length);
        return new ByteArray(copy);
    }
    
    //
    
    public void put(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] = value;
    }
    
    public void putRange(int index, byte[] src)
    {
        this.putRange(index, src, 0, src.length);
    }
    
    public void putRange(int index, byte[] src, int off, int len)
    {
        checkRange(index, len);
        System.arraycopy(src, off, arr, this.off + index, len);
    }
    
    public void putRange(int index, ByteArray src)
    {
        this.putRange(index, src, 0, src.len);
    }
    
    public void putRange(int index, ByteArray src, int off, int len)
    {
        checkRange(index, src.len);
        System.arraycopy(src.arr, src.off + off, arr, this.off + index, len);
    }
    
    //
    
    public byte get(int index)
    {
        checkIndex(index);
        return arr[off + index];
    }
    
    public void getRange(int index, byte[] dst)
    {
        this.getRange(index, dst, 0, dst.length);
    }
    
    public void getRange(int index, byte[] dst, int off, int len)
    {
        checkRange(index, len);
        System.arraycopy(arr, this.off + index, dst, off, len);
    }
    
    public void getRange(int index, ByteArray dst)
    {
        this.getRange(index, dst, 0, dst.len);
    }
    
    public void getRange(int index, ByteArray dst, int off, int len)
    {
        checkRange(index, dst.len);
        System.arraycopy(arr, this.off + index, dst.arr, dst.off + off, len);
    }
    
    //
    
    public void add(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] += value;
    }
    
    public void sub(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] -= value;
    }
    
    public void mul(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] *= value;
    }
    
    public void div(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] /= value;
    }
    
    public void mod(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] %= value;
    }
    
    public void xor(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] ^= value;
    }
    
    public void and(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] &= value;
    }
    
    public void or(int index, byte value)
    {
        checkIndex(index);
        arr[off + index] |= value;
    }
    
    //
    
    private final void checkIndex(int index)
    {
        if (index < 0 || index >= len)
            throw new IndexOutOfBoundsException();
    }
    
    private final void checkRange(int index, int len)
    {
        if (index < 0 || index + len >= this.len)
            throw new IndexOutOfBoundsException();
    }
}
