package br.com.bksolutionsdomotica.servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import br.com.bksolutionsdomotica.modelo.Cliente;
import br.com.bksolutionsdomotica.modelo.SocketBase;

public class SocketCliente extends Cliente,{
	
	private Cliente cliente;
//	private ObjectInputStream inObj;
//	private ObjectOutputStream outObj;

	public SocketCliente(Socket socket) throws IOException {
		super(socket);		
	}





	public void closeResouces() throws IOException {
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}
		if (socket != null) {
			socket.close();
		}
	}

	public void sendCommand(String comando) throws IOException {
		if (comando != null && !comando.isEmpty()) {
			if (out != null) {
				out.write(comando);
				out.flush();
			}
		}
	}

	public String commandReceiver() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		if (in != null && in.ready()) {
			while (in.ready()) {
				int retorno = in.read();
				stringBuilder.append(Character.toChars(retorno));
			}
		}
		return stringBuilder.toString();
	}

//	public void sendObject(Object obj) throws IOException {
//		if (obj != null) {
//				outObj = new ObjectOutputStream(socket.getOutputStream());
//				outObj.writeObject(obj);
//				outObj.flush();
//				outObj = null;
//			
//		}
//	}

//	public Object objectReceiver() throws IOException, ClassNotFoundException {
//		Object obj = null;
//		if (inObj != null) {
//			while (inObj.available() > -1) {
//				obj = inObj.readObject();
//			}
//		}
//		return obj;
//	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public boolean isHarware() {
		return isHarware;
	}

	public void setHarware(boolean isHarware) {
		this.isHarware = isHarware;
	}

}