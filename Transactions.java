import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//GeoToken
//David Folliet

public class Transactions {					//container for Transactions
	public Transactions() {				
		transactions = new LinkedHashMap<String,Transaction>();
	}
	
	public void insert(String transactionHash, Transaction transaction) {
		transactions.put(transactionHash, transaction);
	}
	public Transaction get(String transactionHash) {
		return (Transaction) transactions.get(transactionHash);
	}
	public String toString() {
		String string = "";
		Set transactionSet = transactions.entrySet();
		for(Object transaction : transactionSet)
			string += "{\n" + transaction + "}\n";
		return string;
	}
	private Map transactions;
	
}
