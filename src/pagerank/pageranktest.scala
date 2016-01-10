package pagerank

import java.io.{ByteArrayOutputStream, PrintStream}
import org.junit.{After, Before, Test}
import org.junit.Assert._
import scala.io.Source._


class PagerankTest {
  
  def readPR(filename: String, size: Int) = {
    val res = Array.ofDim[Double](size)
    val lines = fromFile(filename).getLines()
    
    while (lines.hasNext) {
      val line = lines.next.split("=").map(_.trim)
      val idx = line(0).toInt
      val pr = line(1).toDouble
      res(idx) = pr
    }
    res
  }
  
  @Test def noEdgesTest() {
    val g = new Graph(4, 4)
    val ranks = Array.fill(4)(1.0 / 4)
    val (result, iter) = Rank.rank(g, 0.85, 0.001, 1000)
    assertArrayEquals(ranks, result, 0.001)
  }
  
  @Test def smallTest1() {
    val g = new Graph(4)
    g.addEdge(0,1)
    g.addEdge(1,2)
    g.addEdge(2,0)
    g.addEdge(2,1)
    val ranks = Array(0.2314, 0.3933, 0.3753)
    val (result, iter) = Rank.rank(g, 0.7, 0.001, 1000)
    assertArrayEquals(ranks, result, 0.001)
  }
  
  @Test def smallTest2() {
    val g = new Graph(4)
    g.addEdge(0,1)
    g.addEdge(1,0)
    g.addEdge(1,2)
    g.addEdge(2,1)
    val ranks = Array(0.2647, 0.4706, 0.2647)
    val (result, iter) = Rank.rank(g, 0.7, 0.001, 1000)
    assertArrayEquals(ranks, result, 0.001)
  }
  
  @Test def smallTest3() {
    val g = new Graph(8)
    val edges = Vector((0,1), (0,2), (1,3), (2,1), (2,4), (3,1), (3,4),
                        (3,5), (4,5), (4,6), (4,7), (5,7), (6,0), (6,4),
                        (6,7), (7,5), (7,6))
    edges.foreach(e => g.addEdge(e._1, e._2))
    val ranks = Array(0.0600, 0.0675, 0.0300, 0.0675, 0.0975, 0.2025, 0.1800, 0.2950)
    val (result, iter) = Rank.rank(g, 1, 0.001, 1000)
    assertArrayEquals(ranks, result, 0.001)
  }

  @Test def test10000() {
    val ranks = readPR("test/test-10000-rank.txt", 10000)
    Rank.main(Array("test/test-10000.txt"))
    val result = readPR("ranks.txt", 10000)
    assertArrayEquals(ranks, result, 0.000001)
  }
  
  @Test def test50000() {
    val ranks = readPR("test/test-50000-rank.txt", 50000)
    Rank.main(Array("test/test-50000.txt"))
    val result = readPR("ranks.txt", 50000)
    assertArrayEquals(ranks, result, 0.000001)
  }
  
  @Test def test100000() {
    val ranks = readPR("test/test-100000-rank.txt", 100000)
    Rank.main(Array("-epsilon", "0.0000001", "test/test-100000.txt"))
    val result = readPR("ranks.txt", 100000)
    assertArrayEquals(ranks, result, 0.000001)
  }
  
}