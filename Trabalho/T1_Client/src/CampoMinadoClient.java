import java.rmi.Naming;
import java.rmi.RemoteException;

import t1common.CampoMinadoInterface;
import java.util.Scanner;

public class CampoMinadoClient {
	
	static char[][] m_gameBoard;
	static int      m_playerId;
	
	public static void main (String[] args) {
		
	    Scanner scanner = new Scanner(System.in);
		
		//System.out.println("Digite o endereco da maquina seguido de <ENTER>...");

		//String endereco = scanner.next();
		
		System.out.println("Digite o seu nome para comecar a jogar seguido de <ENTER>...");
		
		String nome = scanner.next();
		
		try {
			//CampoMinadoInterface jogo = (CampoMinadoInterface) Naming.lookup ("//"+endereco+"/CampoMinado");
			CampoMinadoInterface jogo = (CampoMinadoInterface) Naming.lookup ("//localhost/CampoMinado");
			
			m_playerId = jogo.registraJogador(nome);
			
			if(m_playerId == -1)
			{
				System.out.println("Nome de usuário já existe!");
				System.exit(1);
			}
			
			m_gameBoard = jogo.obtemTabuleiro(m_playerId);
			
			boolean exit = false;
			
			while(true)
			{
				PrintBoard();
				
				System.out.println("Escolha uma opção: ");
				
				System.out.println("a) Abrir posição ");
				System.out.println("m) Marcar bomba ");
				System.out.println("s) Sair ");
				
				while(true)
				{
					String action = ReadInput(m_playerId, jogo, scanner);
					
					if(action.equals("a") || action.equals("m") || action.equals("s"))
					{
						switch(action)
						{
							case "a":
							{
								boolean joga = false;
								int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE;
								
								while(true)
								{
									if(x == Integer.MIN_VALUE && y == Integer.MIN_VALUE)
										System.out.println("Digite a posicao desejada em 'x' e 'y' -ou- Digite 'v' para voltar...");
									
									String line = ReadInput(m_playerId, jogo, scanner);
									boolean error = false;
									
									if(line.equals("v"))
										break;

									else
									{
										if(x == Integer.MIN_VALUE)
										{
											try {
											      x = Integer.parseInt(line);
											} catch (NumberFormatException e) {
											      error = true;
											}
										}
										else if(y == Integer.MIN_VALUE)
										{
											try {
											      y = Integer.parseInt(line);
											} catch (NumberFormatException e) {
											      error = true;
											}
										}
										if(!error && x != Integer.MIN_VALUE && y != Integer.MIN_VALUE)
										{
											if((x >= m_gameBoard.length)||(y >= m_gameBoard.length)||(x < 0)||(y < 0))
												error = true;
											else
											{
												joga = true;
												break;
											}
										}
									}
									if(error)
										System.out.println("Erro: Digite corretamente os dados!");
								}
								if(joga)
								{
									int result  = jogo.enviaJogada(m_playerId, x, y);
									if(result == -7)
									{
										String errorMessage = jogo.getErrorMessage(m_playerId);
										System.out.println("Erro: " + errorMessage);
										System.exit(1);
									}
									if(result == -2)
										System.out.println("Erro: Posicao ja marcada!");
									if(result == -1)
										System.out.println("Erro: Posicao ja aberta!");
									if(result == 0)
									{
										System.out.println("BOOOM!!!!!!");
										System.out.println("Fim de Jogo.");
									}
									m_gameBoard = jogo.obtemTabuleiro(m_playerId);
									if(m_gameBoard == null)
									{
										String errorMessage = jogo.getErrorMessage(m_playerId);
										System.out.println("Erro: " + errorMessage);
										System.exit(1);
									}
								}
							}
							break;
							case "m":
							{
							
							}
							break;
							case "s":
							{
								exit = true;
							}
							break;
						}
						break;
					}
					else
					{
						System.out.println("Opção inválida, digite uma das opções corretas!");
					}
				}
				
				if(exit)
					break;
				
			}
			
			System.out.println("Obrigado por jogar! Até a próxima!");
			
			//n = nota.obtemNota(args[1]);
			//System.out.println ("Nome: "+args[1]);
			//if	(n<-1.0)
			//	System.out.println ("Resultado: nome nao encontrado!\n");
			//else
			//	System.out.println ("Nota: "+n);
			
		} catch (Exception e) {
			System.out.println ("CampoMinadoClient failed.");
			e.printStackTrace();
		}
	}
	
	public static String ReadInput(int id, CampoMinadoInterface jogo, Scanner scanner)
	{
		String result = scanner.next();
		boolean alive = true;
		try {
			alive = jogo.keepAlive(id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!alive)
		{
			String errorMessage = "Error: Disconnected from the server!";
			try {
				errorMessage = jogo.getErrorMessage(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(errorMessage);
			System.exit(0);
		}
		return result;
	}
	
	public static void PrintBoard()
	{
		System.out.println("------------ Tabuleiro ---------------");
		
		if(m_gameBoard.equals(null))
		{
			System.out.println("Error: Gameboard is null! :(");
			return;
		}
		System.out.println("    ---------------------------");
		for(int y = 0; y < m_gameBoard.length; y++)
		{
			String line = "     | ";
			for(int x = 0; x < m_gameBoard[0].length; x++)
			{
				line += m_gameBoard[y][x] + " | ";
			}
			System.out.println(line);
			System.out.println("    ---------------------------");
		}
	}
}
