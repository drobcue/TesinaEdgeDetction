package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class MySqlConncetion {
	public static void main (String[]args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		ArrayList user = new ArrayList();
		Random rndGrade = new Random(5), rndCrs1 = new Random(1), rndCrs2 = new Random(2) ;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306?" +
                "user=root&password=passw1rd");
		Statement stmt = conn.createStatement();
		PreparedStatement insert = conn.prepareStatement("INSERT INTO maa.USER_SCHEDULE VALUES(?,?,?)");
		ResultSet rs = stmt.executeQuery("SELECT ID_USER FROM maa.USER");
		System.out.println("Getting users.......");
		while(rs.next()){
			user.add(rs.getString("ID_USER"));
		}
		System.out.println("\n\nUsers sets.......");
		char grade;
		int course1;
		int course2;
		System.out.println("\n\nGenerating courses.......");
		for(int i = 0; i < user.size(); i++){
			grade = getGrade(rndGrade.nextInt(6));
			course1 = rndCrs1.nextInt(2) + 4;
			System.out.println("Calculus key : " + course1);
			insert.setString(1, (String) user.get(i));
			insert.setInt(2, course1);
			insert.setString(3, Character.toString(grade));
			insert.addBatch();
			
			course2 = rndCrs2.nextInt(3) + 1;
			System.out.println("Coti key : " + course2);
			grade = getGrade(rndGrade.nextInt(6));
			insert.setString(1, (String) user.get(i));
			insert.setInt(2, course2);
			insert.setString(3, Character.toString(grade));
			insert.addBatch();
			System.out.println("Students prosesed.........." + i);
			
		}
		System.out.println("All students prosesed.\n\nInserting into database.......");
		insert.executeBatch();
		System.out.println("\n\nAll records prepses.");
		
	}
	
	
	
	public static char getGrade(int option){
		switch (option) {
		case 0:
			return 'W';
		case 1:
			return 'F';
		case 2: 
			return 'D';
		case 3:
			return 'C';
		case 4:
			return 'B';
		case 5:
			return 'A';
		default:
			return ' ';
		}
	}
}
