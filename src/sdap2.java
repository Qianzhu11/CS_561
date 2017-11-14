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
			boolean contains = false;
			Com newCom = new Com(name, prod, (month - 1) / 3 + 1);
			for (Com c : combination.keySet()) {
				if (c.equals(newCom)) {
					contains = true;
					combination.put(c, avgQuant(c, combination.get(c), quant));
					break;
				}
			}
			if (contains == false) {
				combination.put(newCom, quant);
			}
		}
		return combination;
	}
	
	public static int avgQuant(Com com, int quantBefore, int quant) {
		return (com.count * quantBefore + quant) / (++com.count);
	}
	
	public void displayReport2(Map m) {
		
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
			Map m = getCom(rs);
			System.out.println(m);
			
		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();

		}
	}
}

class Com {
	String name, prod;
	int quarter;
	int count = 1;
	
	public Com(String name, String prod, int quarter) {
		this.name = name;
		this.prod = prod;
		this.quarter = quarter;
	}
	
	public String toString() {
		return name + "_" + prod + "_" + quarter;
	}
	
	public boolean equals(Com c) {
		return (this.name == c.name && this.prod == c.prod && this.quarter == c.quarter);
	}
}