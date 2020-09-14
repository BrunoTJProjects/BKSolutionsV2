package modelo;

import java.sql.SQLException;

import org.json.JSONObject;

import conexaobd.BKHardwareDAO;

public class Hardware {

	private String mac;
	private Modelo modelo;
	private BKHardwareDAO hardwareDAO;

	public Hardware(String mac, Modelo modelo) {
		this.mac = mac;
		this.modelo = modelo;
		hardwareDAO = new BKHardwareDAO();		
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public synchronized JSONObject getChaves() throws SQLException, ClassNotFoundException {
		JSONObject dados = hardwareDAO.getChaves(this);
		return dados;
	}
	
	public synchronized JSONObject getChaves(String nada) throws SQLException, ClassNotFoundException {
		JSONObject dados = hardwareDAO.getChaves(this);
		return dados;
	}

	public synchronized int setChaves(JSONObject jsonObj) throws ClassNotFoundException, SQLException {
		int linhasafetadas = hardwareDAO.setChaves(this, jsonObj);
		return linhasafetadas;
	}

	public synchronized String getChave(String chave) throws SQLException, ClassNotFoundException {
		String valor = hardwareDAO.getChave(this, chave);
		return valor;
	}

	public synchronized int setChave(String chave, String valor) throws ClassNotFoundException, SQLException {
		int linhasafetadas = hardwareDAO.setChave(this, chave, valor);
		return linhasafetadas;
	}
	
	public synchronized int getCliente() throws SQLException, ClassNotFoundException {
		int dados = hardwareDAO.getCliente(this);
		return dados;
	}

	public String toString() {
		String info = null;
		info = "MAC: " + mac + " MODELO: " + modelo.getNome();
		return info;
	}
}