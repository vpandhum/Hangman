import java.io.*;
import java.util.*;
import java.util.Random;

public class HangMan {

	// array of words to be used for hangman
	// static String [] words = {"dicitionary","chair","table","water",
	// "apple","cereal", "attempted"};

	// list holds the attempted letters used by the player
	static List<Character> attempts = new ArrayList<Character>();
	static int correct = 0;
	static int incorrect = 0;
	static int letters;
	static int numTries = 5;
	static boolean YES = true;
	static boolean NO = false;
	static hangAI computer = new hangAI();

	/**
	 * pick a random words from 'wordList'
	 */

	public static String select(String[] wordList) {
		Random r = new Random();
		int n = r.nextInt(wordList.length);
		return wordList[n];
	}

	/**
	 * creates a dotted line with the same number of spaces as the number of
	 * letters in the word to be guessed
	 * 
	 * @param g
	 */

	public static String line(String g) {
		StringBuilder state = new StringBuilder();
		char[] word = g.toCharArray();
		int check = 0;

		for (int i = 0; i < word.length; i++) {
			for (int j = 0; j < attempts.size(); j++) {
				if (word[i] == attempts.get(j)) {
					state.append(attempts.get(j));
					System.out.print(attempts.get(j));
					check = 1;
				}
			}
			if (check == 0) {
				state.append("_");
				System.out.print("_");
			} else {
				check = 0;
			}
		}
		System.out.println("");
		String state2 = state.toString();
		return state2;
	}

	/**
	 * gets user input and puts it into 'attempts' list
	 * 
	 * @param g
	 */

	public static Character getAttempt() {
		Scanner in = new Scanner(System.in);
		Character g = in.next().charAt(0);
		if (attempts.contains(g)) {
			System.out
					.println("Guess has already been tried. Please guess again.");
			getAttempt();
		} else {
			attempts.add(g);
		}
		return g;
	}

	public static Character AI_Attempt(String state) {
		Character g = computer.makeGuess(state);
		if (attempts.contains(g)) {
			System.out
					.println("Guess has already been tried. Please guess again.");
			AI_Attempt(state);
		} else {
			attempts.add(g);
		}
		return g;
	}

	/**
	 * checks to see if the attempted letter is correct or not then increments
	 * the appropriate variable
	 * 
	 * @param g
	 *            : word being guess
	 * @param a
	 *            : attempted letter guess at word
	 */
	public static void checkAttempt(String g, Character a) {
		if (g.indexOf(a) >= 0) {
			computer.updateGuesses(a, YES);
			correct++;
		} else {
			computer.updateGuesses(a, NO);
			incorrect++;
		}
		return;
	}

	/**
	 * Checks win condition
	 * 
	 * @param g
	 * @return true : if you won false: if you lost
	 */
	public static boolean win(String g) {
		if (incorrect < 5 && correct == letters) {
			return true;
		}
		return false;
	}

	/**
	 * Finds the number of unique letters in a word by going through the word
	 * and putting all of the letters into a list. The repeated letter does not
	 * go into the list.
	 * 
	 * @param g
	 *            : Word to be checked
	 * @return the number of unique letters in a word
	 */
	public static int numCorrect(String g) {
		int check = 0; // fake boolean to check if the letter is already in
						// 'attempts'
		char[] word = g.toCharArray(); // breaks the word into an array of char
		List<Character> unique = new ArrayList<Character>(); // holds unique
																// letters
		unique.add(word[0]); // adds the first letter into unique

		for (int i = 1; i < g.length(); i++) {
			for (int j = 0; j < unique.size(); j++) {
				if (unique.get(j) == word[i]) { // if the letter is already in
												// the list
												// continue to the next word
					check = 1;
					continue;
				}
			}
			if (check == 0) { // if the letter is not repeated add it to the
								// list
				unique.add(word[i]);
			} else {
				check = 0; // reset the check for the next letter
			}
		}
		return unique.size(); // return the number of unique letters in the word
	}

	/**
	 * _____ | | | ( ) | /|\ | / \ |_____
	 * 
	 * draws the Hangman
	 */
	public static void drawHangman() {
		System.out.println("_____\n|   |");
		for (int i = 0; i < 3; i++) {
			System.out.print("|");
			if (incorrect > 0 && i == 0) {
				System.out.print("  ( )");
			}
			if (i == 1) {
				if (incorrect == 2) {
					System.out.print("   |");
				} else if (incorrect == 3) {
					System.out.print("  /|");
				} else if (incorrect > 3) {
					System.out.print(" /O|O\\");
				}
			}
			if (i == 2) {
				if (incorrect == 4) {
					System.out.print("  /");
				} else if (incorrect > 4) {
					System.out.print("  / \\");
				}
			}
			System.out.println("");
		}
		System.out.println("|_____");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.nanoTime();

		String token1 = "";
		// Read in list of words and separate into strings using white space
		Scanner scan = new Scanner(new File("listofwords.txt"))
				.useDelimiter("\\s+");

		// list of Characters to accept the value
		List<String> temps = new LinkedList<String>();

		// while loop
		while (scan.hasNext()) {
			token1 = scan.next();
			temps.add(token1);
		}
		scan.close();

		// copy the strings into to an array
		String[] wordList = temps.toArray(new String[0]);

		String guess = select(wordList); // picks a random word from wordList
		letters = numCorrect(guess);
		String state1;

		while (incorrect < numTries && correct != letters) {
			drawHangman();
			state1 = line(guess);
			// System.out.println("state1: " + state1);
			checkAttempt(guess, AI_Attempt(state1));

			for (int i = 0; i < attempts.size(); i++) {
				System.out.print(attempts.get(i) + " ");
			}
			System.out.println("\n");
		}

		line(guess);
		drawHangman();
		if (win(guess)) {
			System.out.println("\nYou Got It!");
		} else {
			System.out.println("\nYou Lost!\n Answer: " + guess);
		}
		
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		double seconds = (double)duration/1000000000.0;
		System.out.println("Runtime: " + seconds + "seconds");
	}

}
