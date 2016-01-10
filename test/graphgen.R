library(igraph)
options(scipen=999, "digits"=16)

# Compute some graphs using the Barabasi-Albert model.

graph.sizes <- c(10000, 50000, 100000)

for (s in graph.sizes) {
  g <- sample_pa(s)
  write.graph(g, paste0("test-", s, ".txt"), format="edgelist")
  
  # Calculate pagerank for the generated graph
  pr <- page_rank(g)
  
  output.name <- paste0("test-", s, "-rank.txt")
  
  # This loop writes very slowly
  for (i in 1:s) {
    cat(paste(i-1, "=", pr$vector[i], sep=" "), sep="\n", file=output.name, append=T)
  }
}