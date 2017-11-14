import java.util.*;

public class demo {
	
	public static int avgQuant(Com com, int quantBefore, int quant) {
		return (com.count * quantBefore + quant) / (++com.count);
	}
	
	public static void main(String[] args) {
		Map<Com, Integer> combination = new HashMap<Com, Integer>();
		int quant = 1;
		Com newCom = new Com("a", "a", 1);
		combination.put(newCom, 1);
		Com c1 = new Com("a", "a", 1);
		int quant1 = 3;
		Com c2 = new Com("a", "a", 1);
		int quant2 = 5;
		boolean contains = false;
		for (Com c : combination.keySet()) {
			if (c.equals(c2)) {
				contains = true;
				combination.put(c, avgQuant(c, combination.get(c), quant2));
				System.out.println(c.count);
			}
		}
		if (contains == false) {
			combination.put(c2, quant2);
		}
		
		System.out.println(combination);
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