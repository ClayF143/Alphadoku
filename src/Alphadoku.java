import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

public class Alphadoku
{
	private final String RULESPATH = "rules.txt";
	private final String NONUNIQUE_SOLUTION_PATH = "tempPuzzle.cnf";
	private final int NUM_VARS = 15625;
	private int numRulesClauses;
	
	public Alphadoku()
	{
		numRulesClauses = 0;
		try
		{
			numRulesClauses = rules();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public int rules() throws IOException
	{
		// creates a text file that has the cnf clauses for the rules of alphadoku
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(RULESPATH));
		int numClauses = 0;
		
		// write clauses for the rule: each square only has 1 letter
		for(int square = 0; square < 25 * 25; square++)
		{
			// ex square 1
			// at least 1 var pertaining to square 1 must be true
			for(int i = 1 + square * 25; i<=25 + square * 25; i++)
			{
				writer.write(String.valueOf(i) + " ");
			}
			writer.write("0\n");
			numClauses++;
			
			// and no pair of those vars can both be true
			for(int i = 1 + square * 25; i <= 24 + square * 25; i++)
			{
				for(int j = i + 1; j <= 25 + square * 25; j++)
				{
					// -i -j 0
					writer.write("-" + String.valueOf(i) + " -" + String.valueOf(j) + " 0\n");
					numClauses++;
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
					//varInt(row, col, letter) => -varInt(__, col, letter) & -(row, __, letter) & -varInt( square, letter)
					//a => -b  is the same as  -a | -b
					int mainVar = varInt(row, col, letter);
					
					// same letter can't be in that row
					for(int r = 0; r < 25; r++)
					{
						int impliedFalseVar = varInt(r, col, letter);
						if(r != row)
						{
							writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
							numClauses++;
						}
					}
					// same letter can't be in that column
					for(int c = 0; c < 25; c++)
					{
						int impliedFalseVar = varInt(row, c, letter);
						if(c != col)
						{
							writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
							numClauses++;
						}
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
							{
								writer.write("-" + String.valueOf(mainVar) + " -" + String.valueOf(impliedFalseVar) + " 0\n");
								numClauses++;
							}
						}
					}
				}
			}
		}
		writer.close();
		return numClauses;
	}
	
	public int givens(String inputPath, String outputPath)
	{
		// creates the full cnf file to find a solution for a given problem
		
		// read text matrix	from input file
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
			
			// first copy the rules over.
			BufferedReader reader = new BufferedReader(new FileReader(RULESPATH));
			char [] buffer = new char[8192];
			long count = 0;
			int n;
			while ((n = reader.read(buffer)) != -1)
			{
				writer.write(buffer, 0, n);
				count += n;
			}
			reader.close();
			
			int numclauses = numRulesClauses;
			
			// convert the given layout to cnf clauses, each square being its value is a clause
			boolean first = true;
			for(int i = 0; i < puzzle.length; i++)
			{
				if(puzzle[i] != '_')
				{
					if(!first)
						writer.write("\n");
					else
						first = false;
					//int letter = (int)puzzle[i] - (int)'A' + 1;
					int var = varInt(i, puzzle[i]);
					writer.write(String.valueOf(var) + " 0");
					numclauses++;
				}
			}
			// write the "p cnf vars clauses" header to the beginning of the file
			RandomAccessFile f = new RandomAccessFile(new File(outputPath), "rw");
			f.seek(0); // to the beginning
			String firstLine = "p cnf " + String.valueOf(NUM_VARS) + " " + String.valueOf(numclauses) + " 0\n";
			f.write(firstLine.getBytes());
			f.close();
			writer.close();
			return numclauses;
		} catch (IOException e1)
		{
			e1.printStackTrace();
			return 0;
		}
	}
	
	// returns the integer value that represents a given variable
	private int varInt(int row, int col, int letter)
	{
		return (row * 25 + col) * 25 + letter;
	}
	
	private int varInt(int index, char letter)
	{
		int l = (int)letter - (int)'A' + 1;
		return index * 25 + l;
	}
	
	private void removeSolution(char [] puzzle, String puzzlePath, int numvars)
	{
		// if the first 3 squares are C,D,E, then cnf of removed solution
		// would be ~x1,1,3 | ~x1,2,4 | ~x1,3,5 ...
		
		// add the remove solution clause to the 'givens' cnf in a new text file
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(NONUNIQUE_SOLUTION_PATH));
			BufferedReader in = new BufferedReader(new FileReader(puzzlePath));
			// first write the new header, which is the same as the old one but with 1 extra clause
			out.write("p cnf " + NUM_VARS + " " + String.valueOf(numvars + 1) + "\n");
			in.readLine();
			
			// then copy the givens cnf
			char [] buffer = new char[8192];
			long count = 0;
			int n;
			while ((n = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, n);
				count += n;
			}
			in.close();
			
			// then add the last clause at the bottom
			String newLine = "\n";
			for(int i = 0; i < puzzle.length; i++)
			{
				newLine += "-" + String.valueOf(varInt(i, puzzle[i])) + " ";
			}
			newLine += "0";
			out.write(newLine);
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void solvePuzzle(String puzzlePath, String outputPath)
	{
		// finds a solution to the puzzle if there is one, then checks to see if that solution is unique
		// uses the SAT4J solver
		
		// first create the cnf file
		int numvars = givens(puzzlePath, outputPath);
		
		ISolver solver = SolverFactory.newDefault();
		Reader reader = new DimacsReader(solver);
		try
		{
			IProblem problem = reader.parseInstance(outputPath);
			if(problem.isSatisfiable())
			{
				// if there's a solution, print it
				System.out.println("Satisfiable!");
				int [] solution = problem.model();
				char [] charSolution = new char [625];
				int i = 0;
				for(int var: solution)
				{
					if(var > 0)
					{
						var = (var - 1) % 25;
						char v = (char)((int)'A' + var);
						charSolution[i] = v;
						i++;
					}
				}
				printPuzzle(charSolution);
				
				// find second solution and print
				removeSolution(charSolution, outputPath, numvars);
				IProblem problem2 = reader.parseInstance(NONUNIQUE_SOLUTION_PATH);
				if(problem2.isSatisfiable())
				{
					System.out.println("Not Unique, second solution:");
					solution = problem2.model();
					charSolution = new char [625];
					i = 0;
					for(int var: solution)
					{
						if(var > 0)
						{
							var = (var - 1) % 25;
							char v = (char)((int)'A' + var);
							charSolution[i] = v;
							i++;
						}
					}
					printPuzzle(charSolution);
				}
				else
					System.out.println("Solution is unique.");
			}
			else
				System.out.println("No Solution");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// if no solution stop and print no solution
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
		System.out.println("\n");
	}
	
	public void solveDirectory(File examplesDirectory)
	{
		// runs solvePuzzle() on every file in a directory
		File [] filesList = examplesDirectory.listFiles();
		long time = 0;
		for(int i = 0; i < filesList.length; i++)
		{
			String exampleLocation = filesList[i].getAbsolutePath();
			String exampleSolution = "puzzle" + String.valueOf(i + 1) + ".cnf";
			System.out.println("Solving " + exampleLocation);
			long start = System.currentTimeMillis();
			solvePuzzle(exampleLocation, exampleSolution);
			long end = System.currentTimeMillis();
			time += end - start;
			System.out.println("Time: " + String.valueOf(end - start) + " milliseconds");
		}
		System.out.println("Average Time: " + String.valueOf(time / filesList.length));
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
		File examplesDirectory = new File(examplesLocation);
		File [] filesList = examplesDirectory.listFiles();
		
		Alphadoku a = new Alphadoku();
		a.solveDirectory(examplesDirectory);
	}
}
