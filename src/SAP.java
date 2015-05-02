import java.util.Iterator;


public class SAP {
	private final Digraph graph;
	private final BFS vbfs;
	private final BFS wbfs;
	private int sapDistance;
	private int sapVertex;
	
	// constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
    	this.graph = new Digraph(G);
    	this.vbfs = new BFS(G.V());
    	this.wbfs = new BFS(G.V());
    	
    	sapDistance = -1;
    	sapVertex = -1;
    }
    
	private class BFS implements Iterable<Integer> {
		private final boolean[] visited;
		private final int[] distance;
		private final Queue<Integer> modified = new Queue<Integer>();
		private final Queue<Integer> queue = new Queue<Integer>();
		
		public BFS(int size) {
			visited = new boolean[size];
			distance = new int[size];
			
			for (int i = 0; i < size; i++) {
				visited[i] = false;
				distance[i] = -1;
			}
		}
		
		public Iterator<Integer> iterator() {
			return modified.iterator();
		}
		
		public void clear() {
			while (!modified.isEmpty()) {
				 int v = modified.dequeue();
				 visited[v] = false;
				 distance[v] = -1;
			}
		}
		
		public boolean reachable(int v) {
			return visited[v];
		}
		
		public int distance(int v) {
			return distance[v];
		}
		
		public void bfsUtil() {
			while (!queue.isEmpty()) {
				int w = queue.dequeue();
				for (int vertex : graph.adj(w)) {
					if (!visited[vertex]) {
						visited[vertex] = true;
						distance[vertex] = distance[w] + 1;
						modified.enqueue(vertex);
						queue.enqueue(vertex);
					}
				}
			}
		}
		
		public void bfs(int v) {
			visited[v] = true;
			distance[v] = 0;
			
			modified.enqueue(v);
			queue.enqueue(v);
			
			bfsUtil();
		}
		
 		public void bfs(Iterable<Integer> v) {
 			if (v == null) {
 				throw new NullPointerException();
 			}
			for (int vertex : v) {
				visited[vertex] = true;
				distance[vertex] = 0;
				modified.enqueue(vertex);
				queue.enqueue(vertex);
			}
			
			bfsUtil();
		}
	}
	
	private void verifyInput(int v) {
		if (v < 0 || v >= graph.V()) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	private void verifyInput(Iterable<Integer> v) {
		if (v == null) {
			throw new NullPointerException();
		}
		for (int vertex : v) {
			verifyInput(vertex);
		}
	}
	
	private void init(int v, int w) {
		verifyInput(v);
		verifyInput(w);
		
		vbfs.clear();
		wbfs.clear();
		
		vbfs.bfs(v);
		wbfs.bfs(w);
	}
	
	private void init(Iterable<Integer> v, Iterable<Integer> w) {
		verifyInput(v);
		verifyInput(w);
		
		vbfs.clear();
		wbfs.clear();
		
		vbfs.bfs(v);
		wbfs.bfs(w);
	}
	
	private void findAncestorAndDistance() {
		int resultDistance = -1;
		int resultVertex = -1;
		
		BFS[] bfs = { vbfs, wbfs };
		
		for (BFS b : bfs) {
			for (int vertex : b) {
				if (vbfs.reachable(vertex) && wbfs.reachable(vertex)) {
					int distance = vbfs.distance(vertex) + wbfs.distance(vertex);
					
					if (resultDistance == -1 || distance < resultDistance) {
						resultDistance = distance;
						resultVertex = vertex;
					}
				}
			}
		}
		
		sapDistance = resultDistance;
		sapVertex = resultVertex;
	}
	
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
    	init(v, w);
    	
    	findAncestorAndDistance();
    	return sapDistance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
    	init(v, w);
    	
    	findAncestorAndDistance();
    	return sapVertex;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
	   if (v == null || w == null) {
		   throw new NullPointerException();
	   }
	   
	   init(v, w);
	   
	   findAncestorAndDistance();
	   return sapDistance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    	if (v == null || w == null) {
 		   throw new NullPointerException();
 		}
    	
    	init(v, w);
    	findAncestorAndDistance();
    	return sapVertex;
    }

    // do unit testing of this class
    public static void main(String[] args) {
    	In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}