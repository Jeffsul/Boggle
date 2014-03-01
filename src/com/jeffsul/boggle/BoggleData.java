package com.jeffsul.boggle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BoggleData 
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("boardScores.txt"));
		String line;
		long sum = 0;
		long count = 0;
		while ((line = reader.readLine()) != null)
		{
			sum += Integer.parseInt(line);
			count++;
		}
		double avg = sum / (double) count;
		System.out.println("Average Board Score: " + avg);
		reader.close();
		
		reader = new BufferedReader(new FileReader("totalWords.txt"));
		sum = 0;
		count = 0;
		while ((line = reader.readLine()) != null)
		{
			sum += Integer.parseInt(line);
			count++;
		}
		double avgWPG = sum / (double) count;
		System.out.println("Average Words Per Game: " + avgWPG);
		reader.close();
		
		System.out.println("Average Points Per Word: " + (avg / avgWPG));
	}
	
//	int max = 750;
//	int min = 5;
//	int maxWord = 12;
//	int tests = 1000000;
//	int len = (int)Math.sqrt(blocks.size());
//	int[] scores = new int[tests];
//	int[] totWords = new int[tests];
//	int[] wordDistrib = new int[14];
//	Arrays.fill(wordDistrib, 0);
//	ArrayList<String> bestBoards = new ArrayList<String>();
//	ArrayList<String> worstBoards = new ArrayList<String>();
//	ArrayList<String> bestWords = new ArrayList<String>();
//	long time = System.nanoTime();
//	for (int i = 0; i < tests; i++)
//	{
//		board = generateBoard(len);
//		solver.solveBoard(board);
//		int maxScore = solver.getMaxScore();
//		String bestWord = solver.getMaxWord();
//		String[] wordList = solver.getAllWords();
//		scores[i] = maxScore;
//		totWords[i] = wordList.length;
//		for (int j = 0; j < wordList.length; j++)
//			wordDistrib[wordList[j].length() - 3]++;
//		if (maxScore >= max)
//			bestBoards.add(printBoard());
//		else if (maxScore <= min)
//			worstBoards.add(printBoard());
//		if (bestWord.length() >= maxWord)
//			bestWords.add(bestWord);
//		if (bestWord.length() >= 14)
//		{
//			System.out.println(bestWord);
//			System.out.println(printBoard());
//		}
//		if (maxScore > 1077)
//		{
//			System.out.println(maxScore);
//			System.out.println(printBoard());
//		}
//	}
//	System.out.println((System.nanoTime() - time) / 1000000000.0);
//	writeToFile(scores, "boardScores.txt");
//	writeToFile(totWords, "totalWords.txt");
//	writeToFile(wordDistrib, "wordDistribution.txt");
//	writeToFile(bestBoards, "bestBoards.txt");
//	writeToFile(worstBoards, "worstBoards.txt");
//	writeToFile(bestWords, "bestWords.txt");
}

