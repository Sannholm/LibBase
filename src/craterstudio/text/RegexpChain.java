/*
 * Created on 12 feb 2010
 */

package craterstudio.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import craterstudio.data.LRUMap;
import craterstudio.data.tuples.Trio;

public class RegexpChain
{
    private final List<Trio<Pattern, String, Boolean>> patternAndRewrite;
    private final LRUMap<String, String> cache;
    
    public RegexpChain(int poolSize)
    {
        patternAndRewrite = new ArrayList<Trio<Pattern, String, Boolean>>();
        
        cache = new LRUMap<String, String>(poolSize);
    }
    
    public void clear()
    {
        patternAndRewrite.clear();
        
        cache.clear();
    }
    
    public void addPattern(String regexp, String replacement, boolean stop)
    {
        Pattern pattern = Pattern.compile(regexp);
        
        Trio<Pattern, String, Boolean> data;
        data = new Trio<Pattern, String, Boolean>(pattern, replacement, Boolean.valueOf(stop));
        
        patternAndRewrite.add(data);
        
        cache.clear();
    }
    
    public String rewrite(String value)
    {
        if (cache.contains(value))
            return cache.get(value);
        
        String input = value;
        for (Trio<Pattern, String, Boolean> trio : patternAndRewrite)
        {
            Pattern pattern = trio.first();
            String replacement = trio.second();
            if (!pattern.matcher(value).matches())
                continue;
            
            value = pattern.matcher(value).replaceAll(replacement);
            if (trio.third().booleanValue()) // stop
            break;
        }
        
        cache.put(input, value);
        return value;
    }
}
