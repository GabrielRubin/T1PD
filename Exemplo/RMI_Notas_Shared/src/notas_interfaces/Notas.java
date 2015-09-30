package notas_interfaces;
// Arquivo: Notas.java (Roland Teodorowitsch; 28 ago. 2013)
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class Aluno {  
	public String nome;
	public double nota;

	public Aluno(String  nome,double nota) {
		this.nome = nome;
		this.nota = nota;
	}
}

public class Notas extends UnicastRemoteObject implements NotasInterface {

	private static final long serialVersionUID = -513804057617910473L;
	private Aluno[] alunos;

	public Notas () throws RemoteException {
		 this.alunos = new Aluno[]{
				 new Aluno("Alexandre", 9.5),
				 new Aluno("Barbara",   8.5),
				 new Aluno("Joao",      6.5),
				 new Aluno("Maria",     9.0),
				 new Aluno("Paulo",    10.0),
				 new Aluno("Pedro",     7.0)
		 };
	}

	public double obtemNota(String nome) throws RemoteException {
		for	(int i=0;i<alunos.length;++i)
		{
			if      (alunos[i].nome.equals(nome))
			{
				mudaNota(nome);
				return alunos[i].nota;
			}
		}
		return -1.0;
	}

	@Override
	public void mudaNota(String nome) throws RemoteException {
		// TODO Auto-generated method stub
		for	(int i=0;i<alunos.length;++i)
		{
			if      (alunos[i].nome.equals(nome))
			{
				alunos[i].nota += 0.5;
			}
		}
	}

}

