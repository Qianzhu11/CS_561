import java.util.*;

public class demo {
	
	public static int avgQuant(Com com, int quantBefore, int quant) {
		return (com.count * quantBefore + quant) / (++com.count);
	}
	
	public static void main(String[] args) {
		int a = 1;
		System.out.println("CUSTOMER PRODUCT QUARTER BEFORE_AVG AFTER_AVG");
		System.out.println("======== ======= ======= ========== =========");
		System.out.printf("%-8s %-7s %-7s %10d %9d%n", "Emily", "Soap", "Q1", 22156, 415);
	}
}

class Com1 {
	String name, prod;
	int quarter;
	int count = 1;
	
	public Com1(String name, String prod, int quarter) {
		this.name = name;
		this.prod = prod;
		this.quarter = quarter;
	}
	
	public String toString() {
		return name + " " + prod + " " + quarter;
	}
	
	public boolean equals(Com1 c) {
		return (this.name == c.name && this.prod == c.prod && this.quarter == c.quarter);
	}
}