package it.dstech.connessionedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ConnessioneDatabase {

	static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver"); // in questo punto carichia nella JVM in esecuzione la nostra libreria 
		String password ="Pirandello1@"; // la vostra password
		String username = "root"; // la vostra username
		String url = "jdbc:mysql://localhost:3306/world?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
		
		Connection connessione = DriverManager.getConnection(url, username, password);
		Statement statement = connessione.createStatement();
		
		
		while(true) {
		menu();
		int scelta=input.nextInt();
		input.nextLine();
		
		
			switch (scelta) {
				case 1:
					System.out.println("Inserisci il country-code della nazione di cui vuoi conoscere le città");
					String countrycode= input.nextLine();
					
					ResultSet risultatoQuery = statement.executeQuery("select * from city where CountryCode =\""+countrycode+ "\" ;");
					while(risultatoQuery.next()) {
						int id = risultatoQuery.getInt(1);
						String nome = risultatoQuery.getString("Name");
						int pop = risultatoQuery.getInt(5);
						System.out.println(id + " " + nome + " - " + pop);
					}
					break;
				case 2:
					System.out.println("Inserisci il nome della nazione di cui vuoi conoscere la città più popolosa");
					String nomeNazione= input.nextLine();
					ResultSet query = statement.executeQuery("select * from country where Name =\""+nomeNazione+ "\" ;");
					String nazione= "";
					while(query.next()) {
						nazione = query.getString(1);
					}
					
					ResultSet risultatoQuery2 = statement.executeQuery("select * from city where CountryCode =\""+nazione+ "\" ;");
					
					int popMax=0;
					String nome= "";
					while(risultatoQuery2.next()) {
						int pop = risultatoQuery2.getInt(5);
						
						if(popMax<pop) {
							nome = risultatoQuery2.getString("Name");
							popMax=pop;
						}
					}
					System.out.println("La città più popolosa in " +nomeNazione+" è: " +nome + " - " + popMax);
					break;
				case 3:
					
					System.out.println("Inserisci la forma di governo di cui vuoi conoscere lo stato con estensione maggiore");
					String form= input.nextLine();
					ResultSet queryTerritorio = statement.executeQuery("select * from country where GovernmentForm =\""+form+ "\" ;");
					String formaDiGoverno= "";
					
					int estMax=0;
					String territorioMaggiore= "";
					while(queryTerritorio.next()) {
						int est = queryTerritorio.getInt(5);
						
						if(estMax<est) {
							territorioMaggiore = queryTerritorio.getString("Name");
							estMax=est;
						}
					}
					System.out.println("La nazione più estesa per la forma di governo " +formaDiGoverno+" è: " +territorioMaggiore + " - " + estMax);
					
					break;
				case 0:
					System.exit(0);
					break;
			}
		}
		
	}
	
	public static void menu() {
		System.out.println("Cosa vuoi fare?");
		System.out.println("1. Stampa le città dato il countrycode");
		System.out.println("2. Trova la città più popolosa data la nazione");
		System.out.println("3. Trova lo stato più esteso data la forma di governo");
		System.out.println("0. Esci");
	}
	
}