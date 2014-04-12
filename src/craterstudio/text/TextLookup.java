/*
 * Created on 20 mei 2010
 */

package craterstudio.text;

import java.util.HashMap;
import java.util.Map;

public class TextLookup
{
    public static void main(String[] args)
    {
        TextLookup lookup = new TextLookup(8);
        lookup.put("hello world");
        lookup.put("hello");
        lookup.put("world");
        lookup.put("word");
        lookup.get("boo");
    }
    
    private final int maxRangeLength;
    private final int maxDetailedShrink;
    private final Map<String, String> mapper;
    
    public TextLookup(int maxRangeLength)
    {
        this.maxRangeLength = maxRangeLength;
        maxDetailedShrink = 64;
        mapper = new HashMap<String, String>();
    }
    
    public int put(String text)
    {
        if (text.length() > maxDetailedShrink)
        {
            return 0;
        }
        
        if (!putImpl(text))
        {
            return 0;
        }
        
        int count = 0;
        
        // shrink
        for (int i = text.length() - 1; i > 0; i--)
        {
            if (putImpl(text.substring(0, i)))
            {
                count++;
            }
        }
        
        // range
        for (int len = maxRangeLength; len > 0; len--)
        {
            int end = Math.max(0, text.length() - len + 1);
            
            for (int i = 0; i < end; i++)
            {
                if (putImpl(text.substring(i, i + len)))
                {
                    count++;
                }
            }
        }
        return count;
    }
    
    public void compile()
    {
        
    }
    
    public String get(String text)
    {
        String result = mapper.get(text);
        if (result == null)
            result = text;//throw new NoSuchElementException(text);
        return result;
    }
    
    private boolean putImpl(String text)
    {
        if (mapper.containsKey(text))
            return false;
        
        mapper.put(text, text);
        return true;
    }
}
