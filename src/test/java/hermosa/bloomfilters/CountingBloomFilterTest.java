package hermosa.bloomfilters;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CountingBloomFilterTest {

    CountingBloomFilter<String> countingBloomFilter = new CountingBloomFilter<String>(1000, 0.01);

    @Test
    public void testPut() throws Exception {
        assertTrue(countingBloomFilter.put("putting") == 0);
        assertTrue(countingBloomFilter.put("putting") == 1);
    }

    @Test
    public void testLikelyToConain() throws Exception {
        countingBloomFilter.put("dude");
        assertTrue(countingBloomFilter.likelyToConain("dude") == 1);
        assertTrue(countingBloomFilter.likelyToConain("dudes")==0);

        countingBloomFilter.put("dude");
        assertTrue(countingBloomFilter.likelyToConain("dude") == 2);
    }

    @Test
    public void testPutPerformance(){
        int warmupIterations = 10000;
        int perfIterations = 10000000;

        for (int warmupIter=0; warmupIter<warmupIterations; warmupIter++)
            countingBloomFilter.put("some shit");

        long begin = System.nanoTime();
            for (int perfIter=0;perfIter<perfIterations; perfIter++)
                countingBloomFilter.put("something else");
        long end = System.nanoTime();
        long insertionsPerSec = (perfIterations)/((end-begin)/(1000*1000*1000));
        System.out.println("Througput of "+ insertionsPerSec + " puts per second");
    }

    @Test
    public void testCheckPerformance(){
        int warmupIterations = 10000;
        int perfIterations = 10000000;

        countingBloomFilter.put("some shit");
        countingBloomFilter.put("something else");

        for (int warmupIter=0; warmupIter<warmupIterations; warmupIter++)
            countingBloomFilter.likelyToConain("some shit");

        long begin = System.nanoTime();
        for (int perfIter=0;perfIter<perfIterations; perfIter++)
            countingBloomFilter.likelyToConain("something else");
        long end = System.nanoTime();
        long nonEmptyChecksPerSec = (perfIterations)/((end-begin)/(1000*1000*1000));
        System.out.println("Througput of "+ nonEmptyChecksPerSec + " non empty checks per second");

        begin = System.nanoTime();
        for (int perfIter=0;perfIter<perfIterations; perfIter++)
            countingBloomFilter.likelyToConain("something that hasn't been inserted");
        end = System.nanoTime();
        long emptyChecksPerSec = (perfIterations)/((end-begin)/(1000*1000*1000));
        System.out.println("Througput of "+ emptyChecksPerSec + " empty checks per second");
    }
}
