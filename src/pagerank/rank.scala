/*
 * Author: Rasmus Heikkila, 2016
 */

package pagerank

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import scala.io.Source._


object Rank {
  
  val help = """
      Usage: pagerank [options] filename
      Options:
      -alpha    the dampening factor. Default 0.85.
      -epsilon    convergence criterion - maximum elementwise
                difference between iterations. Default 0.000001.
      -iters    the number of maximum iterations. Default 1000.
"""
  
  val defaultAlpha = 0.85
  val defaultEpsilon = 0.000001
  val defaultMaxIters = 1000
  val defaultSize = 1000
  
  def main(args: Array[String]) = {
    if (args.length == 0) println(help)
    val arglist = args.toList
    type ArgMap = Map[Symbol, Any]
    
    // Clock this computation
    val start = System.nanoTime()
    
    // Helper function for parsing command line arguments with pattern matching
    def parseArgs(map: ArgMap, list: List[String]): ArgMap = {
      try {
        list match {
          case Nil => map
          case "-alpha" :: value :: tail => parseArgs(map ++ Map('alpha -> value.toDouble), tail)
          case "-epsilon" :: value :: tail => parseArgs(map ++ Map('epsilon -> value.toDouble), tail)
          case "-iters" :: value :: tail => parseArgs(map ++ Map('iters -> value.toInt), tail)
          case "-size" :: value :: tail => parseArgs(map ++ Map('size -> value.toInt), tail)
          case string :: Nil => parseArgs(map ++ Map('file -> string), list.tail)
          case option :: tail => println("Unknown option: " + option)
                                 sys.exit(1)
        }
      }
      catch {
        case e: NumberFormatException => println("Illegal arguments!"); sys.exit(1)
        case ex: Exception => throw ex
      }
    }
    val argmap = parseArgs(Map(), arglist)
    
    if (!argmap.contains('file)) {
      println("No filename given!")
      sys.exit(1)
    }
    
    // Retrieve arguments from the argument map
    // asInstanceOf is probably far from ideal here
    val alpha = argmap.getOrElse('alpha, defaultAlpha).asInstanceOf[Double]
    val epsilon = argmap.getOrElse('epsilon, defaultEpsilon).asInstanceOf[Double]
    val iters = argmap.getOrElse('iters, defaultMaxIters).asInstanceOf[Int]
    val size = argmap.getOrElse('size, defaultSize).asInstanceOf[Int]
    
    try {
      val graph = readFile(argmap('file).toString, size)
      val (pageranks, numIters) = rank(graph, alpha, epsilon, iters)
      println("Number of iterations: " + numIters)
      writeFile(argmap('file).toString, pageranks)
    }
    catch {
      case e: java.io.FileNotFoundException => println("File not found!"); sys.exit(1)
      case ex: Exception => throw ex
    }
    
    val end = System.nanoTime()
    val duration = (end - start) / math.pow(10, 9)
    println("Computation completed in " + duration + " seconds")
    
  }
  
  /* Builds a Graph object from a file specifying the edges of the
   * graph in the following format: "A B" where A and B are index numbers
   * of vertices.
   * @param filename - name of the file containing the edges
   * @param expectedSize - expected number of vertices
   */
  def readFile(filename: String, expectedSize: Int) = {
    val lines = fromFile(filename).getLines()
    val g = new Graph(expectedSize)
    while (lines.hasNext) {
      val line = lines.next().split(' ').map(_.toInt)
      g.addEdge(line(0), line(1))
    }
    g    
  }
  
  def writeFile(filename: String, ranks: Array[Double]) = {
    val target = "ranks.txt"
    val writer = new BufferedWriter( new FileWriter(target) )
    
    for (i <- 0 until ranks.size) {
      val line = i + "=" + ranks(i)
      writer.write(line)
      writer.newLine()
    }
    writer.close()
  }
  
  /* Calculates the pageranks of a graph. 
   * @param graph - a Graph object representing the graph
   * @param alpha - the dampening factor of the Pagerank algorithm
   * @param epsilon - the convergence criterion: maximum allowed elementwise difference between
   *                   iterations
   * @param iters - The maximum number of iterations. Even if algorithm has not converged, calculation
   *                stops after this amount of iterations.
   * @return a Tuple with an Array containing the pageranks of the vertices and number of iterations
   */
  def rank(graph: Graph, alpha: Double, epsilon: Double, iters: Int) = { 
    val n = graph.size
    val pr = Array.fill(n)(1.0 / n)
    val prnew = Array.ofDim[Double](n)
    
    var iter = 1
    var delta = 0.0
    val beta = 1 - alpha
    val sinks = graph.sinkNodes
    
    do {
      delta = 0.0
      
      // Sum the pageranks of sink nodes
      var sinksum = 0.0
      for (s <- sinks) {
        sinksum += pr(s)
      }
      
      var i = 0
      while (i < n) {
        // Distribute sinksum evenly to all nodes, also add random jump probability
        prnew(i) = (beta + alpha * sinksum) / n
        for (v <- graph.inEdges.getOrElse(i, Set.empty)) {
          // Add a fraction of each neighbor node's pagerank to this node's pagerank
          prnew(i) += alpha * pr(v) / graph.outEdgesCount.get(v)
        }
        i += 1
      }
      
      // Find the maximum element difference between iterations 
      var j = 0
      while (j < n) {
        val diff = math.abs(prnew(j) - pr(j))
        if (diff > delta) {
          delta = diff
        }
        j += 1
      }
      
      System.arraycopy(prnew, 0, pr, 0, n)
      iter += 1
    } while (delta > epsilon && iter < iters)
    
    (pr, iter)
    
  }

}