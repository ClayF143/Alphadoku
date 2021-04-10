import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	
	public String givens(String inputPath, String outputPath)
	{
		// read text matrix		
		File inputPuzzleFile = new File(inputPath);
		Scanner myReader;
		String data = "";
		try
		{
			myReader = new Scanner(inputPuzzleFile);
			while (myReader.hasNextLine())
			{
				String line = myReader.nextLine();
				if(! line.isEmpty())
				{
					data += line + "\n";
				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		data = data.replace(" ", "");
		data = data.replace("\n", "");
		char [] puzzle = data.toCharArray();
		System.out.println("Given Puzzle:");
		printPuzzle(puzzle);
		System.out.println("\n");
		
		// convert the given layout to cnf clauses, each square being its value is a clause
		boolean first = true;
		String givens = "";
		for(int i = 0; i < puzzle.length; i++)
		{
			if(puzzle[i] != '_')
			{
				if(!first)
					givens += "\n";
				else
					first = false;
				int letter = (int)puzzle[i] - (int)'A' + 1;
				int var = i*25 + letter;
				givens += String.valueOf(var) + " 0";
			}
		}
		
		// make a text file and copy the rules and given layout
		data = "";
		try(FileInputStream fileInputStream = new FileInputStream(RULESPATH))
		{
		    int ch = fileInputStream.read();
		    while(ch != -1)
		    {
		        data += (char)ch;
		        ch = fileInputStream.read();
		    }
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(outputPath))
		{
			data = data + "\n" + givens;
		    fileOutputStream.write(data.getBytes());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	// returns the integer value that represents a given variable
	private int variableInt(int row, int col, int letter)
	{
		return (row * 5 + col) * 25 + letter;
	}
	
	private String removeSolution(char [] puzzle)
	{
		// if the first 3 squares are C,D,E, then cnf of removed solution
		// would be ~x1,1,3 | ~x1,2,4 | ~x1,3,5 ...
		
		// add the remove solution clause to the 'givens' cnf in a new text file
		
		return "";
	}
	
	public void solvePuzzle(String puzzlePath)
	{
		//String puzzleCnf = givens(puzzlePath);
		// minisat
		// if no solution stop and pring no solution
		// check for mult solutions, print result
	}
	
	public void printPuzzle(char [] puzzle)
	{
		for(int r = 0; r < 25; r++)
		{
			if(r%5 == 0 && r!= 0)
				System.out.print("\n\n\n");
			else if(r!= 0)
				System.out.print("\n");
			
			for(int c = 0; c < 25; c++)
			{
				if(c%5 == 0 && c!=0)
					System.out.print("   ");
				System.out.print(puzzle[r*25 + c] + " ");
			}
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
		a.givens(examplesLocation + "\\alpha_0.txt", "puzzle1.txt");
	}
}
