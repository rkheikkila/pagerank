# pagerank

Implementation of the Google's PageRank algorithm written in Scala. 

This is a power iteration algorithm based on [an article by David Austin](http://www.ams.org/samplings/feature-column/fcarc-pagerank). 
It is able to compute PageRanks for directed graphs with millions of vertices. 

# Usage

This project can be used as an command line utility with the following options supported:

- `-alpha`: the dampening factor of the algorithm. Default value is 0.85.
- `-epsilon`: Convergence criterion. When the maximum change of any node's PageRank between two iterations is less than this value, the computation is finished. Default value 0.000001.
- `-iters`: Maximum number of iterations. Computation will stop when this limit is reached, even if the PageRank isn't considered converged yet.
- `-size`: Number of vertices. Used to allocate suitably large hash tables and to avoid hash table rehashing.

The methods can also be used as an API. The `rank` method takes four arguments and returns a tuple containing the PageRank values as an Array and the number of iterations:

```
val (pr, iterations) = rank(graph, alpha, epsilon, iters)
```

where `graph` is a Graph object and the other arguments are explained above.

# Dependencies

[fastutil](https://github.com/vigna/fastutil): the type-specific implementations of hashsets and hashmaps are used because of their superb performance compared to the standard `scala.collection.mutable` library.



