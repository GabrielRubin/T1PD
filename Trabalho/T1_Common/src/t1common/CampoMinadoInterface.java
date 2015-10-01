package t1common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CampoMinadoInterface extends Remote {
    
    public int registraJogador(String nome) throws RemoteException;
    // -1 Jogador já registrado
    // -2 Numero maximo de partidas atingido!
    // -3 ERRO: Game = NULL!
    
    public char[][] obtemTabuleiro(int id) throws RemoteException;
    //"Tabuleiro" pode ser qualquer tipo:
    // vetor, classe, string, ...
    
    public int enviaJogada(int id, int linha, int coluna) throws RemoteException;
    // 2 = venceu a partida (revelou todas as casas)
    // 1 = jogada ok (mas ainda existem casas para serem reveladas)
    // 0 = jogou numa bomba
    // -1 = identificador invalido
    // -2 = jogada invalida
    // -3 = jogada já foi feita anteriormente
    
    public int marcaBomba(int id, int linha, int coluna) throws RemoteException;
    // 1 = jogada ok (...)
    // -1 = identificador invalido
    // -2 = jogada invalida
    // -3 = bomba já foi marcada anteriormente
    
    public int desmarcaBomba(int id, int linha, int coluna) throws RemoteException;
    // 1 = jogada ok (...)
    // -1 = identificador invalido
    // -2 = jogada invalida
    // -3 = bomba não foi marcada anteriormente
    
    public String getErrorMessage(int id) throws RemoteException;
    
    public boolean keepAlive(int id) throws RemoteException;

}