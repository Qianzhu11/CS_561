import java.sql.*;
import java.util.*;

public class Assignment2_1 {
	
	public static List<Combo> getCombination(ResultSet rs) throws SQLException {
		List<Combo> res = new ArrayList<Combo>();
		while (rs.next()) {
			String cust = rs.getString("cust");
			String prod = rs.getString("prod");
			Combo c = new Combo(cust, prod);
			c.quant = rs.getInt("quant");
			c.avg = c.quant;
			boolean contain = false;
			for (int i = 0; i < res.size(); i++) {
				Combo c1 = res.get(i);
				if (c.equals(c1)) {
					contain = true;
					c1.avg = avg(c1, c.quant);
				}
			}
			if (contain == false)
				res.add(c);
		}
		return res;
	}
	
	public static List<Combo> getResult(List<Combo> l) {
		for (int i = 0; i < l.size(); i++) {
			Combo c1 = l.get(i);
			for (int j = 0; j < l.size(); j++) {
				Combo c2= l.get(j);
				if (c1.custEqual(c2) && !c1.prodEqual(c2))
					c1.other_prod_avg = otherProdAvg(c1, c2);
				if (!c1.custEqual(c2) && c1.prodEqual(c2))
					c1.other_cust_avg = otherCustAvg(c1, c2);
			}
		}
		return l;
	}
	
	public static double otherProdAvg(Combo c1, Combo c2) {
		double res = (c1.other_prod_avg * c1.count_prod + c2.avg * c2.avg_count) / (c1.count_prod + c2.avg_count);
		c1.count_prod += c2.avg_count;
		return res;
	}
	
	public static double otherCustAvg(Combo c1, Combo c2) {
		double res = (c1.other_cust_avg * c1.count_cust + c2.avg * c2.avg_count) / (c1.count_cust + c2.avg_count);
		c1.count_cust += c2.avg_count;
		return res;
	}
	
	public static double avg(Combo c, int quant) {
		return (c.avg * c.avg_count + quant) / (++c.avg_count);
	}
	
	public static void display(List<Combo> l) {
		System.out.println("CUSTOMER PRODUCT THE_AVG OTHER_PROD_AVG OTHER_CUST_AVG");
		System.out.println("======== ======= ======= ============== ==============");
		for (Combo c : l) {
			System.out.printf("%-8s %-7s %7d %14d %14d%n", c.cust, c.prod, (int)c.avg, (int)c.other_prod_avg, (int)c.other_cust_avg);
		}
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
			display(getResult(getCombination(rs))); 			
		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
}

class Combo {
	String cust, prod;
	int  quant, avg_count = 1, count_prod = 0, count_cust = 0;
	double avg, other_prod_avg, other_cust_avg;
	
	public Combo (String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
	}
	
	public boolean equals(Combo c) {
		return (this.cust.hashCode() == c.cust.hashCode() && this.prod.hashCode() == c.prod.hashCode());
	}
	
	public boolean custEqual(Combo c) {
		return this.cust.hashCode() == c.cust.hashCode();
	}
	
	public boolean prodEqual(Combo c) {
		return this.prod.hashCode() == c.prod.hashCode();
	}
}