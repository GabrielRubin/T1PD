package t1common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.beans.finder.FieldFinder;

class Player
{
	public String m_name;
	public int 	  m_id;
	public String m_endOfGameMessage;
	
	public Player (String name, int id)
	{
		m_name = name;
		m_id   = id;
		m_endOfGameMessage = "None";
	}
}

enum GameState  { Free, Playing }

class Game
{	
	final int BOARD_SIZE 	  = 5;
	final int NUMBER_OF_BOMBS = 5;
	final int TIMER_DELAY     = 5000;
	final int TIMER_INTERVAL  = 1000;
	final int TIMER_MAXTIME   = 120;
	
	public CampoMinado m_mainGame;
	public Player m_owner;
	public GameState m_gameState;
	public int[][] m_board;
	public int[][] m_boardMask;
	public int     m_numberOfFreeSpaces;

	public Timer m_timer;
	public int   m_timerTotalTime;
	
	private class BoardPosition
	{
		public int m_y = 0;
		public int m_x = 0;
		
		public BoardPosition(int y, int x)
		{
			m_y = y;
			m_x = x;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			final BoardPosition other = (BoardPosition)obj;
			if((this.m_x == other.m_x) && (this.m_y == other.m_y))
				return true;
			return false;
		}
	}
	
	public Game(CampoMinado main)
	{
		m_mainGame  = main;
		m_owner     = null;
		m_gameState = GameState.Free;
	}
	
	public void StartNewGame()
	{
		m_gameState = GameState.Playing;
		
		CreateBoard();
		
		ResetTimer();
		
		m_numberOfFreeSpaces = (BOARD_SIZE * BOARD_SIZE) - NUMBER_OF_BOMBS;
		
		m_timer = new Timer();
		
		m_timer.scheduleAtFixedRate(new TimerTask()
				{
					public void run()
					{
						m_timerTotalTime++;
						
						if(m_timerTotalTime >= TIMER_MAXTIME)
						{
							m_owner.m_endOfGameMessage = "Timer exceeded! Player disconected";
							m_mainGame.TerminateGame(m_owner.m_id);
						}
					}
				},TIMER_DELAY, TIMER_INTERVAL);
		
		PrintBoard(m_board);
	}
	
	public void GameOver()
	{
		m_owner     = null;
		m_gameState = GameState.Free;
		m_timer.cancel();
		m_timer     = null;
	}
	
	public void ResetTimer()
	{
		m_timerTotalTime = 0;
		
		System.out.println("timer reset!");
	}
	
	public int OpenBoard(int y, int x)
	{
		ResetTimer();
		
		if(m_boardMask[y][x] == -1) //posicao ja aberta
			return -1;
		if(m_boardMask[y][x] == 1)  //posicao marcada
			return -2;
		
		if(m_board[y][x] == -1)     //BOOM
		{
			m_boardMask[y][x] = -1;
			return 0;
		}
		if(m_board[y][x] > 0)       //Quase
		{
			m_boardMask[y][x] = -1;
			m_numberOfFreeSpaces--;
			return 1;
		}
		
		FloodOpenBoard(y, x);       //Vazio
		
		return 1;
	}
	
	private void CreateBoard()
	{
		m_board     = new int[BOARD_SIZE][BOARD_SIZE];
		m_boardMask = new int[BOARD_SIZE][BOARD_SIZE];
		
		//sort bombs
		int numBombs = 0, bombX = 0, bombY = 0;
		Random rand = new Random();
		while(numBombs < NUMBER_OF_BOMBS)
		{
			bombX = rand.nextInt(BOARD_SIZE);
			bombY = rand.nextInt(BOARD_SIZE);
			if(m_board[bombY][bombX] == -1)
				continue;
			m_board[bombY][bombX] = -1;
			numBombs++;
		}
		
		for(int y = 0; y < BOARD_SIZE; y++)
		{
			for(int x = 0; x < BOARD_SIZE; x++)
			{
				if(m_board[y][x] == -1)
					continue;
				m_board[y][x] = CheckNearBombs(y, x);
			}
		}
	}
	
	private int CheckNearBombs(int y, int x)
	{
		if(m_board == null)
			return 0;
		
		int numBombs = 0;
		
		for(int yy = -1; yy < 2; yy++)
		{
			for(int xx = -1; xx < 2; xx++)
			{
				int yyy = y + yy;
				int xxx = x + xx;
				
				if(yyy < 0 || xxx < 0 || yyy >= BOARD_SIZE || xxx >= BOARD_SIZE)
					continue;
				
				if(m_board[yyy][xxx] == -1)
					numBombs++;
			}
		}
		return numBombs;
	}
	
	private void FloodOpenBoard(int y, int x)
	{
		Queue<BoardPosition> openList   = new LinkedList<BoardPosition>();
		Queue<BoardPosition> closedList = new LinkedList<BoardPosition>();
		
		openList.add(new BoardPosition(y, x));
		
		while(!openList.isEmpty())
		{
			BoardPosition currentBoardPos = openList.remove();
			closedList.add(currentBoardPos);
			
			if(m_board[currentBoardPos.m_y][currentBoardPos.m_x] == -1)
				continue;
			
			m_boardMask[currentBoardPos.m_y][currentBoardPos.m_x] = -1;
			m_numberOfFreeSpaces--;
			
			if(m_board[currentBoardPos.m_y][currentBoardPos.m_x] > 0)
				continue;
			
			for(int yy = -1; yy < 2; yy++)
			{
				for(int xx = -1; xx < 2; xx++)
				{
					int yyy = currentBoardPos.m_y + yy;
					int xxx = currentBoardPos.m_x + xx;
					
					if(yyy < 0 || xxx < 0 || yyy >= BOARD_SIZE || xxx >= BOARD_SIZE)
						continue;
					
					BoardPosition boardPos = new BoardPosition(yyy, xxx);
					if(!openList.contains(boardPos) && !closedList.contains(boardPos))
						openList.add(boardPos);
				}
			}
		}
	}
	
	public static void PrintBoard(int[][] board)
	{
		System.out.println("---------------------------");
		for(int y = 0; y < board.length; y++)
		{
			String line = " | ";
			for(int x = 0; x < board[0].length; x++)
			{
				line += board[y][x] + " | ";
			}
			System.out.println(line);
			System.out.println("---------------------------");
		}
	}
}

public class CampoMinado extends UnicastRemoteObject implements CampoMinadoInterface 
{
	final   int MAX_PARTIDAS = 50;
	private int m_playerCode;
	
	private ArrayList<Game> m_games;
	public ArrayList<Player> m_playerCemetery;
	
	private static final long serialVersionUID = -513804057617910473L;
	
	public CampoMinado() throws RemoteException 
	{
		m_games = new ArrayList<Game>();
		m_playerCode = 0;
		for(int i = 0; i < MAX_PARTIDAS; i++)
		{
			m_games.add(new Game(this));
		}
		m_playerCemetery = new ArrayList<Player>();
	}
	
	@Override
	public int registraJogador(String nome) throws RemoteException 
	{
		if(CountGames() >= MAX_PARTIDAS)
			return -2;
		
		for(Game game : m_games)
		{
			if(game.m_gameState.equals(GameState.Playing) && game.m_owner.m_name.equals(nome))
				return -1;
		}
		
		Player newPlayer = new Player(nome, GetPlayerCode());
		
		Game freeGame = GetFreeGame();
		
		if(freeGame == null)
			throw new RemoteException("FreeGame is Null! :(");
		
		freeGame.m_owner = newPlayer;

		freeGame.StartNewGame();
		
		return newPlayer.m_id;
	}

	@Override
	public int enviaJogada(int id, int linha, int coluna) throws RemoteException {
		
		Game game = GetPlayerGame(id);
		
		if(game == null)
			return -7;
		
		int result = game.OpenBoard(coluna, linha);
		
		//BOMB EXPLODED
		if(result <= 0)
			return result;
		//WIN!
		if(game.m_numberOfFreeSpaces <= 0)
		{
			//DO STUFF:
			return 2;
		}
		
		return 1;
	}

	@Override
	public int marcaBomba(int id, int linha, int coluna) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int desmarcaBomba(int id, int linha, int coluna) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public char[][] obtemTabuleiro(int id) throws RemoteException 
	{
		Game playerGame = GetPlayerGame(id);
		
		if(playerGame == null)
			return null;
		
		return GetPrintableBoard(playerGame.m_board, playerGame.m_boardMask);
	}
	
	private synchronized char[][] GetPrintableBoard(int[][] board, int[][] boardMask)
	{
		char[][] printBoard = new char[board.length][board[0].length];
		
		for(int y = 0; y < board.length; y++)
		{
			for(int x = 0; x < board[0].length; x++)
			{
				char content = 'x';
				
				if(boardMask[y][x] == -1)
				{
					if(board[y][x] == -1 )
						content = 'b';
					else
					{
						String temp = "" + board[y][x];
						content = temp.charAt(0);
					}
				}
				else if(boardMask[y][x] == 1)
					content = 'm';
				
				printBoard[y][x] = content;
			}
		}
		
		return printBoard;
	}
	
	private synchronized Game GetPlayerGame(int playerId)
	{
		for(Game game : m_games)
		{
			if(game.m_gameState.equals(GameState.Playing) && game.m_owner.m_id == playerId)
				return game;
		}
		return null;
	}
	
	private synchronized Game GetFreeGame()
	{
		for(Game game : m_games)
		{
			if(game.m_gameState.equals(GameState.Free))
				return game;
		}
		return null;
	}
	
	private synchronized int CountGames()
	{
		int count = 0;
		for(Game game : m_games)
		{
			if(!(game.m_gameState.equals(GameState.Free)))
				count++;
		}
		return count;
	}
	
	private synchronized int GetPlayerCode()
	{
		return m_playerCode++;
	}
	
	public synchronized boolean TerminateGame(int playerId)
	{
		Game game = GetPlayerGame(playerId);
				
		if(game == null)
			return false;
		
		System.out.println("Game of " + game.m_owner.m_name + " is now over!");
		
		m_playerCemetery.add(game.m_owner);
		
		game.GameOver();
		
		return true;
	}

	@Override
	public String getErrorMessage(int id) throws RemoteException {
		
		for(Player p : m_playerCemetery)
			if(p.m_id == id)
				return p.m_endOfGameMessage;
		
		return "Erro de comunicacao!";
	}

	@Override
	public boolean keepAlive(int id) throws RemoteException {
		// TODO Auto-generated method stub
		Game game = GetPlayerGame(id);
		
		if (game == null)
			return false;
		
		game.ResetTimer();
		
		return true;
	}

}
