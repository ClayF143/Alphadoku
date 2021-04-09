import java.util.HashMap;

public class Alphadoku
{
	private final String RULESPATH = "rules.txt";
	
	public static void rules()
	{
		// each square only has 1 letter
		// each column has 1 of every letter
		// each row has 1 of every letter
		// each block has 1 of every letter
	}
	
	public static String givens(String filePath)
	{
		// read text matrix into a hashmap
		HashMap<Integer, String> puzzle = new HashMap<Integer, String>();
		
		// make a text file and copy the rules
		
		// convert hashmap into CNF and add to text file
		
	}
	
	private static int variableInt(int row, int col, int letter)
	{
		return (row * 5 + col) * 25 + letter;
	}
	
	private String removeSolution(HashMap<Integer, String> solution)
	{
		// if the first 3 squares are C,D,E, then cnf of removed solution
		// would be ~x1,1,3 | ~x1,2,4 | ~x1,3,5 ...
		
		// add the remove solution clause to the 'givens' cnf in a new text file
	}
	
	public void solvePuzzle(String puzzlePath)
	{
		String puzzleCnf = givens(puzzlePath);
		// minisat
		// if no solution stop and pring no solution
		// check for mult solutions, print result
	}
}
