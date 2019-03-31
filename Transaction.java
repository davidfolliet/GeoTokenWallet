import java.math.BigDecimal;
import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

//GeoToken
//David Folliet
public class Transaction {				//Base class for Transaction
	public Transaction(GeoToken geoToken, String fromAddress, String toAddress, BigDecimal numberOfTokens, BigInteger gasPrice, BigInteger gasLimit) {
		this.geoToken = geoToken;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.numberOfTokens = numberOfTokens;
		this.gasPrice = gasPrice;
		this.gasLimit = gasLimit;
	}
	public Transaction(String fromAddress, String toAddress, BigDecimal numberOfTokens, BigInteger gasPrice, BigInteger gasLimit) {								//Eth transaction
		this(null, fromAddress, toAddress, numberOfTokens, gasPrice, gasLimit);
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public BigDecimal getNumberOfTokens() {
		return numberOfTokens;
	}
	public BigInteger getGasPrice() {
		return gasPrice;
	}
	public BigInteger getGasLimit() {
		return gasLimit;
	}
	public GeoToken getGeoToken() {
		return geoToken;
	}
	public boolean isEthTransaction() {
		return isEthTransaction;
	}
	public String toString() {														//outgoing tx only... overwrite in outgoing, delete txrecept and add to outgoing................. ..... ...... .... . . . . . . . . .
		return "Status: " + status
				+ ",\n"
				+ "Transaction Hash: " + transactionReceipt.getTransactionHash()  
				+ ",\n"
				+ "To: " + toAddress
				+ ",\n"
				+ "From: " + fromAddress
				+ ",\n"
				+ "IsEthereumTransaction: " + isEthTransaction
				+ ",\n"
				+ "Amount: " + numberOfTokens
				+ ",\n"	
				+ "Gas Price Gwei: " + new BigDecimal(gasPrice.toString()).divide(new BigDecimal(Math.pow(10, 9))).toString()
				+ ",\n"	
				+ "Gas Limit: " + gasLimit.toString()
				+ ",\n"
				+ "Gas Used: " + UnitConverter.getGasFromHexValue(transactionReceipt.getGasUsedRaw())
				+ ",\n"
				+ "Eth Transaction Cost: " + UnitConverter.getTransactionFeeEth(transactionReceipt.getGasUsedRaw(), gasPrice).toPlainString() 
				+ ",\n"	
				+"USD Transaction Cost: $" + new UnitConverter().getUsdValue(UnitConverter.getTransactionFeeEth(transactionReceipt.getGasUsedRaw(), gasPrice).toPlainString())
				+ "\n";
	}
	private GeoToken geoToken;
	private String fromAddress;
	private String toAddress;
	private BigDecimal numberOfTokens;
	private BigInteger gasPrice;
	private BigInteger gasLimit;
	protected boolean isEthTransaction;
	private Status status;
	protected TransactionReceipt transactionReceipt;
	protected static enum Status{
		OUTGOING, MINED, RECEIPT_TIMEOUT, RECEIVED, SENT
	}
	protected void setStatus(Status staus) {
		this.status = staus;
	}
	public Status getStatus() {
		return status;
	}
}
