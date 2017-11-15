import java.sql.*;
import java.util.*;

public class sdap2 {
	
	public static Map<ReportOne, Double> getReportOne(ResultSet rs) throws SQLException {
		Map<ReportOne, Double> res = new HashMap<ReportOne, Double>();
		while (rs.next()) {
			String name = rs.getString("cust");
			String prod = rs.getString("prod");
			int quant = rs.getInt("quant");
			boolean contains = false;
			ReportOne ro = new ReportOne(name, prod);
			for (ReportOne r : res.keySet()) {
				if (r.equals(ro)) {
					contains = true;
					res.put(r, avg(r, res.get(r), quant));
					break;
				}
			}
			if (contains == false) res.put(ro, (double)quant);
		}
		return res;
	}
	
	public static double avg(ReportOne ro, double quantBefore, int quant) {
		return (ro.count * quantBefore + quant) / (++ro.count); 
	}
	
	public static Map<ReportOne, int[]> result1(Map<ReportOne, Double> map) {
		Map<ReportOne, int[]> res = new HashMap<ReportOne, int[]>();
		for (Map.Entry<ReportOne, Double> map1 : map.entrySet()) {
			int[] resData = new int[2];
			ReportOne r = map1.getKey();
			for (ReportOne ro : map.keySet()) {
				
			}
		}
		return res;
	}
	
	public static Map<ReportOne, Double> report1(Map<ReportOne, Double> map) {
		for (Map.Entry<ReportOne, Double> map1 : map.entrySet()) {
			ReportOne ro = map1.getKey();
			for (Map.Entry<ReportOne, Double> map2 : map.entrySet()) {
				ReportOne newRo = map2.getKey();
				if (newRo.name.equals(ro.name) && !newRo.prod.equals(ro.prod)) {
					ro.otherProdAvg = ro.otherAvg(map2.getValue());
				}
				if (!newRo.name.equals(ro.name) && newRo.prod.equals(ro.prod)) {
					ro.otherCustAvg = ro.otherCust(map2.getValue());
				}
			}
		}
		return map;
	}
	
	public static void displayReportOne(Map<ReportOne, Double> map) {
		System.out.println("CUSTOMER PRODUCT THE_AVG OTHER_PROD_AVG OTHER_CUST_AVG");
		System.out.println("======== ======= ======= ============== ==============");
		for (Map.Entry<ReportOne, Double> entry : map.entrySet()) {
			ReportOne ro = entry.getKey();
			System.out.printf("%-8s %-7s %7d %14d %14d%n", ro.name, ro.prod, (int)entry.getValue().doubleValue(), (int)ro.otherProdAvg, (int)ro.otherCustAvg);
		}
	}
		
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
	
	public static Map<Com, Integer> Report2(Map<Com, Integer> map) {
		for (Map.Entry<Com, Integer> entry : map.entrySet()) {
			Com c = entry.getKey();
			Com before = new Com(c.name, c.prod, c.quarter - 1);
			Com after = new Com(c.name, c.prod, c.quarter + 1);
			for (Com com : map.keySet()) {
				if (com.equals(before)) c.beforeAvg = map.get(com);
				if (com.equals(after)) c.afterAvg = map.get(com);
				if (c.quarter == 1) c.beforeAvg = 0;
				if (c.quarter == 4) c.afterAvg = 0;
			}
		}
		return map;
	}
	
	public static void displayReport2(Map<Com, Integer> map) {
		System.out.println("CUSTOMER PRODUCT QUARTER BEFORE_AVG AFTER_AVG");
		System.out.println("======== ======= ======= ========== =========");
		for (Map.Entry<Com, Integer> entry : map.entrySet()) {
			Com c = entry.getKey();
			if (c.quarter == 1) {
				System.out.printf("%-8s %-7s %-7s %10s %9d%n", c.name, c.prod, "Q1", "<NULL>", c.afterAvg);
			}
			if (c.quarter == 4) {
				System.out.printf("%-8s %-7s %-7s %10d %9s%n", c.name, c.prod, "Q4", c.beforeAvg, "<NULL>");
			}
			else if (c.quarter == 2 || c.quarter == 3) {
				System.out.printf("%-8s %-7s %-7s %10d %9d%n", c.name, c.prod, quarterToString(c.quarter), c.beforeAvg, c.afterAvg);
			}
		}
	}
	
	public static String quarterToString(int quarter) {
		if (quarter == 2) return "Q2";
		return "Q3";
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
//			Map<Com, Integer> m = Report2(getCom(rs));
//			displayReport2(m);
			displayReportOne(report1(getReportOne(rs)));
			
		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();

		}
	}
}

class Com {
	String name, prod;
	int quarter, beforeAvg, afterAvg;
	int count = 1;
	Com before, after;
	
	public Com(String name, String prod, int quarter) {
		this.name = name;
		this.prod = prod;
		this.quarter = quarter;
	}
	
	public String toString() {
		return name + " " + prod + " " + quarter + " " + beforeAvg + " " + afterAvg;
	}
	
	public boolean equals(Com c) {
		return (this.name.hashCode() == c.name.hashCode() && this.prod.hashCode() == c.prod.hashCode() && this.quarter == c.quarter);
	}
}

class ReportOne {
	String name, prod;
	int count = 1, prodCount = 0, custCount = 0;
	double otherProdAvg, otherCustAvg;
	
	public ReportOne(String name, String prod) {
		this.name = name;
		this.prod = prod;
	}
	
	public String toString() {
		return this.name + " " + this.prod;
	}
	
	public double otherAvg(Double double1) {
		return ((this.otherProdAvg * this.prodCount + double1) / (++this.prodCount));
	}

	public double otherCust(Double double1) {
		return ((this.otherCustAvg * this.custCount + double1) / (++this.custCount));
	}
	
	public boolean equals(ReportOne ro) {
		return (this.name.hashCode() == ro.name.hashCode() && this.prod.hashCode() == ro.prod.hashCode());
	}
}