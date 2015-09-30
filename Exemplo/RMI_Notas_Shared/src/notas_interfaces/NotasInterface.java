package notas_interfaces;
// Arquivo: NotasInterface.java (Roland Teodorowitsch; 28 ago. 2013)
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotasInterface extends Remote {
	public double obtemNota(String nome) throws RemoteException;
	
	void mudaNota(String nome) throws RemoteException;
}

