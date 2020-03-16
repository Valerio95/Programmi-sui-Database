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
			creaPartita(connessione);
	
			while(true) {
			System.out.println("Attendere che lo sfidante abbia inserito i suoi Digimon nella partita, poi premere 1");
			int avvio = scanner.nextInt();
			scanner.nextLine();
			if(avvio==1 && controlloSfidante(connessione)) {
				giocaArena(connessione);
			}
		}
	}
	
	
	public static boolean controlloSfidante(Connection connessione) throws SQLException {
		PreparedStatement controlloAvvio = connessione.prepareStatement("select * from Partita;");
		ResultSet execute = controlloAvvio.executeQuery();
		
		while(execute.next()) {
			if(execute.getBoolean("idSfidante") && execute.getBoolean("ds1") && execute.getBoolean("ds2") && execute.getBoolean("ds3")) {
				return true;
			} 
		} return false;
	}
	
	
	public static void giocaArena(Connection connessione) throws SQLException{
		
			generaTurno(selezionaRandom(connessione));
		
	}	
	
	
	public static void generaTurno(int idDigimon) {
		
		
	}
	
	
	public static int selezionaRandom(Connection connessione) throws SQLException {
		PreparedStatement codicePartita = connessione.prepareStatement("select MAX(idPartita) from Partita;");
		ResultSet execute = codicePartita.executeQuery();
		int id=0;
		while(execute.next()) {
			id=execute.getInt("idPartita");
		}
		PreparedStatement squadraDigimon = connessione.prepareStatement("select * from Partita where id =?;");
		squadraDigimon.setInt(1, id);
		ResultSet executeSquadra = squadraDigimon.executeQuery();
		int[] squadra = new int[3];
		int i=0;
		while(executeSquadra.next()) {
			squadra[i]=executeSquadra.getInt("dc"+(i+1));
			i++;
		}
		int idDigimonScelto = squadra[(int) (Math.random()*3)];
		return idDigimonScelto;
	}
	
	
	
	public static void creaPartita(Connection connessione) throws SQLException {
		String queryCreazionePartita= "INSERT INTO Partita ( idCreatore, password, dc1, dc2, dc3) VALUES (?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryCreazionePartita);
		System.out.println("Inserisci il tuo id");
		int idPrimo= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci la password della partita");
		String password= scanner.nextLine();

		PreparedStatement sceltaDigimon = connessione.prepareStatement("select * from Digimon where idUtente =?;");
		sceltaDigimon.setInt(1, idPrimo);
		ResultSet listaDigimon = sceltaDigimon.executeQuery();
		System.out.println("Ecco i digimon che hai a disposizione: ");
		stampa(listaDigimon);		
		
		int[] digimonSelezionati=selezionaDigimon(connessione);
		
		prepareStatement.setInt(1, idPrimo);
		prepareStatement.setString(2, password);
		prepareStatement.setInt(3, digimonSelezionati[0]);
		prepareStatement.setInt(4, digimonSelezionati[1]);
		prepareStatement.setInt(5, digimonSelezionati[2]);
		prepareStatement.execute();
	}
	
	
	public static int[] selezionaDigimon(Connection connessione) throws SQLException {
		
		int[] idSelezionati = new int[3];	
		System.out.println("Inserisci l'id del primo Digimon da selezionare");
		idSelezionati[0]= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci l'id del secondo Digimon da selezionare");
		idSelezionati[1]= scanner.nextInt();
		scanner.nextLine();
		System.out.println("Inserisci l'id del terzo Digimon da selezionare");
		idSelezionati[2]= scanner.nextInt();
		scanner.nextLine();
		
			return idSelezionati;
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
