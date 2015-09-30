package t1common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

class Player
{
	public String m_name;
	public int 	  m_id;
	
	public Player (String name, int id)
	{
		m_name = name;
		m_id   = id;
	}
}

enum GameState  { Free, Playing }

class Game
{	
	final  int BOARD_SIZE = 5;
	public Player m_owner;
	public GameState m_gameState;
	public int[][] m_board;
	public int[][] m_boardMask;
	
	public Game()
	{
		m_owner     = null;
		m_gameState = GameState.Free;
	}
	
	public void StartNewGame()
	{
		m_gameState = GameState.Playing;
		
		CreateBoard();
		
		//PrintBoard(m_board);
	}
	
	private void CreateBoard()
	{
		m_board     = new int[BOARD_SIZE][BOARD_SIZE];
		m_boardMask = new int[BOARD_SIZE][BOARD_SIZE];
		
		//sort bombs
		int numBombs = 0, bombX = 0, bombY = 0;
		Random rand = new Random();
		while(numBombs < 5)
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
	
	private static final long serialVersionUID = -513804057617910473L;
	
	public CampoMinado() throws RemoteException 
	{
		m_games = new ArrayList<Game>();
		m_playerCode = 0;
		for(int i = 0; i < MAX_PARTIDAS; i++)
		{
			m_games.add(new Game());
		}
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
			throw new RemoteException("Game is Null! :(");
		
		freeGame.m_owner = newPlayer;

		freeGame.StartNewGame();
		
		return newPlayer.m_id;
	}

	@Override
	public int enviaJogada(int id, int linha, int coluna) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
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
			throw new RemoteException("Cannot find player game! :(");
		
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
						content = (char)board[y][x];
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

}
