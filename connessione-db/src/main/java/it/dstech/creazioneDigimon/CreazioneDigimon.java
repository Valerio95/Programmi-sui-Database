package it.dstech.creazioneDigimon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CreazioneDigimon {
	static Scanner scanner=new Scanner(System.in);
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String password ="SWCTvf0TtX";
		String username = "J2bCsBdKMg";
		String url = "jdbc:mysql://remotemysql.com:3306/J2bCsBdKMg?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
		Connection connessione = DriverManager.getConnection(url, username, password);
		
		
		while(true) {
			menu();
			int scelta=scanner.nextInt();
			scanner.nextLine();
			
			switch (scelta) {
			case 1:
				creazioneDigimon(connessione);	
				break;
			case 2:
				rimozioneDigimon(connessione);
				break;
			case 3:
				giocaPartita(connessione);
				break;
			case 0:
				System.exit(0);
				break;
			}
		}
	}
	
	
	public static void giocaPartita(Connection connessione) throws SQLException {
			int rigaPrecedente = creaPartita(connessione);
			
			while(true) {
			System.out.println("Attendere che lo sfidante abbia inserito i suoi Digimon nella partita, poi premere 1");
		
			int avvio = scanner.nextInt();
			scanner.nextLine();
			int nuovaRiga=conteggioRiga(connessione);
			if(avvio==1 && nuovaRiga==rigaPrecedente) {
				System.out.println("Il tuo sfidante non ha ancora scelto i suoi Digimon");
			} else if(avvio==1 && nuovaRiga>rigaPrecedente) {
				giocaArena(connessione);
			}
		}
	}
	
	
	public static int conteggioRiga(Connection connessione) throws SQLException {
		PreparedStatement controlloAvvio = connessione.prepareStatement("select idPartita from Partita;");
		ResultSet execute = controlloAvvio.executeQuery();
		int numeroRiga = 0;
		while(execute.next()) {
			if(numeroRiga<execute.getRow()) {
				numeroRiga=execute.getRow();
			}
		}
		return numeroRiga;
	}
	
	
	public static void giocaArena(Connection connessione){
	}
	
	
	public static int creaPartita(Connection connessione) throws SQLException {
		String queryCreazionePartita= "INSERT INTO Partita ( idCreatore, password, dc1, dc2, dc3) VALUES (?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryCreazionePartita);
		System.out.println("Inserisci il tuo id");
		int idPrimo= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci la password della partita");
		String password= scanner.nextLine();

		PreparedStatement sceltaDigimon = connessione.prepareStatement("select * from Digimon where idUtente =\"" +idPrimo+"\";");
		ResultSet listaDigimon = sceltaDigimon.executeQuery();
		System.out.println("Ecco i digimon che hai a disposizione: ");
		stampa(listaDigimon);		
		
		String[] digimonSelezionati=selezionaDigimon(connessione);
		
		prepareStatement.setInt(1, idPrimo);
		prepareStatement.setString(2, password);
		prepareStatement.setString(3, digimonSelezionati[0]);
		prepareStatement.setString(4, digimonSelezionati[1]);
		prepareStatement.setString(5, digimonSelezionati[2]);
		prepareStatement.execute();
		
		int riga =conteggioRiga(connessione);
		return riga;
	}
	
	
	public static String[] selezionaDigimon(Connection connessione) throws SQLException {
		System.out.println("Inserisci l'id del primo Digimon da selezionare");
		int idDC1= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci l'id del secondo Digimon da selezionare");
		int idDC2= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci l'id del terzo Digimon da selezionare");
		int idDC3= scanner.nextInt();
		scanner.nextLine();
		
		String nome1="", nome2="", nome3="";
		String[] nomiSelezionati = new String[3];
		PreparedStatement selezioneDigimon = connessione.prepareStatement("select nome from Digimon where id =\""+idDC1+"\";");
		ResultSet executeSelezionata = selezioneDigimon.executeQuery();
		while(executeSelezionata.next()) {
			nome1 = executeSelezionata.getString("nome");
		}
		PreparedStatement selezioneDigimon2 = connessione.prepareStatement("select nome from Digimon where id =\""+idDC2+"\";");
		ResultSet executeSelezionata2 = selezioneDigimon2.executeQuery();
		while(executeSelezionata2.next()) {
			nome2 = executeSelezionata2.getString("nome");
		}
		PreparedStatement selezioneDigimon3 = connessione.prepareStatement("select nome from Digimon where id =\""+idDC3+"\";");
		ResultSet executeSelezionata3 = selezioneDigimon3.executeQuery();
		while(executeSelezionata3.next()) {
			nome3 = executeSelezionata3.getString("nome");
		}
			nomiSelezionati[0] = nome1;
			nomiSelezionati[1] = nome2;
			nomiSelezionati[2] = nome3;
			
		return nomiSelezionati;
	}
	
	
	public static void menu() {
		System.out.println("Cosa vuoi fare?");
		System.out.println("1. Crea un Digimon nel database");
		System.out.println("2. Rimuovi un Digimon dal database");
		System.out.println("3. Gioca partita");
		System.out.println("0. Esci");
	}
	
	
	private static void creazioneDigimon(Connection connessione) throws SQLException {
		String queryInserimentoDigimon= "INSERT INTO Digimon (nome, HP, ATK, DEF, RES, EVO, idUtente, tipo) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryInserimentoDigimon);
		System.out.println("Inserisci il nome");
		prepareStatement.setString(1, scanner.nextLine());
		prepareStatement.setInt(2, generaHP());
		prepareStatement.setInt(3, generaAttacco());
		prepareStatement.setInt(4, generaDifesa());
		prepareStatement.setInt(5, generaResistenza());
		
		System.out.println("Inserisci lo stadio evolutivo");
		prepareStatement.setString(6, scanner.nextLine());
		System.out.println("Inserisci l'id dell'utente");
		prepareStatement.setString(7, scanner.nextLine());
		System.out.println("Inserisci il tipo del Digimon");
		prepareStatement.setString(8, scanner.nextLine());
		prepareStatement.execute();
	}


	private static void rimozioneDigimon(Connection connessione) throws SQLException {
		PreparedStatement prepareStatement = connessione.prepareStatement("select * from Digimon;");
		ResultSet executeQuery = prepareStatement.executeQuery();
		System.out.println("Ecco i digimon nel database: ");
	
		stampa(executeQuery);
		
		String queryRimozioneDigimon= "DELETE FROM Digimon WHERE id= ?";
		PreparedStatement prepareStatement2 = connessione.prepareStatement(queryRimozioneDigimon);
		System.out.println("Inserisci l'id del Digimon da rimuovere");
		prepareStatement2.setInt(1, scanner.nextInt());
		prepareStatement2.execute();
		System.out.println("Digimon eliminato");
	}
	
	
	public static void stampa(ResultSet executeQuery) throws SQLException {
		while (executeQuery.next()) {
			int id =executeQuery.getInt(1);
			String nome=executeQuery.getString("nome");
			int hp = executeQuery.getInt(3);
			int atk = executeQuery.getInt(4);
			int def = executeQuery.getInt(5);
			int res = executeQuery.getInt(6);
			String evo = executeQuery.getString(7);
			int idUtente = executeQuery.getInt(8);
			String tipo = executeQuery.getString(9);
			System.out.println(id + " " + nome + " " + hp + " " + atk + " " + def + " " + res + " " + evo + " " + idUtente + " " + tipo);
		
		}
	}
	
	
	public static int generaAttacco() {
		int attacco = 0;
		boolean condizione= true;
		while(condizione) {
			attacco = (int) (Math.random()*151);
			if(attacco>100) {
				condizione=false;
			}
		}
		return attacco;
	}
	
	
	public static int generaDifesa() {
		int difesa = 0;
		boolean condizione= true;
		while(condizione) {
			difesa = (int) (Math.random()*31);
			if(difesa>10) {
				condizione=false;
			}
		}
		return difesa;
	}
	
	
	public static int generaResistenza() {
		int resistenza = 0;
		boolean condizione= true;
		while(condizione) {
			resistenza = (int) (Math.random()*11);
			if(resistenza>5) {
				condizione=false;
			}
		}
		return resistenza;
	}
	
	
	public static int generaHP() {
		int HP = 0;
		boolean condizione= true;
		while(condizione) {
			HP = (int) (Math.random()*1600);
			if(HP>1000) {
				condizione=false;
			}
		}
		return HP;
	}
}
