
public class Outcast {
	private final WordNet wordnet;
	
	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		this.wordnet = wordnet;
	}
	
	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		int[] distance = new int[nouns.length];
		int maximum = -1;
		String outcast = null;
		for (int nounA = 0; nounA < nouns.length; nounA++) {
			for (int nounB = 0; nounB < nouns.length; nounB++) {
				distance[nounA] += wordnet.distance(nouns[nounA], nouns[nounB]);
			}
			if (distance[nounA] > maximum) {
				outcast = nouns[nounA];
				maximum = distance[nounA];
			}
		}
		return outcast;
	}
	
	// Test client to test outcast detection
	public static void main(String[] args) {
	    WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}
}