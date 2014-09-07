Hermosa
=======

High performance thread unsafe counting bloom filter implementation

A counting bloom filter is a bloom filter variant that holds a count of number of times
a bloom filter bit was set. Such a structure enables one to answer with a high probability
how many times a given element has been inserted into the bloom filter(accounting for a
low false positve rate). The max count is limited to 127, while minimum is 0. This bloomfilter
is not thread safe for performance reasons, take care when using it.

one can expect a throughput of 3 million insertions/membership checks for second. it is 8 times
less space efficient than a traditional bloom filter because it uses a byte array instead of a
bit array to maintain count.