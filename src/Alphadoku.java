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
	
	public void rules()
	{
		String rules = "";
		
		// top formating
		
		// each square only has 1 letter
		for(int square = 0; square < 25 * 25; square++)
		{
			// ex square 1
			// at least 1 var pertaining to square 1 must be true
			for(int i = 1 + square * 25; i<=25 + square * 25; i++)
			{
				rules += String.valueOf(i) + " ";
			}
			rules += "0\n";
			
			// and no pair of those vars can both be true
			for(int i = 1 + square * 25; i <= 24 + square * 25; i++)
			{
				for(int j = i + 1; j <= 25 + square * 25; j++)
				{
					// -i -j 0
					rules += "-" + String.valueOf(i) + " -" + String.valueOf(j) + " 0\n";
				}
			}
		}
		
		// each row, column, and block has 1 of every letter
		for(int row = 0; row < 25; row++)
		{
			for(int col = 0; col < 25; col++)
			{
				for(int letter = 1; letter <= 25; letter ++)
				{
					//varInt(row, col, letter) => -varInt(__, col, letter) & -varInt(row, __, letter) & -varInt( square, letter)
					//a => -b  is the same as  -a | -b
					int mainVar = varInt(row, col, letter);
					
					// same letter can't be in that row
					for(int r = 0; r < 25; r++)
					{
						int impliedFalseVar = varInt(r, col, letter);
						if(r != row)
							rules += "-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n";
					}
					
					// same letter can't be in that column
					for(int c = 0; c < 25; c++)
					{
						int impliedFalseVar = varInt(row, c, letter);
						if(c != col)
							rules += "-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n";
					}
					
					// same letter can't be in that block
					int rowBlock = (int) (row/5);
					int colBlock = (int) (col/5);
					for(int r = rowBlock * 5; r < (rowBlock + 1) * 5; r++)
					{
						for(int c = colBlock * 5; c < (colBlock + 1) * 5; c++)
						{
							int impliedFalseVar = varInt(r, c, letter);
							if(c != col || r != row)
								rules += "-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n";
						}
					}
				}
			}
		}
		
		writeToFile(rules, RULESPATH);
	}
	
	public void givens(String inputPath, String outputPath)
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
	}
	
	public void writeToFile(String data, String outputPath)
	{
		try(FileOutputStream fileOutputStream = new FileOutputStream(outputPath))
		{
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
	}
	
	// returns the integer value that represents a given variable
	private int varInt(int row, int col, int letter)
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
		a.rules();
		// a.givens(examplesLocation + "\\alpha_0.txt", "puzzle1.txt");
	}
}
