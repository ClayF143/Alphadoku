import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class Alphadoku
{
	private final String RULESPATH = "rules.txt";
	public final int NUMVARS = 15625;
	
	public Alphadoku()
	{
		
	}
	
	public void rules() throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(RULESPATH));
		
		// top formating
		
		// each square only has 1 letter
		for(int square = 0; square < 25 * 25; square++)
		{
			// ex square 1
			// at least 1 var pertaining to square 1 must be true
			for(int i = 1 + square * 25; i<=25 + square * 25; i++)
			{
				writer.write(String.valueOf(i) + " ");
			}
			writer.write("0\n");
			
			// and no pair of those vars can both be true
			for(int i = 1 + square * 25; i <= 24 + square * 25; i++)
			{
				for(int j = i + 1; j <= 25 + square * 25; j++)
				{
					// -i -j 0
					writer.write("-" + String.valueOf(i) + " -" + String.valueOf(j) + " 0\n");
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
							writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
					}
					
					// same letter can't be in that column
					for(int c = 0; c < 25; c++)
					{
						int impliedFalseVar = varInt(row, c, letter);
						if(c != col)
							writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
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
								writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
						}
					}
				}
			}
		}
		writer.close();
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
		
		// make a text file and copy the rules and given layout
		try
		{
			// a puzzle's cnf is the rules cnf file plus a clause for each given letter in the puzzle
			copyFile(new File(RULESPATH), new File(outputPath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true));
			
			// convert the given layout to cnf clauses, each square being its value is a clause
			for(int i = 0; i < puzzle.length; i++)
			{
				if(puzzle[i] != '_')
				{
					int letter = (int)puzzle[i] - (int)'A' + 1;
					int var = varInt(i, letter);
					writer.write("\n" + String.valueOf(var) + " 0");
				}
			}
			writer.close();
		} catch (IOException e1)
		{
			e1.printStackTrace();
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
	
	public void copyFile(File source, File dest) throws IOException
	{
		FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
		sourceChannel = new FileInputStream(source).getChannel();
		destChannel = new FileOutputStream(dest).getChannel();
		destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		sourceChannel.close();
		destChannel.close();
	}
	
	// returns the integer value that represents a given variable
	private int varInt(int row, int col, int letter)
	{
		return (row * 5 + col) * 25 + letter;
	}
	private int varInt(int index, int letter)
	{
		return index * 25 + letter;
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
		try
		{
			a.rules();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		a.givens(examplesLocation + "\\alpha_0.txt", "puzzle1.txt");
	}
}
