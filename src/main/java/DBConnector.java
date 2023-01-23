import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DBConnector {

	//load DB variables
	static Dotenv dotenv = Dotenv.load();
	static final String DB_URL = dotenv.get("DB_URL");
	static final String USER = dotenv.get("USER");
	static final String PASS = dotenv.get("PASS");

	static Connection conn = null;
	static PreparedStatement Pstmt;

	public static void createDB (){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			conn =DriverManager.getConnection(DB_URL,USER,PASS );
			Statement stmt = conn.createStatement();
			String createDB = "CREATE TABLE RULINGS ("+
					"id INTEGER NOT NULL AUTO_INCREMENT,"+
					"chamber VARCHAR(100), "+
					"esas_no VARCHAR(50),"+
					"karar_no VARCHAR(50),"+
					"judgement LONGTEXT, "+
					"PRIMARY KEY (id))";


			stmt.executeUpdate(createDB);


		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static PreparedStatement createConnection() throws SQLException {
		Pstmt= conn.prepareStatement("INSERT INTO RULINGS(chamber,esas_no,karar_no,judgement)" + "values(?,?,?,?)");
		return Pstmt;
	}
}
