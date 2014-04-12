/*
 * Created on 15 jun 2010
 */

package craterstudio.func;

public class NullFilter<T> implements Filter<T>
{
    @Override
    public boolean accept(T value)
    {
        return (value != null);
    }
}