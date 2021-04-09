import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Alphadoku
{
	private final String RULESPATH = "rules.txt";
	
	public Alphadoku()
	{
		
	}
	
	public static void rules()
	{
		// each square only has 1 letter
		// each column has 1 of every letter
		// each row has 1 of every letter
		// each block has 1 of every letter
	}
	
	public String givens(String filePath)
	{
		
		
		
		// read text matrix into a hashmap
		HashMap<Integer, String> puzzle = new HashMap<Integer, String>();
		
		File inputPuzzleFile = new File(filePath);
		Scanner myReader;
		String [] data = new String [25];
		try
		{
			myReader = new Scanner(inputPuzzleFile);
			int i = 0;
			while (myReader.hasNextLine())
			{
				String line = myReader.nextLine();
				if(! line.isEmpty())
				{
					data[i] = line;
					i++;
				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		for(String line: data)
		{
			System.out.println(line);
		}
		
		// make a text file and copy the rules
		
		// convert hashmap into CNF and add to text file
		
		return "";
		
	}
	
	private int variableInt(int row, int col, int letter)
	{
		return (row * 5 + col) * 25 + letter;
	}
	
	private String removeSolution(HashMap<Integer, String> solution)
	{
		// if the first 3 squares are C,D,E, then cnf of removed solution
		// would be ~x1,1,3 | ~x1,2,4 | ~x1,3,5 ...
		
		// add the remove solution clause to the 'givens' cnf in a new text file
		
		return "";
	}
	
	public void solvePuzzle(String puzzlePath)
	{
		String puzzleCnf = givens(puzzlePath);
		// minisat
		// if no solution stop and pring no solution
		// check for mult solutions, print result
	}
	
	public void printPuzzle(HashMap<Integer, String> puzzle)
	{
		for(int r = 0; r < 25; r++)
		{
			for(int c = 0; c < 25; c++)
			{
				Integer key = new Integer(r*25 + c);
				String letter = puzzle.get(key);
				System.out.print(letter + " ");
			}
			System.out.println();
		}
	}
	
	public void solveDirectory(String directoryPath)
	{
		
	}
	
	public static void main(String [] args)
	{
		String projectLocation = "";
		try
		{
			projectLocation = new java.io.File( "." ).getCanonicalPath();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		String examplesLocation = projectLocation + "\\example_puzzles";
		
		
		Alphadoku a = new Alphadoku();
		a.givens(examplesLocation + "\\alpha_0.txt");
	}
}
