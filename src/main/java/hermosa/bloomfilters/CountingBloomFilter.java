package hermosa.bloomfilters;


import sun.misc.Hashing;

import java.util.Random;

import static java.lang.Math.*;

/**
 * A counting bloom filter is a bloom filter variant that holds a count of number of times
 * a bool filter bit was set. Such a structure enables one to answer with a high probability
 * how many times a given element has been inserted into the bloom filter(accounting for a
 * low false positve rate). The max count is limited to 127, while minimum is 0. This bloomfilter
 * is not thread safe for performance reasons, take care when using it.
 * @param <T>
 */
public class CountingBloomFilter<T>{

    private final byte[] bloomArray;
    private final int bloomFilterSize;
    private final int numOfHashFuncs;
    private final byte[] hashFunctions;

    /**
     * Given the number of possible elements to be stored in the bloom filter
     * the bloom filter is initialized with an optimally sized bloom array and
     * an optimal number of hash functions for the given false positive rate.
     * @param numberOfElements
     * @param falsePositiveRate
     */
    public CountingBloomFilter(int numberOfElements, double falsePositiveRate){
        bloomFilterSize = numOfCellsRequired(numberOfElements, falsePositiveRate);
        numOfHashFuncs = optimalHashFuncs(numberOfElements);
        bloomArray = new byte[bloomFilterSize];
        hashFunctions = new byte[numOfHashFuncs];
        initHashFuncs(hashFunctions);
    }

    /**
     * returns with high probability the minimum number of times the object has been inserted
     * into the bloom filter. The max number that can be returned is 127, the minimum being 0
     * @param obj
     * @return minimum number of times the object might have been inserted into the bloom filter
     */
    public int put(T obj) {
        int[] cells = getCells(obj);
        return setCells(cells);
    }

    /**
     * returns with high probability the minimum number of times the object has been inserted
     * into the bloom filter. The max number that can be returned is 127, the minimum being 0
     * @param obj
     * @return minimum number of times the object might have been inserted into the bloom filter
     */
    public int likelyToConain(T obj) {
        int[] cells = getCells(obj);
        return minimumInserstionsIn(cells);
    }

    private int minimumInserstionsIn(int[] cells) {
        int minCellVal = Integer.MAX_VALUE;
        for (int cellNo=0; cellNo < cells.length; cellNo++){
            int bloomCell = cells[cellNo];
            if (bloomArray[bloomCell] < minCellVal)
                minCellVal = bloomArray[bloomCell];
        }
        return minCellVal;
    }

    private int setCells(int[] cells) {
        int minCellVal = Integer.MAX_VALUE;
        for (int cellNo=0; cellNo < cells.length; cellNo++){
            int bloomCell = cells[cellNo];
            if (bloomArray[bloomCell] < minCellVal)
                minCellVal = bloomArray[bloomCell];

            if (bloomArray[bloomCell] < 127) {
                bloomArray[bloomCell]++;
            }

        }
        return minCellVal;
    }

    private int[] getCells(T obj) {
        int[] cells = new int[numOfHashFuncs];
        for (int cellNo=0; cellNo < numOfHashFuncs; cellNo++)
            cells[cellNo] = getCell(obj,hashFunctions[cellNo]);
        return cells;
    }

    private int getCell(T obj, byte hashFunction) {
        byte[] objHash = intToByteArray(obj.hashCode());
        byte[] bytesToHash = new byte[objHash.length+1];
        System.arraycopy(objHash, 0 , bytesToHash, 0, objHash.length);
        bytesToHash[objHash.length] = hashFunction;
        int hashIndex = abs(Hashing.murmur3_32(bytesToHash));
        return hashIndex%bloomFilterSize;
    }

    private byte[] intToByteArray(int val) {
        return new byte[]{
                (byte) (val>>24),
                (byte) (val>>16),
                (byte) (val>>8),
                (byte) val
        };
    }

    private int optimalHashFuncs(int numberOfElements) {
        return new Double(ceil((bloomFilterSize/numberOfElements)*0.7)).intValue();
    }

    private int numOfCellsRequired(int numberOfElements, double falsePositiveRate) {
        return new Double(ceil((-numberOfElements) * log(falsePositiveRate) / (pow(log(2), 2)))).intValue();
    }

    private void initHashFuncs(byte[] hashFunctions) {
        new Random().nextBytes(hashFunctions);
    }
}
