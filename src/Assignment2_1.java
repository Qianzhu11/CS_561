/*
 * The idea is through one iteration, we could get each customer-product combination
 * and with their average quantity, then we store them in a list. Why I choose list is that
 * list allows us to quickly access the element when given the index. After I get the whole combinations,
 * we need to compute other products' average quantity for the same customer. We could iterate the
 * result list which contains the whole combinations to find the element which has the same customer name
 * but different product. Compute their average quantity. And the same way to compute other customer's 
 * quantity for the same product. In order to store the customer-product combination, I create an auxiliary
 * class Combo in the bottom to define the combination. Next two reports are similar to this one, so there will be
 * no such explanations in next ones.
 */

import java.sql.*;
import java.sql.*;
import java.util.*;

public class Assignment2_1 {
	
	public static List<Combo> getCombination(ResultSet rs) throws SQLException { 
		List<Combo> res = new ArrayList<Combo>();		//This function helps us to get all the 		
		while (rs.next()) {								//customer-product and their average quantity
			String cust = rs.getString("cust");			//combinations. 
			String prod = rs.getString("prod");
			Combo c = new Combo(cust, prod);
			c.quant = rs.getInt("quant");				
			c.avg = c.quant;							//Initially, the average quantity of the combination is its quantity
			boolean contain = false;					//flag to signal if the result list contains the same combination
			for (int i = 0; i < res.size(); i++) {								
				Combo c1 = res.get(i);					
				if (c.equals(c1)) {						//if there has been the same customer-product combination,	
					contain = true;						//we set the flag to be true and
					c1.avg = avg(c1, c.quant);			//we need to update the average quantity for it.
				}
			}
			if (contain == false)						//if the flag is false which means the list doesn't contain the
				res.add(c);								//the same combination, we add it into the list.
		}
		return res;
	}
	
	public static List<Combo> getResult(List<Combo> l) {	//after get the list that contains whole combinations
		for (int i = 0; i < l.size(); i++) {				//we need to compute the average product of other products
			Combo c1 = l.get(i);							//and the same products for other customers.
			for (int j = 0; j < l.size(); j++) {			//Since we have stored the information in the list,
				Combo c2= l.get(j);							//so this step is easy, for each given combination, we iterate
				if (c1.custEqual(c2) && !c1.prodEqual(c2))	//the list to find other products for the same customers,
					c1.other_prod_avg = otherProdAvg(c1, c2);	//then call this function to compute
				if (!c1.custEqual(c2) && c1.prodEqual(c2))		//after these steps, all the information we need are store
					c1.other_cust_avg = otherCustAvg(c1, c2);	//in the combination which is in the result list.
			}
		}
		return l;
	}
	
	public static double otherProdAvg(Combo c1, Combo c2) {	//firstly, we get the current_other_products_total
		double res = (c1.other_prod_avg * c1.count_prod + c2.avg * c2.avg_count) / (c1.count_prod + c2.avg_count);	//then add other_prod_same_cust_total to it
		c1.count_prod += c2.avg_count;	//finally, we update the count_prod of the Combo c1
		return res;
	}
	
	public static double otherCustAvg(Combo c1, Combo c2) {	//this one works the same way as last one
		double res = (c1.other_cust_avg * c1.count_cust + c2.avg * c2.avg_count) / (c1.count_cust + c2.avg_count);
		c1.count_cust += c2.avg_count;
		return res;
	}
	
	public static double avg(Combo c, int quant) {		//help us to compute the average quantity for Combo c, 
		return (c.avg * c.avg_count + quant) / (++c.avg_count);	//the meaning of the attribute of Combo is illustrated in the class Combo
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
	String cust, prod;	//quant is the quantity of Combo, avg_count is number of the Combo that have the same cust and prod attribute as Combo c, initially it's 1, since Combo c itself is also taken into account.  
	int  quant, avg_count = 1, count_prod = 0, count_cust = 0;	//count_prod is the number of products where the customers are the same, count_prod helps us to compute the other_prod_avg
	double avg, other_prod_avg, other_cust_avg;			//count_cust has the similar effect as the count_cust.
	
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