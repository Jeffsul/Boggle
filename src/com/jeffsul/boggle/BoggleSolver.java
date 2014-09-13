package com.jeffsul.boggle;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class BoggleSolver {
	private static Scanner scanner = new Scanner(System.in);
	
	private static final String DICTIONARY_FILE = "TWL06.txt";
	private static final String BLOCKS_FILE = "Blocks.txt";
	private static final int NUM_BLOCKS = 16;
	private static final int SIZE = (int) Math.sqrt(NUM_BLOCKS);
	private static final String[] blocks = new String[NUM_BLOCKS];
	private static Prefix root = new Prefix();
	
	static class Prefix {
		private boolean isWord;
		private Prefix[] links = new Prefix[26];
		
		public void insert(String word) {
			if (word.length() == 0) {
				isWord = true;
			} else {
				int index = word.charAt(0) - 'A';
				Prefix next = links[index];
				if (next == null) {
					links[index] = new Prefix();
				}
				links[index].insert(word.substring(1));
			}
		}
		
		public boolean isPrefix(String prefix) {
			if (prefix.length() == 1) {
				return links[prefix.charAt(0) - 'A'] != null;
			}
			int index = prefix.charAt(0) - 'A';
			if (links[index] != null) {
				return links[index].isPrefix(prefix.substring(1));
			}
			return false;
		}
		
		public boolean isWord(String word) {
			if (word.length() == 0) {
				return isWord;
			}
			int index = word.charAt(0) - 'A';
			if (links[index] != null) {
				return links[index].isWord(word.substring(1));
			}
			return false;
		}
	}
	
	private HashMap<String, Boolean> boardWords;
	private int boardSum;
	
	private boolean[][] checked;
	private char[][] board;
	
	public BoggleSolver() {
		loadDictionary();
	}
	
	private static void loadDictionary() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(DICTIONARY_FILE));
			String line;
			while ((line = reader.readLine()) != null) {
				root.insert(line);
			}
		} 
		catch (Exception ex) {}
	}
	
	public void solveBoard(char[][] brd) {
		board = brd;
		boardWords = new HashMap<String, Boolean>();
		boardSum = 0;
		
		int len = board.length;
		checked = new boolean[len][len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				boardSum += findSum(i, j, blockToString(board[i][j]));
			}
		}
	}

	private int findSum(int row, int col, String string) {
		int sum = 0;
		if (root.isWord(string) && !boardWords.containsKey(string)) {
			sum = getWordScore(string);
			if (sum > 0) {
				boardWords.put(string, true);
			}
		}
		
		if (root.isPrefix(string)) {
			checked[row][col] = true;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0) {
						continue;
					}
					int newRow = row + i;
					int newCol = col + j;
					if (newRow >= 0 && newCol >= 0
					    && newRow < checked.length && newCol < checked.length && !checked[newRow][newCol]) {
						String ltr = blockToString(board[newRow][newCol]);
						if (root.isPrefix(string + ltr) || root.isWord(string + ltr)) {
							sum += findSum(newRow, newCol, string + ltr);
						}
					}
				}
			}
			checked[row][col] = false;
		}
		
		return sum;
	}
	
	public static int getWordScore(String word) {
		if (word == null) {
			return 0;
		}
		
		int length = word.length();
		/*if (word.indexOf('Q') != -1)
		{
			for (int i = 0; i < word.length(); i++)
				if (word.charAt(i) == 'Q')
					len--;
		}*/
		if (length < 3) {
			return 0;
		}
		if (length == 3) {
			return 1;
		}
		return length - 3;
	}
	
	public static boolean isInDictionary(String word) {
		return root.isWord(word.toUpperCase());
	}
	
	public int getMaxScore() {
		return boardSum;
	}
	
	public String getMaxWord() {
		int max = 0;
		String bestWord = null;
		for (String word : boardWords.keySet()) {
			int pts = getWordScore(word);
			if (pts > max) {
				max = pts;
				bestWord = word;
			}
		}
		return bestWord;
	}
	
	public String[] getAllWords() {
		String[] allWords = new String[boardWords.size()];
		boardWords.keySet().toArray(allWords);
		Arrays.sort(allWords);
		return allWords;
	}
	
	private static String blockToString(char c) {
		String ltr = Character.toString(c);
		if (c == 'Q') {
			ltr += 'U';
		}
		return ltr;
	}
	
	private static void getBoardInput() {
		BoggleSolver solver = new BoggleSolver();
		while (true) {
			System.out.println("Enter the Boggle board:");
			String line = scanner.nextLine().toUpperCase();
			int len = line.length();
			char[][] board = new char[len][len];
			for (int i = 0; i < len; i++) {
				board[0][i] = line.charAt(i);
			}
			for (int i = 1; i < len; i++) {
				line = scanner.nextLine().toUpperCase();
				for (int j = 0; j < len; j++) {
					board[i][j] = line.charAt(j);
				}
			}
			
			solver.solveBoard(board);
			System.out.println("Max Score: " + solver.getMaxScore());
			
			String bestWord = solver.getMaxWord();
			System.out.println("Best Word: " + bestWord + " (" + getWordScore(bestWord) + ")");
			
			for (String word : solver.getAllWords()) {
				System.out.println(word + " (" + getWordScore(word) + ")");
			}
			
			System.out.println();
		}
	}
	
	private static void runTests() {
		BoggleSolver solver = new BoggleSolver();
		loadBlocks();
		//long start = System.nanoTime();
		
		double exp = 0;
		double var = 0;
		double expW = 0;
		double varW = 0;
		double count = 1000;
		
		for (int i = 0; i < count; i++) {
			solver.solveBoard(generateBoard());
			
			int score = solver.getMaxScore();
			exp += score / count;
			var += score * score / count;
			
			int words = solver.getAllWords().length;
			expW += words / count;
			varW += words * words / count;
		}
		System.out.println("Mean: " + exp);
		System.out.println("SD: " + Math.sqrt((var - exp*exp)));
		System.out.println("Mean Words: " + expW);
		System.out.println("SD Words: " + Math.sqrt((varW - expW*expW)));
		//System.out.println("Finished: " + (System.nanoTime() - start) / 1000000000.0);
	}
	
	private static void loadBlocks() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(BLOCKS_FILE));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				blocks[i] = line;
				i++;
			}
		} catch (Exception ex) {}
	}
	
	private static char[][] generateBoard() {
		ArrayList<Integer> indices = new ArrayList<Integer>(NUM_BLOCKS);
		for (int i = 0; i < NUM_BLOCKS; i++) {
			indices.add(i);
		}
		char[][] board = new char[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				String block = blocks[indices.remove((int)(indices.size() * Math.random()))];
				board[i][j] = block.charAt((int)(Math.random() * block.length()));
			}
		}
		return board;
	}
	
	public static void main(String[] args) {
		//getBoardInput();
		runTests();
	}
}

