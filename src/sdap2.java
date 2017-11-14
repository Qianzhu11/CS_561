import java.sql.*;
import java.util.*;

public class sdap2 {
	
	public static Map<Com, Integer> getCom(ResultSet rs) throws SQLException {
		Map<Com, Integer> combination = new HashMap<Com, Integer>();
		while (rs.next()) {
			String name = rs.getString("cust");
			String prod = rs.getString("prod");
			int month = rs.getInt("month");
			int quant = rs.getInt("quant");
			Com newCom = new Com(name, prod, month);
			combination.put(newCom, quant);
		}
		return combination;
	}
	
	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = ".";
		String url ="jdbc:postgresql://localhost:5432/CS-561";
		
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		} catch (Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		
		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			System.out.println(getCom(rs));
		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();

		}
	}
}

class Com {
	String name, prod;
	int month;
	public Com(String name, String prod, int month) {
		this.name = name;
		this.prod = prod;
		this.month = month;
	}
}