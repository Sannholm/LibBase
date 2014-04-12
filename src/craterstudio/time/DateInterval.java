/*
 * Created on 3 jan 2011
 */

package craterstudio.time;

import java.util.ArrayList;
import java.util.List;

import craterstudio.text.TextDate;
import craterstudio.util.ListUtil;

public class DateInterval
{
    public String begin_date;
    public String end_date;
    
    public int max_matches;
    public int interval_amount;
    public DateIntervalType interval_type;
    
    public void setup(String begin, int limit)
    {
        begin_date = begin;
        end_date = null;
        max_matches = limit;
    }
    
    public void setup(String begin, String end)
    {
        begin_date = begin;
        end_date = end;
        max_matches = 0;
    }
    
    public void interval(int interval, DateIntervalType type)
    {
        interval_amount = interval;
        interval_type = type;
    }
    
    //
    
    public void verifyState()
    {
        if (interval_amount <= 0 || interval_type == null)
        {
            throw new IllegalStateException("incorrect interval: " + interval_amount + " " + interval_type);
        }
        
        if (end_date == null)
        {
            if (max_matches <= 0)
                throw new IllegalStateException("incorrect end condition: " + max_matches + " matches");
            TextDate.check(begin_date);
        }
        else
        {
            if (max_matches != 0)
                throw new IllegalStateException("incorrect end condition: " + max_matches + " matches");
            if (!TextDate.lessThanOrEquals(begin_date, end_date))
                throw new IllegalStateException("begin > end");
        }
    }
    
    public String[] getMatches()
    {
        YearMonthDate begin = new YearMonthDate(begin_date);
        
        YearMonthDate end = null;
        if (end_date != null)
        {
            end = new YearMonthDate(end_date);
            if (DateMath.compare(begin, end) > 0)
                throw new IllegalStateException();
        }
        
        List<String> matches = new ArrayList<String>();
        
        for (int traverse = 0; true; traverse += interval_amount)
        {
            YearMonthDate result = new YearMonthDate();
            
            switch (interval_type)
            {
                case DAY:
                    DateMath.traverseDays(begin, traverse, result);
                    break;
                
                case MONTH:
                    DateMath.traverseMonths(begin, traverse, result);
                    break;
                
                case YEAR:
                    DateMath.traverseYears(begin, traverse, result);
                    break;
                
                default:
                    throw new IllegalStateException();
            }
            
            // too late
            if (end_date != null && DateMath.compare(result, end) > 0)
                break;
            
            matches.add(result.toString());
            
            // enough matches
            if (end_date == null && matches.size() == max_matches)
                break;
        }
        
        return ListUtil.toArray(String.class, matches);
    }
}
