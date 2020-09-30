package br.com.bksolutionsdomotica.conexao;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import conexaobd.BKClienteDAO;
import modelo.Cliente;

public class ServerCoreBK {
	private int port;
	private boolean threadStart = true;
	private ServerSocket serverSocket;
	private volatile ServerCore serverCore;
	private volatile List<SocketCliente> socketClientes;

	public ServerCoreBK(int port) {
		this.port = port;
		serverCore = new ServerCore();
		socketClientes = new ArrayList<SocketCliente>();
	}

	public void init() throws IOException {

		serverCore.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = null;
				try {
					serverSocket = new ServerSocket(port);
					while (true) {
						socket = serverSocket.accept();
						onSocketConnected(socket);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Erro no Servidor > " + e.getMessage());
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();

		System.out.println("Servidor ouvindo na porta " + port);

	}

	public void restartThreadServer() {
		threadStart = true;
		if (!serverCore.isAlive()) {
			serverCore = null;
			serverCore = new ServerCore();
			serverCore.start();
		}
	}

	public void stopThread() {
		threadStart = false;
		while (serverCore.isAlive()) {

		}
		return;
	}

	public Cliente clienteLogado(String email, String senha) throws ClassNotFoundException, SQLException {
		Cliente cliente = null;
		BKClienteDAO clienteDAO = new BKClienteDAO();
		cliente = clienteDAO.logarCliente(email, senha);
		return cliente;
	}

	private void onSocketConnected(Socket socket) throws IOException {
		SocketCliente sc = new SocketCliente(socket);
		socketClientes.add(sc);
//		new TimeOut(null, socket).start();
		System.out.println("cliente connectado/ Total: " + socketClientes.size());
	}

	public void enviaComando(SocketCliente sc, String command) throws IOException {
		if (sc != null && command != null && !command.isEmpty()) {
			serverCore.comando = command;
			serverCore.sc = sc;
		} else {
			serverCore.comando = null;
			serverCore.sc = null;
		}
	}

	public void setInterfaceConnectionListener(InterfaceCommand interfaceCommand) {
		serverCore.setInterfaceConnectionListener(interfaceCommand);
	}

	public void removeSocketCliente(SocketCliente sc) throws IOException {
		sc.closeResouces();
		socketClientes.remove(sc);
	}

	private class ServerCore extends Thread {
		private String comando;
		private SocketCliente sc;
		private InterfaceCommand interfaceCommand;

		public ServerCore() {

		}

		@Override
		public void run() {
			while (threadStart) {

				List<SocketCliente> listaTemporaria = new ArrayList<SocketCliente>(socketClientes);

				for (SocketCliente sc : listaTemporaria) {
					try {
						runOnce(sc);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void runOnce(SocketCliente sc) throws ClassNotFoundException, SQLException {

			try {
				String string = sc.commandReceiver();
				
				if (string != null && !string.isEmpty()) {
					
					JSONObject jsonObject = new JSONObject(string);
					
					String tipoSolicitacao = (String) jsonObject.get("teste");
					
					switch(string) {
					case "LOGAR\r\n":
						sc.setCliente(interfaceCommand.onRequestSignIn(sc));
						break;
					case "DESLOGAR\r\n":
						interfaceCommand.onRequestSignOut(sc);
						break;
					case "DESCONECTAR\r\n":
						interfaceCommand.onRequestDisconnectSocket(sc);
						break;
					default:
						interfaceCommand.onCommandReceveived(sc, string);
					}
				}
				enviarComando(sc);

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		private void enviarComando(SocketCliente sc) throws IOException {
			if (comando != null && !comando.isEmpty()) {
				if (this.sc != null && sc != null) {
					if (this.sc.equals(sc)) {
						sc.sendCommand(comando);
						this.comando = null;
						this.sc = null;
					}
				}
			}
		}

		public void setInterfaceConnectionListener(InterfaceCommand interfaceCommand) {
			this.interfaceCommand = interfaceCommand;
		}

	}

	public interface InterfaceCommand {
		
		public Cliente onRequestSignIn(SocketCliente socketCliente) throws ClassNotFoundException, SQLException, IOException;
		public Cliente onRequestSignOut(SocketCliente socketCliente) throws ClassNotFoundException, SQLException, IOException;
		public void onRequestDisconnectSocket(SocketCliente socketCliente) throws IOException;
		public void onCommandReceveived(SocketCliente socketCliente, String stringRecebida) throws IOException;

	}
}