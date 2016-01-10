/*
 * Author: Rasmus Heikkila, 2016
 */

package pagerank

import scala.collection.mutable.Set
import scala.collection.mutable.Map

import it.unimi.dsi.fastutil.ints._

/*
 * Simple representation of a directed graph with vertices
 * indiced by integers starting from 0. More memory
 * efficient that the generic adjacency matrix implementation.
 * @param manualSize: If a sink node is the largest-indexed node, manually 
 * setting the graph size is necessary. 
 * @param expectedSize - expected number of vertices in the graph
 */
class Graph(private val expectedSize: Int = 1000, private val manualSize: Int = 0) {
  val inEdges = Map[Int, Set[Int]]()
  val outEdgesCount = new Int2IntOpenHashMap(expectedSize)
  outEdgesCount.defaultReturnValue(0)
  val nodes = new IntOpenHashSet(expectedSize)
  var _size = 0
  
  def addEdge(from: Int, to: Int) = {
    if (inEdges.contains(to)) {
      inEdges(to) += from
    } else {
      inEdges(to) = Set(from)
    }
    
    outEdgesCount.addTo(from, 1)
    nodes.add(from)
    nodes.add(to)
    
    val max = math.max(from, to)
    if (max > _size) {
      _size = max
    }
  }
  
  // Since the graph is zero-indexed, size is 
  def size = if (manualSize > _size) manualSize else (_size + 1)
  
  // Returns vertices with indegree 0, i.e. pages that no pages link to.
  def sinkNodes = {
    (0 until this.size).filter(v => !nodes.contains(v) || outEdgesCount.get(v) == 0)
  }
  
}