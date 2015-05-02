import java.util.HashMap;


/**
 * 
 */

/**
 * @author karthikeyanvb
 *
 */
public class WordNet {
	/*
	 * Data Structures to store Synsets and synset-id
	 */
	private final HashMap<Integer, String> id2synsets;
	private final HashMap<String, Bag<Integer>> synsets2id;
	
	private SAP sap;

	// Constructor to parse input files and populate data structures
	public WordNet(String synsets, String hypernyms) {
		id2synsets = new HashMap<Integer, String>();
		synsets2id = new HashMap<String, Bag<Integer>>();
		
		readSynsets(synsets);
		readHypernyms(hypernyms);
		
		sap = new SAP(readHypernyms(hypernyms));
	}
	
	// File to read synsets.txt file
	private void readSynsets(String synsets) {
		In input = new In(synsets);
		Bag<Integer> intbag;
		
		String line;
		while ((line = input.readLine()) != null) {
			String[] tokens = line.split(",");
			int synsetid = Integer.parseInt(tokens[0]);
			id2synsets.put(synsetid, tokens[1]);
			String[] nouns = tokens[1].split(" ");
			
			for (String str : nouns) {
				intbag = synsets2id.get(str);
				if (intbag == null) {
					intbag = new Bag<Integer>();
					intbag.add(synsetid);
					synsets2id.put(str, intbag);
				} else {
					intbag.add(synsetid);
				}
			}
		}
	}
	
	// File to read hypernym file
	private Digraph readHypernyms(String hypernyms) {
		Digraph graph = new Digraph(id2synsets.size());
		In input = new In(hypernyms);
		
		String line;
		while ((line = input.readLine()) != null) {
			String[] tokens = line.split(",");
			int synsetid = Integer.parseInt(tokens[0]);
			
			for (int itr = 1; itr < tokens.length; itr++) {
				graph.addEdge(synsetid, Integer.parseInt(tokens[itr]));
			}
		}
		
		verifyCycle(graph);
		verifyRoot(graph);
		
		return graph;
	}
	
	private void verifyCycle(Digraph graph) {
		DirectedCycle cycle = new DirectedCycle(graph);
		
		if (cycle.hasCycle()) {
			throw new IllegalArgumentException();
		}
	}
	
	private void verifyRoot(Digraph graph) {
		int roots = 0;

	    for (int i = 0, sz = graph.V(); i < sz; i++) {
	    	if (!graph.adj(i).iterator().hasNext()) {
	    		roots += 1;
	        }
	    }

	    if (roots != 1) {
	        throw new IllegalArgumentException();
	    }
	}
	
	// returns all WordNet nouns
	public Iterable<String> nouns() {
		if (synsets2id == null) {
			throw new NullPointerException();
		}
		
		return synsets2id.keySet();
	}
	
	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		if (word == null) {
			throw new NullPointerException();
		}
		return synsets2id.containsKey(word);
	}

	// Verfiy if the argument is a noun?
	private void verifySynset(String synset) {
		if (!isNoun(synset)) {
			throw new IllegalArgumentException();
		}
	}
	
	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		verifySynset(nounA);
		verifySynset(nounB);
		
		if (sap == null) {
			throw new NullPointerException();
		}
		
		return sap.length(synsets2id.get(nounA), synsets2id.get(nounB));
	}

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {
		verifySynset(nounA);
		verifySynset(nounB);
		
		if (sap == null || synsets2id == null || id2synsets == null) {
			throw new NullPointerException();
		}
		int id = sap.ancestor(synsets2id.get(nounA), synsets2id.get(nounB));
		String ancestor = id2synsets.get(id);
		
		return ancestor;
	}
	
	/**ja
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WordNet wordnet = new WordNet(args[0], args[1]);		
	}

}

