import java.util.*;

public class SimulatedAnnealing {
	boolean[] fixedPositions;
	int redundantSwaps;
	
	public void initSolver(int[][] board) {
		fixedPositions = new boolean[81];
		redundantSwaps = 0;
		int [][] originalBoard = new int [9][9];
		copyArray(board, originalBoard);
		initializeBoard(board);
		//displayBoard(board);
		solverSA(board, .8, 0); //YOUR TEMP STARTS AT .8

		System.out.println("\n ------------------------------");
		System.out.println("  Original Board for reference: ");
		displayBoard(originalBoard);
	}

	private void initializeBoard( int[][] board) {
		ArrayList<Integer> missingValues = new ArrayList<Integer>();
		for(int r = 0; r < 7; r+=3){ 
			for(int c = 0; c < 7; c+=3){				
				missingValues = updateVals(missingValues); //generate all possible numbers
				for(int row = r; row < r + 3; row++){
					for(int col = c; col < c + 3; col++){
						if(board[row][col] != 0){ //if there is already a nonzero entry, 
							missingValues.remove(new Integer( board[row][col]));
							board[row][col] = new Integer( board[row][col]);
							fixedPositions[row*9 + col] = true;
						}
					}
				}
				Collections.shuffle(missingValues);
				for(int row = r; row < r + 3; row++){
					for(int col = c; col < c + 3; col++){
						if(board[row][col] == 0){ //if the entry is zero
							board[row][col] = missingValues.remove(0); //now each chromosome is unique.
							fixedPositions[row*9 + col] = false;
						}
					}
				}
			}
		}
	}

	/** Finds and returns the number of conflicts in the board 
	 * @return number of conflicts in the board */
	public int utility(int[][] board){ 
		int totalErrors = 0;
		HashMap<Integer, Integer> errors = new HashMap<Integer, Integer>();

		for(int row = 0; row < 9; row++){//count conflicts in the rows. 
			for(int col = 0; col < 9; col++){
				if(errors.get(board[row][col]) == null) //if the map contains no value at this key.
					errors.put(board[row][col], 1); //add the key with a value of one.
				else
					errors.put(board[row][col], errors.get(board[row][col]) + 1);
			}

			for(int i = 1; i <= 9; i++)	{
				if(errors.get(i) != null && errors.get(i) > 1){  
					totalErrors += errors.get(i) - 1; //adding to the utility.. 
				}
				errors.put(i, null); //reset map for next row
			}
		}
		//count errors in the columns columns
		for(int col = 0; col < 9; col++){
			for(int row = 0; row < 9; row++){
				if(errors.get(board[row][col]) == null)
					errors.put(board[row][col], 1);
				else
					errors.put(board[row][col], errors.get(board[row][col]) + 1);
			}
			for(int i = 1; i <= 9; i++){
				if(errors.get(i) != null && errors.get(i) > 1){
					totalErrors += errors.get(i) - 1;
				}
				errors.put(i, null); //reset map for next column
			}
		}
		return totalErrors;
	}

	// cooling constant = .8
	public int[][] solverSA(int[][] board, double temperature, int numSwaps){
		int numConflicts = utility(board);
		int square = (int)(Math.random()*9); //pick a random number between 1- 9
		int rIndex = 0;
		int cIndex = 0;
		int r1, c1, r2, c2;		

		if(numConflicts == 0){
			//displayBoard(board);
			System.out.println("\n Solution found after " + numSwaps + " iterations. Redundant swap count: " + redundantSwaps + " Unique boards: " + (numSwaps - redundantSwaps));
			return board;
		}

		//randomly decide what square to swap the two numbers.
		rIndex = (square <= 2) ? 0 : (square <= 5) ? 3 : 6;
		cIndex = (square % 3 == 0) ? 0 : (square == 1 || square == 4 || square == 7) ? 3 : 6;

		do { //randomly pick two numbers to swap! 
			r1 = (int)(Math.random()*3);
			c1 = (int)(Math.random()*3);
			r2 = (int)(Math.random()*3);
			c2 = (int)(Math.random()*3);
		} while(fixedPositions[(rIndex+r1)*9+(cIndex+c1)] || fixedPositions[(rIndex+r2)*9+(cIndex+c2)]); //ensuring only to pick two positions to swap if they're enabled (not fixed)
		numSwaps++;

		//generate a temporary board aka neighbour board, with the original boards values.
		int[][] tempBoard = new int[9][9];
		copyArray(board, tempBoard);
		//take the values in the temp board, that were selected to be swapped, and use the original board to passover the positions into the slots that need to be changed.
		//(see the format of r1 -> r2 below..)
		tempBoard[rIndex+r1][cIndex+c1] = board[rIndex+r2][cIndex+c2];
		tempBoard[rIndex+r2][cIndex+c2] = board[rIndex+r1][cIndex+c1];

		int tempConflicts = utility(tempBoard); //evaluate the utility of the "swapped board"

		if(tempConflicts < numConflicts){ //if the tempBoard has a smaller utility, aka if it has less conflicts, overwrite the board with it.
			//System.out.println("Effective Swap at iteration:" + numSwaps + " --- position ("+(rIndex+r1)+","+(cIndex+c1) +") with (" +(rIndex+r2)+","+(cIndex+c2)+ ") and temperature " + temperature);
			copyArray(tempBoard, board);
			//displayBoard(board); 
		}else{
			//if the tempBoard is worse, then consider the probability of finding something better using the SA probability function.
			double probability = Math.exp((numConflicts - tempConflicts)/temperature); //this is the acceptance probability.
			double randNum = Math.random(); //
			if(probability>= randNum && ((rIndex+r1)!=(rIndex+r2) && (cIndex+c2) !=(cIndex+c1) )){
				copyArray(tempBoard, board); //if it appears that you are less likely to find something better? use the tempBoard still.
				System.out.println("Effective Swap at iteration:" + numSwaps + " --- position ("+(rIndex+r1)+","+(cIndex+c1) +") with (" +(rIndex+r2)+","+(cIndex+c2)+ ") and temperature " + temperature);
				//displayBoard(board); 

			}
			else { //the board is identical to the last one, this swap is redundant aka swapping the same position
				redundantSwaps++;
			}
		}

		if(numSwaps > 4450) //depth try 20000
			return board;
		try {
			return solverSA(board, temperature *= .8, numSwaps);
		} catch(StackOverflowError e){
			System.out.println("No solution found --> ");
			return board;
		}
	}

	/**
	 * Takes in two parameters, the latter is replaced by the former.
	 * All of the values in the second parameter are overwritten by the values in the first.
	 * @param source 
	 * @param destination
	 */
	public void copyArray(int[][] source, int[][] destination){
		for (int a=0;a<source.length;a++){
			System.arraycopy(source[a],0,destination[a],0,source[a].length);
		}
	}

	public void initializeSquare(int rowStart, int colStart, int[][] board) {
		ArrayList<Integer> missingValues = new ArrayList<Integer>();
		//generate all possible numbers
		missingValues.clear();
		for(int i = 1; i <= 9; i++)
			missingValues.add(i);

		for(int row = rowStart; row < rowStart + 3; row++){
			for(int col = colStart; col < colStart + 3; col++){
				if(board[row][col] != 0){ //if there is already a nonzero entry, 
					missingValues.remove(new Integer(board[row][col])); 		
					//fixedPositions[row][col] = true; assign the fixed positions here, if converting to a 2D array is better... 
				}
			}
		}
		Collections.shuffle(missingValues);	
		for(int row = rowStart; row < rowStart + 3; row++){
			for(int col = colStart; col < colStart + 3; col++){
				if(board[row][col] == 0) //if the entry is zero
					board[row][col] = missingValues.remove(0); //fill up the empty spaces in the board by replace the entry with next possible solution and remove the solution from that list
			}
		}
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
	public ArrayList<Integer> updateVals(ArrayList<Integer> missingValues){
		missingValues.clear();
		for(int i = 1; i <= 9; i++)
			missingValues.add(i);
		return missingValues;
	}
}