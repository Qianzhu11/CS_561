/*
 * This is also similar to previous reports. Through one iteration, we store the customer-product-quarter combination in list.
 * and compute minimum and average quantity of each element
 */

import java.sql.*;
import java.util.*;

public class Assignment2_3 {
	
	public static List<Combination> getCombo(ResultSet rs) throws SQLException {
		List<Combination> res = new ArrayList<Combination>();
		List<Combination> pseudoRes = new ArrayList<Combination>();
		while (rs.next()) {
			String cust = rs.getString("cust");
			String prod = rs.getString("prod");
			int quarter = (rs.getInt("month") - 1) / 3 + 1;
			Combination c = new Combination(cust, prod, quarter);
			c.quant = rs.getInt("quant");
			c.min = c.quant;		//Initially, for each customer-product-quarter combination, average and minimum quantity is its own quantity.
			c.avg = c.quant;
			boolean contain = false;
			for (int i = 0; i < res.size(); i++) {
				Combination c1 = res.get(i);
				if (c1.equals(c)) {
					contain = true;
					if (c.quant < c1.min) c1.min = c.quant;	//for every equal element(here equal means the same customer, product, quarter)
					c1.avg = avg(c1, c.quant);				//each time, we compare its quantity then update minimum quantity and average quantity.
				}
			}
			if (contain == false) res.add(c);
			pseudoRes.add(c);
		}
		report3(res, pseudoRes);
		return res;
	}
	
	public static int avg(Combination c, int quant) {
		return (c.avg_count * c.avg + quant) / (++c.avg_count);
	}
	
	public static List<Combination> report3(List<Combination> res, List<Combination> pseudoRes) {
		for (int i = 0; i < res.size(); i++) {
			Combination c1 = res.get(i);
			for (int j = 0; j < pseudoRes.size(); j++) {
				Combination c2 = pseudoRes.get(j);
				if (c1.isBeforeRes(c2)) c1.quarterBefore++;	
				if (c1.isAfterRes(c2)) c1.quarterAfter++;
			}
		}
		return res;
	}
	
	public static void displayReport3(List<Combination> l) {
		System.out.println("CUSTOMER PRODUCT QUARTER BEFORE_TOT AFTER_TOT");
		System.out.println("======== ======= ======= ========== =========");
		for (int i = 0; i < l.size(); i++) {
			Combination c = l.get(i);
			if (c.quarterAfter != 0 || c.quarterBefore != 0) {	//if its before_quarter and after_quarter are both 0, we eliminate is so as to avoid displaying useless data
				if (c.quarter == 1) 
					System.out.printf("%-8s %-7s %-7s %10s %9d%n", c.cust, c.prod, "Q1", "<NULL>", c.quarterAfter);
				if (c.quarter == 2)
					System.out.printf("%-8s %-7s %-7s %10d %9d%n", c.cust, c.prod, "Q2", c.quarterBefore, c.quarterAfter);
				if (c.quarter == 3)
					System.out.printf("%-8s %-7s %-7s %10d %9d%n", c.cust, c.prod, "Q3", c.quarterBefore, c.quarterAfter);
				if (c.quarter == 4)
					System.out.printf("%-8s %-7s %-7s %10d %9s%n", c.cust, c.prod, "Q4", c.quarterBefore, "<NULL>");
			}
		}
	}

	public static void main(String[] args) {
		String usr ="postgres";
		String pwd =".";
		String url ="jdbc:postgresql://localhost:5432/CS-561";

		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		} catch(Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			displayReport3(getCombo(rs));
		} catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
}

class Combination {
	String cust, prod;
	int quant, quarter, min, avg, avg_count = 1, quarterBefore = 0, quarterAfter = 0;
	
	public Combination(String cust, String prod, int quarter) {
		this.cust = cust;
		this.prod = prod;
		this.quarter = quarter;
	}
	
	public boolean equals(Combination c) {
		return (cust.hashCode() == c.cust.hashCode() && prod.hashCode() == c.prod.hashCode() && quarter == c.quarter);
	}
	
	public boolean isBeforeRes(Combination c) {	//determine if the previous quarter meets requirement
		return (cust.hashCode() == c.cust.hashCode() && prod.hashCode() == c.prod.hashCode() && quarter > c.quarter && c.quant < avg && c.quant > min);
	}
	
	public boolean isAfterRes(Combination c) {	//determine if the following quarter meets requirement
		return (cust.hashCode() == c.cust.hashCode() && prod.hashCode() == c.prod.hashCode() && quarter < c.quarter && c.quant < avg && c.quant > min);
	}
}
