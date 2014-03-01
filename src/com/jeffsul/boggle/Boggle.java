package com.jeffsul.boggle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class Boggle extends JFrame
{
	private static final String BLOCKS_FILE = "Blocks.txt";
	private static final int NUM_BLOCKS = 16;
	private static final int SIZE = (int) Math.sqrt(NUM_BLOCKS);
	
	private static final Font TEXT_FONT = new Font("Monospaced", Font.PLAIN, 14);
		
	private static final BoggleSolver SOLVER = new BoggleSolver();
	
	private String[] blocks = new String[NUM_BLOCKS];
	
	private char[][] board;
	private Block[][] boardBtns;
	
	private JTextArea wordListField;
	private JTextField wordEntry;
	private JLabel scorePtsLbl;
	private int score;
	
	private ArrayList<String> wordList;
	
	private boolean wordOnBoard;
	
	public Boggle()
	{
		super("Boggle by Jeffrey Sullivan");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		
		loadBlocks();
		
		initGUI();
		initGame();
		
		pack();
		setVisible(true);
	}
	
	private void initGUI()
	{
		JPanel boardPnl = new JPanel(new GridLayout(SIZE, SIZE));
		boardBtns = new Block[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				boardBtns[i][j] = new Block();
				boardPnl.add(boardBtns[i][j]);
			}
		}
		add(boardPnl, BorderLayout.CENTER);
		
		JPanel sidePnl = new JPanel();
		sidePnl.setLayout(new BoxLayout(sidePnl, BoxLayout.Y_AXIS));
		sidePnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel scorePnl = new JPanel();
		JLabel scoreLbl = new JLabel("Score: ");
		scorePnl.add(scoreLbl);
		scorePtsLbl = new JLabel("0");
		scorePnl.add(scorePtsLbl);
		sidePnl.add(scorePnl);
		
		wordListField = new JTextArea(18, 16);
		wordListField.setEditable(false);
		wordListField.setFont(TEXT_FONT);
		JScrollPane scrollPane = new JScrollPane(wordListField);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sidePnl.add(scrollPane);
		
		wordEntry = new JTextField(16);
		wordEntry.setFont(TEXT_FONT);
		wordEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String word = wordEntry.getText().toUpperCase().trim();
				if (wordOnBoard && isValidWord(word) && !wordList.contains(word))
				{
					addWordToList(word);
					wordEntry.setText("");
				}
			}
		});
		wordEntry.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event)
			{
				clear();
				String word = wordEntry.getText().trim();
				if (word.length() > 0)
					wordOnBoard = findWordInBoard(word.toUpperCase());
				else
					wordOnBoard = false;
			}
		});
		sidePnl.add(wordEntry);
		
		add(sidePnl, BorderLayout.LINE_END);
	}
	
	private boolean findWordInBoard(String word)
	{
		char firstLtr = word.charAt(0);
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				if (board[i][j] == firstLtr)
				{
					if (word.length() == 1 || findWordInBoard(word.substring(1), new boolean[SIZE][SIZE], i, j))
					{
						boardBtns[i][j].highlight();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean findWordInBoard(String word, boolean[][] checked, int x, int y)
	{
		checked[x][y] = true;
		char ltr = word.charAt(0);
		for (int dx = -1; dx <= 1; dx++)
		{
			for (int dy = -1; dy <= 1; dy++)
			{
				if (dx == 0 && dy == 0)
					continue;
				int newX = x + dx;
				int newY = y + dy;
				if (newX >= 0 && newY >= 0 && newX < SIZE && newY < SIZE && !checked[newX][newY])
				{
					if (board[newX][newY] == ltr)
					{
						if (word.length() > 1)
						{
							if (findWordInBoard(word.substring(1), checked, newX, newY))
							{
								boardBtns[newX][newY].highlight();
								return true;
							}
						}
						else
						{
							boardBtns[newX][newY].highlight();
							return true;
						}
					}
				}
			}
		}
		checked[x][y] = false;
		return false;
	}
	
	private void clear()
	{
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				boardBtns[i][j].unHighlight();
	}
	
	private void loadBlocks()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(BLOCKS_FILE));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null)
			{
				blocks[i] = line;
				i++;
			}
		} catch (Exception ex) { }
	}
	
	private void initGame()
	{
		wordList = new ArrayList<String>();
		score = 0;
		scorePtsLbl.setText(Integer.toString(score));
		
		generateBoard();
		SOLVER.solveBoard(board);
		displayBoard();
	}
	
	private void generateBoard()
	{
		ArrayList<Point> posns = new ArrayList<Point>();
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				posns.add(new Point(i, j));
		
		board = new char[SIZE][SIZE];
		for (String block : blocks)
		{
			Point pos = posns.remove((int)(Math.random() * posns.size()));
			board[pos.x][pos.y] = block.charAt((int)(Math.random() * block.length()));
		}
	}
	
	private void displayBoard()
	{
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board.length; j++)
			{
				String ltr = Character.toString(board[i][j]);
				if (board[i][j] == 'Q')
					ltr += 'u';
				boardBtns[i][j].setText(ltr);
			}
		}
	}
	
	private static boolean isValidWord(String word)
	{
		if (word.length() < 3)
			return false;
		return BoggleSolver.isInDictionary(word);
	}
	
	private void addWordToList(String word)
	{
		word = word.toUpperCase();
		wordList.add(word);
		
		int wordScore = BoggleSolver.getWordScore(word);
		score += wordScore;
		scorePtsLbl.setText(Integer.toString(score));
		wordListField.append(word + " (" + wordScore + ")\n");
	}
	
	private static class Block extends JLabel
	{
		private static final int BLOCK_SIZE = 100;
		
		private static final Font BLOCK_FONT = new Font("Arial", Font.BOLD, 28);
		private static final Border BLOCK_BORDER = BorderFactory.createLineBorder(Color.BLACK);
		
		private static final Color HIGHLIGHT = Color.YELLOW;
		private static final Color BG = Color.WHITE;
		
		public Block()
		{
			super(" ");
			setPreferredSize(new Dimension(BLOCK_SIZE, BLOCK_SIZE));
			setBackground(BG);
			setOpaque(true);
			setBorder(BLOCK_BORDER);
			setFont(BLOCK_FONT);
			setAlignmentY(CENTER_ALIGNMENT);
			setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		public void highlight()
		{
			setBackground(HIGHLIGHT);
		}
		
		public void unHighlight()
		{
			setBackground(BG);
		}
	}
	
	public static void main(String[] args)
	{
		new Boggle();
	}
}
