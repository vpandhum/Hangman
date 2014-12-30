import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.lang.*;

public class hangAI {

	List<String> wordList = new ArrayList<String>();
	List<Character> right = new ArrayList<Character>();
	List<Character> wrong = new ArrayList<Character>();
	List<String> likelyWords = wordList;
	int size = 0;

	hangAI() {
		try {
			String token1 = "";

			// Read in list of words and separate into strings using white space
			Scanner scan = new Scanner(new File("listofwords.txt"))
					.useDelimiter("\\s+");

			// list of Characters to accept the value
			List<String> temps = new LinkedList<String>();

			while (scan.hasNext()) {
				token1 = scan.next();
				temps.add(token1);
			}
			scan.close();

			// copy the strings into to an array
			String[] wordList2 = temps.toArray(new String[0]);

			// add to array list
			for (String s : wordList2) {
				wordList.add(s);
			}

		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public List<String> excludeBad (List<String> words, List<Character> wrong) {
		
		List<String> excluded = words;
		
		for (int i = 0; i < words.size(); i++) {
			String test = excluded.get(i);
			
			for (int j = 0; j < wrong.size(); j++) {
				String ctest = wrong.get(j).toString();
				
				if (test.contains(ctest)) {
					excluded.remove(i);
				}
			}
		}
		
		return excluded;
	}

	/**
	 * 
	 * @param words
	 *            : from the text
	 * @param guess
	 *            : the status of the word that is being guessed
	 * @param pos
	 *            : position of the 4-letter word
	 * @return
	 */
	public List<String> guessTheWord(List<String> words, String guess, int pos) {
		// check the wordlist

		List<String> guessWords = new ArrayList<String>();
		char test = ' ';
		size = words.size();

		for (int i = 0; i < words.size(); i++) {

			String temp = words.get(i);
			if (guess.charAt(pos) == '_') {
				continue;
			} else 
			if (guess.charAt(pos) == (temp.charAt(pos)) && pos < 4) {
				guessWords.add(words.get(i));
				test = guess.charAt(pos);
				continue;
			}
		}
		
		if (guessWords.isEmpty()) {
			return words;
		}
		
		return guessWords;
	}

	// state is the string --> example: "p__p"
	public char makeGuess(String state) {

		String str = state;

		StringBuilder badGuesses = new StringBuilder();
		StringBuilder goodGuesses = new StringBuilder();

		// append each of the wrong characters into a string
		for (Iterator<Character> ex = wrong.iterator(); ex.hasNext();) {
			badGuesses.append(ex.next());
		}

		// append each of the right characters into a string
		for (Iterator<Character> ex = right.iterator(); ex.hasNext();) {
			goodGuesses.append(ex.next());
		}

		String the_guesses = null;
		int counter = 0;

		
		for (int i = 0; i < 4; i++) {
			likelyWords = guessTheWord(likelyWords, state, i);
		}
		
		likelyWords = excludeBad(likelyWords, wrong);
		

		// get frequencies of each character
		LinkedHashMap<Character, Integer> f2 = new LinkedHashMap<Character, Integer>();
		f2 = getFrequencies();


		// CHOOSING A CHARACTER TO GUESS BASED ON HIGHEST FREQUENCY
		// make a guess, choose a letter
		char guessLetter = 'a';
		int freq = 0;
		boolean no_letter = true;
		for (char c = 'a'; c <= 'z'; c++) {
			if (!right.contains(c) && !wrong.contains(c)) {
				if (f2.get(c) != null && f2.get(c) > freq) {
					guessLetter = c;
					freq = f2.get(c);
					no_letter = false;
				}
			}
		}

		// when there's no letter left possible to guess
		if (no_letter) {
			for (char c = 'a'; c <= 'z'; ++c) {
				if (!(right.contains(c) || wrong.contains(c))) {
					return c;
				}
			}
		}
		
		return guessLetter;
	}

	// Get frequency of each letter within the possible words
	public LinkedHashMap<Character, Integer> getFrequencies() {
		String letterS = "abcdefghijklmnopqrstuvwxyz";
		LinkedHashMap<Character, Integer> frequencies = new LinkedHashMap<Character, Integer>();
		for (String possible : likelyWords) {
			for (char letter : possible.toCharArray()) {
				if (!frequencies.containsKey(letter)) {
					frequencies.put(letter, 1);
				} else {
					frequencies.put(letter, frequencies.get(letter) + 1);
				}
			}
		}
		return frequencies;
	}

	// update guesses if correct or wrong
	public void updateGuesses(char guess, boolean good) {
		if (good) {
			right.add(guess);
		} else {
			wrong.add(guess);
		}
	}

	// FILTER WORDS
	public void filter(String the_guesses, StringBuilder badGuesses) {
		String word = the_guesses;
		// System.out.println("guesses" + the_guesses);
		Pattern regex = Pattern.compile(word.replace(
				"_",
				(badGuesses.length() > 0) ? String.format("[a-z&&[^%s]]",
						badGuesses) : "[a-z]"));
		for (String guess : wordList) {
			Matcher matchy = regex.matcher(guess);
			if (matchy.find()) {
				likelyWords.add(guess); // get a list of words that match the
										// state
			}
		}
	}

	// testing purposes
	public void aList(List<Character> listy) {
		for (int i = 0; i < listy.size(); i++) {
			System.out.print(listy.get(i) + " ");
		}
		System.out.println();

	}

	public void bList(List<String> listy) {
		for (int i = 0; i < listy.size(); i++) {
			System.out.print(" " + listy.get(i));

		}
		System.out.println();

	}

	static void insertionSort(int[] frequency, char[] letter) {
		for (int j = 1; j < frequency.length; j++) {
			int index = frequency[j];
			int i = j - 1;
			// System.out.println(frequency[j]);
			char cIndex = letter[j];
			while ((i > -1) && (frequency[i] > index)) {
				frequency[i + 1] = frequency[i];

				letter[i + 1] = letter[i];
				i--;
			}
			frequency[i + 1] = index;
			letter[i + 1] = cIndex;
		}
	} // end of insertionSort

} // end of class

