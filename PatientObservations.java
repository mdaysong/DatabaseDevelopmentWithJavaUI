

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/* I tested using (int hospitalCode = 3;) and the console printed the following: 
 * Select 1 to enter a patient's observation or select 2 to quit the program: 1
	Enter the patient observation: breathing fast
	Enter the patientId: 6
	Enter the date in the following format: YYYY-MM-DD 2020-03-10
	Enter the time in the following format: HH:MI:SS 10:00:00
	INSERT INTO Observation VALUES (21, 6, 3, 'breathing fast', '2020-03-10' , '10:00:00') 
	Observation was successfully inserted.
	Select 1 to enter a patient's observation or select 2 to quit the program: 
	...
	and the pattern continues */

/* Also when entering user input from keyboard, please do not include spaces*/

public class PatientObservations {
	
	private static final String url = "databaseURL";
	//insert the username here
	private static final String user = "";
	//insert the password here
	private static final String password = "";
	public static Connection con;
	public static Statement statement;
	
	//constructor registers a database manager
	public PatientObservations() throws SQLException {
		try {
			DriverManager.registerDriver ( new org.postgresql.Driver() ) ;
		} catch (Exception cnfe){
			System.out.println("Java Driver not found");
		}
		try {
			con = DriverManager.getConnection (url,user, password) ;
			statement = con.createStatement();
		} catch (SQLException egC) {
			System.out.println("Error: trouble getting the connection");
		}
	}
	
	public static void main(String args[]) throws SQLException {
		//open a data connection
		PatientObservations dbco = new PatientObservations();
		
		//accept argument from command line and then convert string to int
		String hospitalCodeString = args[0];
		int hospitalCode = Integer.parseInt(hospitalCodeString);
		
		//int hospitalCode = 3;
		
		//inputValue for testing while-loop conditions
		int inputValue = 0;
		//boolean for testing while-loop conditions
		boolean invalid = false;
		String patientObservation = "";
		String patientIDString = "";
		//create a scanner for input/output
		Scanner sc = new Scanner(System.in);
		//checks if the hospital id is valid
		if (checkHospitalID(hospitalCode) == true) {
			//while the inputValue is not equal to 2, where program would quit
			while (inputValue != 2) {
				System.out.print("Select 1 to enter a patient's observation or select 2 to quit the program: ");
				inputValue = Integer.parseInt(sc.nextLine());
				invalid = false;
				//while 1 is selected to enter a patient's observation
				while (invalid == false && inputValue == 1) {
					System.out.print("Enter the patient observation: ");
					//reads patient observation
					patientObservation = sc.nextLine();
					System.out.print("Enter the patientId: ");
					//reads patient id
					patientIDString = sc.nextLine();
					//converts patient id from string to int
					int patientID = Integer.parseInt(patientIDString);
					//checks if patient id is valid
					if (checkPatientID(patientID) == false) {
						System.out.println("The patientID does not exist.");
						inputValue = 0;
					} else {
						//if patient id is valid then, read the date and time
						System.out.print("Enter the date in the following format: YYYY-MM-DD ");
						String date = sc.nextLine();
						System.out.print("Enter the time in the following format: HH:MI:SS ");
						String time = sc.nextLine();
						//obtain the new observation id, just an incrementing sequence
						int oIdCount = dbco.checkObservationID();
						oIdCount++;
						//insert the query into the database
						dbco.insertValues(oIdCount, patientID, hospitalCode, patientObservation, date, time);
						invalid = true;
					}
				}
			}
			dbco.closeConnection();
		} else {
			//error message that hospital id is not valid
			System.out.println("HospitalID is not valid.");
		}
		//Program has been terminated and connection is closed
		System.out.println("Program has been quit.");
		dbco.closeConnection();
	}
	
	//method that checks if the hospital id is valid
	public static boolean checkHospitalID(int input){
		try {
		    String querySQL = "SELECT hId FROM HealthCarePersonnel WHERE hId = " + input;
		    ResultSet rs = statement.executeQuery(querySQL);
		    if (rs.next() == false) {
		    	return false;
		    } else {
		    	return true;
		    }
		} catch (SQLException e){
			int sqlCode = e.getErrorCode(); 
			String sqlState = e.getSQLState();
			System.out.println("System failed to perform the check hospital id query");
			return false;
		}
	}
	
	//method that returns the total number of existing observations
	public static int checkObservationID(){
		try {
			int count = 0;
		    String querySQL = "SELECT oId FROM Observation";
		    ResultSet rs = statement.executeQuery(querySQL);
		    while (rs.next()) {
		    	count++;
		    	String temp = rs.getString(1);
		    }
		    return count;
		} catch (SQLException e){
			int sqlCode = e.getErrorCode(); 
			String sqlState = e.getSQLState();
			System.out.println("System failed to perform the check observation id query");
			return 0;
		}
	}
	
	//method that checks if the patient id
	public static boolean checkPatientID(int input) {
		try {
		    String querySQL = "SELECT patientId FROM Patient WHERE patientId = " + input;
		    ResultSet rs = statement.executeQuery(querySQL);
		    if (rs.next() == false) {
		    	return false;
		    } else {
		    	return true;
		    }
		} catch (SQLException e){
			int sqlCode = e.getErrorCode(); 
			String sqlState = e.getSQLState();
			System.out.println("System failed to perform the check patient id query");
			return false;
		}
	}
	
	//method that insert the values into the database
	public static void insertValues(int oId, int personID, int hospitalID, String observation, String date, String time) throws SQLException {
		try {
		    String insertSQL = "INSERT INTO Observation VALUES ("+ oId + ", " + personID + ", "+ hospitalID + ", \'" + observation + "\', \'" + date + "\' , \'" + time +"\') " ;
		    System.out.println (insertSQL) ;
		    statement.executeUpdate (insertSQL) ;
		    System.out.println ("Observation was successfully inserted.") ;

		} catch (SQLException e) {
			int sqlCode = e.getErrorCode(); // Get SQLCODE
			String sqlState = e.getSQLState(); // Get SQLSTATE

			System.out.println("Code failed to insert values"); 
	    }
	}
	
	//close the connection
	public void closeConnection() throws SQLException {
		statement.close ( ) ;
		con.close ( ) ;
	}
}
