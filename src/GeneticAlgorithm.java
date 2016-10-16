import java.util.*;

public class GeneticAlgorithm {
	boolean [] fixedPositions;
	boolean solutionFound;
	int maxGen = 4500; //3500 
	int tries = 0;
	int selection; //selection: 0 for tournament, 1 for rank, 2 for roulette.
	int crossover; //crossover: 0 for single point, 1 for two point
	int mutationRate; // 0 for single bit, 1 for entire square
	public GeneticAlgorithm(int[][] board, int size, int select, int cross, int mutation) {
		selection = select;
		crossover = cross;
		mutationRate = mutation;

		fixedPositions = new boolean[81];
		solutionFound = false;

		//displayBoard(board);
		//ArrayList<Organism> organisms = initializePopulation(size, board); // give the population size and the potential numbers to choose from
		int gen = 0;
		while (solutionFound == false && tries < 100) { // && gen != maxGen && gen != -1 ){
			ArrayList<Organism> organisms = initializePopulatonSquares(size, board);
			tries ++;
			solutionFound = false;
			gen = mate(organisms, 0, board); 
		}
		if(solutionFound == true){
			System.out.println("    - A solution was found after " + tries + " attempts. At generation: " + gen );			
		} else if (tries >= 100) {
			System.out.println("    - We've exceeded our attempts, no solution.");
		}
	}

	private ArrayList<Organism> initializePopulatonSquares(int size, int[][] board) {
		ArrayList<Integer> missingValues = new ArrayList<Integer>();
		ArrayList<Organism> population =new ArrayList<Organism>(size);

		for (int i = 0; i < size; i++){
			population.add(new Organism());
			population.get(i).chromosome = new int[9][9];

			for(int r = 0; r < 7; r+=3){ //row 0, 3, 6
				for(int c = 0; c < 7; c+=3){				
					missingValues = updateVals(missingValues); //generate all possible numbers
					for(int row = r; row < r + 3; row++){
						for(int col = c; col < c + 3; col++){
							if(board[row][col] != 0){ //if there is already a nonzero entry, 
								missingValues.remove(new Integer( board[row][col]));
								population.get(i).chromosome[row][col] = new Integer( board[row][col]);
								fixedPositions[row*9 + col] = true;
							}
						}
					}
					Collections.shuffle(missingValues);
					for(int row = r; row < r + 3; row++){
						for(int col = c; col < c + 3; col++){
							if(board[row][col] == 0){ //if the entry is zero
								population.get(i).chromosome[row][col] = missingValues.remove(0); //now each chromosome is unique.
								fixedPositions[row*9 + col] = false;
							}
						}
					}
				}
			}
		}
		evaluatePopulation(population);
		return population;
	}

	//evaluates the fitness of every organism in the population
	public void evaluatePopulation(	ArrayList<Organism> population){
		for (int i = 0; i < population.size(); i++){
			//	displayBoard(population.get(i).chromosome);
			population.get(i).fitness = 81 - fitness(population.get(i).chromosome); //evaluate the fitness by the number of correct solutions - number of possible errors 
			if(population.get(i).fitness == 81){
				solutionFound = true;
				//System.out.println("OMG PERFECT SOLUTION ----------------------------------------------------------------------------- found! ");
				//displayBoard(population.get(i).chromosome);
				break;
			}
		}
	}

	//returns number of conflicts horizontally and vertically //0 is a perfect organism
	public int fitness(int[][] chromosome){ 
		int[][] candidateSolution = new int[9][9];
		copyArray(chromosome, candidateSolution);
		int num = 0;
		HashMap<Integer, Integer> numbers = new HashMap<Integer, Integer>();

		for(int row = 0; row < 9; row++){//count conflicts in the rows. 
			for(int col = 0; col < 9; col++){
				if(numbers.get(candidateSolution[row][col]) == null) // maybe use the contains value function and try that.. if the map contains no value at this key.
					numbers.put(candidateSolution[row][col], 1); //add the key with a value of one.
				else
					numbers.put(candidateSolution[row][col], numbers.get(candidateSolution[row][col]) + 1); //if it does contain a value... then wtf are you doing?
			}

			for(int j = 1; j <= 9; j++)	{
				if(numbers.get(j) != null && numbers.get(j) > 1){  
					num += numbers.get(j) - 1; //adding to the utility.. 
				}
				numbers.put(j, null); //reset map for next row
			}
		}
		//count column errors
		for(int col = 0; col < 9; col++){
			for(int row = 0; row < 9; row++){
				if(numbers.get(candidateSolution[row][col]) == null)
					numbers.put(candidateSolution[row][col], 1);
				else
					numbers.put(candidateSolution[row][col], numbers.get(candidateSolution[row][col]) + 1);
			}
			for(int j = 1; j <= 9; j++){
				if(numbers.get(j) != null && numbers.get(j) > 1){
					num += numbers.get(j) - 1;
				}
				numbers.put(j, null); //reset map for next column
			}
		}
		//System.out.println("FITNESS " + num);
		return num; //this number is the number of errors in this chromosome/candidates board both horiz and vertically
	}

	//mating is the selection process, // using tournament selection which is basically just picking 
	public int mate(ArrayList<Organism> population, int generation, int[][] board) { 
		ArrayList<Organism> newPopulation =new ArrayList<Organism>();
		newPopulation.clear();
		Organism parent1;
		Organism parent2;
		Organism child1;
		Organism child2;
		//	int fittest = fittestOrganism(population).fitness;
		do {
			if(selection == 0) { 	
				parent1 = tournamentSelection(population); 
				parent2 = tournamentSelection(population); 
			} else if (selection == 1 ){
				parent1 =  rankSelection(population); 
				parent2 = rankSelection(population); 

			} else { //if (selection == 2){
				parent1 =rouletteWheelSelection(population);
				parent2 = rouletteWheelSelection(population);
			}

			if(crossover == 0) { 
				child1 = singlePointCrossover(parent1, parent2, board);
				child2 = singlePointCrossover(parent2, parent1, board);
			} else { //crossover = 1
				child1 = twoPointCrossover(parent1, parent2, board);
				child2 = twoPointCrossover(parent2, parent1, board);
			}
			if(mutationRate == 0){
				mutateSinglePoints(child1);
				mutateSinglePoints(child2);
			}else {
				mutateEntireSquare(child1); 
				mutateEntireSquare(child2); 
			}

			newPopulation.add(child1);
			newPopulation.add(child2);
		} while (newPopulation.size() < population.size()); //or just not minus one if you only have one child
		population.clear();
		evaluatePopulation(newPopulation);

		generation++;

		if(solutionFound == false) {
			if (generation < maxGen){
				try{
					return mate(newPopulation, generation, board);
				}
				catch(StackOverflowError e){
					System.err.println("Unsat.");
					return -1;
				}
			} else{ 
				return generation;
			}
		} else {
			//System.out.println("You've found the most optimal primate ------");
			return generation;
		}

	}

	public Organism rouletteWheelSelection(ArrayList<Organism> population){
		double globalFitness = 0.0;
		double totalProbability = 0.0;

		//calculate the global fitness
		for(int i = 0; i < population.size(); i++){
			globalFitness += population.get(i).fitness;
		}

		//calculate the global probabilities
		for(int i = 0; i < population.size(); i++){
			population.get(i).probability = totalProbability + (population.get(i).fitness/globalFitness);
			totalProbability += population.get(i).probability;
		}
		double rand = Math.random() * totalProbability; //omg fuck i think this is supposed to be from 0-global fitness? wtf...shit.
		Collections.sort(population, new ProbabilityComparator());
		
		for(int i = 0; i < population.size()-1; i++){
			//System.out.println(population.get(i).probability + " < " + rand + " < " + population.get(i+1).probability);
			if (population.get(i).probability  < rand && rand < population.get(i+1).probability){
				return population.get(i);
			}
		}
		return population.get(0);
	}	

	/* THIS IS BINARY TOURNAMENT SELECTION */
	public Organism tournamentSelection(ArrayList<Organism> population){
		//select two invididuals from the population, randomly
		for(int i = 0; i < population.size(); i++){
			population.get(i).probability = 0;
		}

		int i = (int)Math.random() * population.size(); //totalProbability; //between 0 and 100.
		int j = (int)Math.random() * population.size(); //totalProbability; //between 0 and 100.
		if(population.get(i).fitness > population.get(j).fitness  ){
			return population.get(i);
		} else {
			return population.get(j);
		}

	}	

	public Organism rankSelection(ArrayList<Organism> population){
		double globalRank = 0.0;
		double totalProbability = 0.0;
		//Rank the population in order of their fitness values
		Collections.sort(population, new FitnessComparator());
		//calculate the global rank
		for(int i = 0; i < population.size(); i++){
			population.get(i).rank = i+1;
			globalRank += population.get(i).rank;
		}

		//calculate the global probabilities
		for(int i = 0; i < population.size(); i++){
			population.get(i).probability = totalProbability + (population.get(i).rank/globalRank);
			totalProbability += population.get(i).probability;
		}

		double rand = Math.random() * totalProbability; //omg fuck i think this is supposed to be from 0-global fitness? wtf...shit.
		Collections.sort(population, new ProbabilityComparator());
		
		for(int i = 0; i < population.size()-1; i++){
			//System.out.println(population.get(i).probability + " < " + rand + " < " + population.get(i+1).probability);
			if (population.get(i).probability  < rand && rand < population.get(i+1).probability){
				return population.get(i);
			}
		}
		return population.get(0);
	}

	private void mutateSinglePoints(Organism child) {
		int rIndex = 0;
		int cIndex = 0;
		int r1, c1, r2, c2;		

		for(int i = 0; i < 4; i++) {//do a few swaps.
			int square = (int)(Math.random()*9); //pick a random number between 1- 9

			//randomly decide what square to swap the two numbers.
			rIndex = (square <= 2) ? 0 : (square <= 5) ? 3 : 6;
			cIndex = (square % 3 == 0) ? 0 : (square == 1 || square == 4 || square == 7) ? 3 : 6;

			do { //randomly pick two numbers to swap as long as they are valid! 
				r1 = (int)(Math.random()*3);
				c1 = (int)(Math.random()*3);
				r2 = (int)(Math.random()*3);
				c2 = (int)(Math.random()*3);
			} while(fixedPositions[(rIndex+r1)*9+(cIndex+c1)] || fixedPositions[(rIndex+r2)*9+(cIndex+c2)]); //ensuring only to pick two positions to swap if they're enabled (not fixed)

			int[][] tempBoard = new int[9][9];
			copyArray(child.chromosome, tempBoard);
			//take the values in the temp board, that were selected to be swapped, and use the original board to passover the positions into the slots that need to be changed.
			child.chromosome[rIndex+r1][cIndex+c1] = tempBoard[rIndex+r2][cIndex+c2];
			child.chromosome[rIndex+r2][cIndex+c2] = tempBoard[rIndex+r1][cIndex+c1];

		}

	}

	private void mutateEntireSquare(Organism child){
		ArrayList<Integer> possibleInputs = new ArrayList<Integer>();
		//change the mutation, pick a square and reorder the unfixed positions in that square

		//now pick a random square.
		int square = (int)(Math.random()*9); //pick a random number between 1- 9

		//randomly decide what squares to rearrange all the possible inputs
		int rIndex = (square <= 2) ? 0 : (square <= 5) ? 3 : 6;		
		int cIndex = (square % 3 == 0) ? 0 : (square == 1 || square == 4 || square == 7) ? 3 : 6;

		for(int i = rIndex; i < rIndex+3; i++){
			for(int j = cIndex; j < cIndex+3; j++){
				if(fixedPositions[i*9+j] == false){	//this position is not fixed	
					possibleInputs.add(new Integer(child.chromosome[i][j]));
				}
			}
		}
		Collections.shuffle(possibleInputs);
		for(int i = rIndex; i < rIndex+3; i++){
			for(int j =cIndex; j < cIndex+3; j++){
				if(fixedPositions[i*9+j] == false){	//take out closed spots\
					child.chromosome[i][j] = possibleInputs.remove(0); 
				}
			}
		}

	}
	//taking the same point in both chromosomes and merging values before for p1 and after for p2 at that index. To create new offspring.
	//swaps only squares in rows. first 3, second 3 or third 3 squares.
	private Organism singlePointCrossover(Organism parent1, Organism parent2, int[][] deletethisparamlol) {
		int [] chromosome1 = new int[81];
		int [] chromosome2 = new int[81];

		Organism child = new Organism();
		int[] childChromosome = new int [81];
		//represent the board as a string, where each 9 integers is a new row.
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int p1num = parent1.chromosome[i][j];
				chromosome1[i*9+j] = p1num; 
				int p2num = parent2.chromosome[i][j];
				chromosome2[i*9+j] = p2num;
			}
		}

		//consider the fixed positions, and extract the values from the first 3 squares (00, 01, 02) and swap them with the bottom six squares of parent two.
		//For parent one, collect the unfixed up to an index and place them in child
		int specifiedStoppingIndex =27; //after the third row 
		for(int i = 0; i < 81; i++) {
			if(fixedPositions[i]){
				childChromosome[i] = chromosome1[i]; //could be 1 or 2's these are fixed so it doesn't matter.
			} 
			if(i <= specifiedStoppingIndex){
				childChromosome[i] = chromosome1[i]; //left of the specified stopping index, these are parent 1's details.
			}
			else if(i > specifiedStoppingIndex){
				childChromosome[i] = chromosome2[i]; //right of the specified stopping index, these are parent 2's details.
			}
		}

		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				child.chromosome[i][j] = childChromosome[i*9 + j];
			}
		}
		return child;
	}

	private Organism twoPointCrossover(Organism parent1, Organism parent2 , int [][] board){
		Organism child = new Organism();
		child.chromosome = new int [9][9];

		//now pick a random square.
		int square = (int)(Math.random()*9); //pick a random number between 1- 9

		//randomly decide what square to keep from parent 1.
		int rIndex = (square <= 2) ? 0 : (square <= 5) ? 3 : 6;		
		int cIndex = (square % 3 == 0) ? 0 : (square == 1 || square == 4 || square == 7) ? 3 : 6;

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(fixedPositions[i*9+j]){	//this position is fixed
					child.chromosome[i][j] = new Integer(board[i][j]);
				} else {
					if ((i > rIndex - 1 && i < rIndex + 3) && (i >cIndex - 1 && i < cIndex + 3) ){
						//give the child parent one's info.
						child.chromosome[i][j] = new Integer(parent1.chromosome[i][j]);
					} else {

						child.chromosome[i][j] = new Integer(parent2.chromosome[i][j]);
					}
				}

			}
		}
		return child;

	}

	private Organism randomOrganism(ArrayList<Organism> population) {
		int rand = (int)Math.random() * population.size();

		return population.get(rand);
	}

	private Organism fittestOrganism(ArrayList<Organism> population) {
		Collections.sort(population, new FitnessComparator()); 
		return population.get(population.size()-1); //the fittest one should be at the end of the list eh
	}

	public void displayBoard(int[][] board){
		for(int row = 0; row < 9; row++){
			System.out.println();
			if(row==0){
				System.out.println("\n -----------------------");
			}
			for(int col = 0; col < 9; col++){
				if(col ==0) {System.out.print("| ");}
				if(board[row][col] != 0 ){
					System.out.print(board[row][col] + " ");
				}
				else{
					System.out.print("-" + " ");
				}	
				if(col==2 | col == 5 | col ==8){
					System.out.print("| ");
				}
			}
			if(row==2 | row == 5 | row ==8){System.out.print("\n -----------------------");}
		}
		System.out.println();
	}
	public void copyArray(int[][] source, int[][] destination){
		for (int a=0;a<source.length;a++){
			System.arraycopy(source[a],0,destination[a],0,source[a].length);
		}
	}
	public ArrayList<Integer> updateVals(ArrayList<Integer> missingValues){
		missingValues.clear();
		
		for(int i = 1; i <= 9; i++)
			missingValues.add(i);
		
		return missingValues;
	}
}

