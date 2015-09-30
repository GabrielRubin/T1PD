import java.rmi.Naming;
import java.rmi.RemoteException;
import t1common.*;

public class CampoMinadoServer {
	
	public static void main (String[] args) {
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry ready.");			
		} catch (RemoteException e) {
			System.out.println("RMI registry already running.");			
		}
		try {
			Naming.rebind("CampoMinado", new CampoMinado());
			System.out.println ("NotasServer is ready.");
		} catch (Exception e) {
			System.out.println ("NotasServer failed:");
			e.printStackTrace();
		}
	}
	
	
}
