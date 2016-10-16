import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

	
	public static void main(String[] args){
		SimulatedAnnealing SA = new SimulatedAnnealing();
		
		String puzzle1 = "";
		String puzzle2 = "";
		String puzzle3 = "";
		String puzzle4 = "";

		int[][] board = new int[9][9];
		//original puzzle //String puzzle = "198526347725341698346978215981257463564139872237684159473815926819762534652493781";
		//	String puzzle = "190520047025341600340908215980057003564130870200684159073800906819702534602400781";
		//String puzzle = "190526340000341600046900015900057463564139070037684100073815926810002534052493701";

		//String puzzle = "190526347720341600046978215900057463564139070037684100073815000819762534052493781";
		//String puzzle = "190526347720341600046978215900057463564139070037684100073815000819762534052493781";

		/*Easy: blanks ~ */
		//String puzzle = "190506040720300608306908205900057460560130072007684100073815926819762534052493781"; 
		//	String puzzle = "190500307720340608346908210900057463560130072237604100073810026809702530052093081"; 
		//	String puzzle = "190500307720340608046908210900057463560130072237604100073810026809702530052493081"; //this works with 20 per generation in usually under 500 generations. for both two point/single point crossover.

		/* Medium blanks ~  */
		//this one solves: USE THIS!
		puzzle1 = "190500307720340608346908210900057463560130072237604100073810026809702530052093081"; 
		
		/*GA SOLVES */
		puzzle2 = "602041005100205840854607291300462570428753109506010320003126057745300012261504903";
		
		//puzzle1 = "602041005100205040854607091300462570028753109506000320003126057745300012261504903";
		//this one doesn't solve for GA, too many empty spaces.
		//puzzle2 = "190500307020340608306908010900057060560130070037604100073010026809702530052093081"; 
		
		puzzle3 = "206310950050062013913805200008107090597038021002096407020009006601053079089601502";
		
		puzzle4 = "076004908050902010010870260460020305097008020030590080305080106041050809080601030";
		

		
		writeBoard(puzzle1, board);
		System.out.println("------------------------------------------------  Using the first puzzle  ");
		SA.displayBoard(board);

		System.out.println(" /*** GENETIC ALGORITHM 1 START ");
		//runGA(board);
		System.out.println(" /*** GENETIC ALGORITHM 1 END ");
		System.out.println(" /*** SIMULATED ANNEALING 1 START " + getCurrentTimeStamp());
		SA.initSolver(board);
		System.out.println(" /*** SIMULATED ANNEALING 1 END " + getCurrentTimeStamp());

		System.out.println("------------------------------------------------  Using the second puzzle  ");
		writeBoard(puzzle2, board);
		SA.displayBoard(board);
		System.out.println(" /*** GENETIC ALGORITHM 2 START ");
		runGA(board);
		System.out.println(" /*** GENETIC ALGORITHM 2 END ");
		System.out.println(" /*** SIMULATED ANNEALING 2 START " + getCurrentTimeStamp());
		SA.initSolver(board);
		System.out.println(" /*** SIMULATED ANNEALING 2 END " + getCurrentTimeStamp());
		
		System.out.println("------------------------------------------------  Using the third puzzle ");
		writeBoard(puzzle3, board);
		SA.displayBoard(board);
		System.out.println(" /*** GENETIC ALGORITHM 3 START " + getCurrentTimeStamp());
		runGA(board);
		System.out.println(" /*** GENETIC ALGORITHM 3  END " + getCurrentTimeStamp());
		System.out.println(" /*** SIMULATED ANNEALING 3 START " + getCurrentTimeStamp());
		SA.initSolver(board);
		System.out.println(" /*** SIMULATED ANNEALING 3 END " + getCurrentTimeStamp());

		System.out.println("------------------------------------------------  Using the fourth puzzle");
		writeBoard(puzzle4, board);
		SA.displayBoard(board);
		System.out.println(" /*** GENETIC ALGORITHM 4 START ");
		runGA(board);
		System.out.println(" /*** GENETIC ALGORITHM 4 END ");
		System.out.println(" /*** SIMULATED ANNEALING 4  START " + getCurrentTimeStamp());
		SA.initSolver(board);
		System.out.println(" /*** SIMULATED ANNEALING 4 END " + getCurrentTimeStamp());
		
		
	}

	private static void runGA(int[][] board) {
		System.out.println("Using Tournament Selection: ");
		
		System.out.println("Test 1 -- single point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 0, 0, 0);
		System.out.println("Test 2 -- single point crossover with square mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 0, 0, 1);
		System.out.println("Test 3 -- two point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 0, 1, 0);
		System.out.println("Test 4 -- two point crossover with square bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 0, 1, 1);

		System.out.println("Using Rank Selection:  "); 
		System.out.println("Test 1 -- single point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 1, 0, 0);
		System.out.println("Test 2 -- single point crossover with square mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 1, 0, 1);
		System.out.println("Test 3 --  two point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 1, 1, 0);
		System.out.println("Test 4 -- two point crossover with square bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 1, 1, 1);

		System.out.println("Using Roulette Selection: ");
		System.out.println("Test 1 -- single point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 2, 0, 0);
		System.out.println("Test 2 -- single point crossover with square mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 2, 0, 1);
		System.out.println("Test 3 -- two point crossover with single bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 2, 1, 0);
		System.out.println("Test 4 -- two point crossover with square bit mutation.  @"+ getCurrentTimeStamp());
		new GeneticAlgorithm(board, 20, 2, 1, 1);

	}
	public static String getCurrentTimeStamp() {
	    return new SimpleDateFormat("mm:ss.SSS").format(new Date());
	}
	private static void writeBoard(String puzzle, int [][] board){
		int empty = 0;
		for(int i = 0; i < puzzle.length(); i++){ 
			char c = puzzle.charAt(i);		
			if(c == '0'){
				empty++;
			}
			board[i/9][i%9] = Character.digit(c, 10);	
		}
		System.out.println("Note this board has " + empty + " empty spaces");
		
		
	}
	
}


