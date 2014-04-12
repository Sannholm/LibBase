/*
 * Created on 7 mrt 2011
 */

package craterstudio.math;


public class PrimeNumbers
{
    private static int[] HUNDRED_PRIMES = new int[100];
    
    public static int[] hundredPrimes()
    {
        return HUNDRED_PRIMES.clone();
    }
    
    static
    {
        PrimeNumbers obj = new PrimeNumbers();
        for (int i = 0; i < HUNDRED_PRIMES.length; i++)
            HUNDRED_PRIMES[i] = obj.next();
    }
    
    public static int getNthPrime(int n)
    {
        return HUNDRED_PRIMES[n];
    }
    
    private int[] primes;
    private int len;
    
    public PrimeNumbers()
    {
        primes = new int[16];
        len = 0;
    }
    
    public int size()
    {
        return len;
    }
    
    public int get(int i)
    {
        while (size() <= i)
            next();
        return primes[i];
    }
    
    public int next()
    {
        if (len == 0)
        {
            primes[len++] = 2;
            return 2;
        }
        
        int last = primes[len - 1];
        int test = last;
        
        outer: while (true)
        {
            test += 1;
            
            for (int i = 0; i < len; i++)
                if (test % primes[i] == 0)
                    continue outer;
            
            if (primes.length == len)
            {
                //this.primes = Arrays.copyOf(this.primes, this.primes.length * 2);
                
                int[] newArray = new int[primes.length * 2];
                System.arraycopy(primes, 0, newArray, 0, primes.length);
                primes = newArray;
            }
            
            primes[len++] = test;
            return test;
        }
    }
}
