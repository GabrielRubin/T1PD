import java.rmi.Naming;
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
					String action = scanner.next();
					
					if(action.equals("a") || action.equals("m") || action.equals("s"))
					{
						switch(action)
						{
							case "a":
							{
								
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
	
	public static void PrintBoard()
	{
		System.out.println("------------ Tabuleiro ---------------");
		
		if(m_gameBoard.equals(null))
		{
			System.out.println("Error: Gameboard is null! :(");
			return;
		}
		System.out.println("   ---------------------------");
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
