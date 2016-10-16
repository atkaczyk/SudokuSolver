import java.util.Comparator;

public class Organism {
	int[][] chromosome = new int [9][9]; 
	boolean [] fixedPositions = new boolean [81];
	int fitness;
	double probability;
	int rank;

	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public int getfitness() {
		return fitness;
	}
	public void setfitness(int fitness) {
		this.fitness = fitness;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getRank() {
		return this.rank;
	}
} 

class ProbabilityComparator implements Comparator<Organism> {

	@Override
	public int compare(Organism o1, Organism o2) {
		double pr1 = o1.getProbability();
		double pr2 = o2.getProbability(); 
		 if(pr1 > pr2){
			  return 1; 
		  } else if (pr1== pr2){
			  return 0;
		  }  
		  return -1;
		  
	} 
}

class FitnessComparator implements Comparator<Organism> {

	@Override
	public int compare(Organism o1, Organism o2) {
		return o1.fitness - o2.fitness; // will be negative if current is less than next, 0 if they are the same and positive if current fitness is greater.
	}
}