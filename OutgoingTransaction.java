import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
//GeoToken
//David Folliet
public class OutgoingTransaction extends Transaction implements Runnable{
	private static final long DELAY = 3000;			//3 seconds (delay to check if transaction was mined
	private static final int ATTEMPTS = 1000; 		//number of times to check if transaction is mined
	public OutgoingTransaction(GeoToken geoToken, String fromAddress, String toAddress, BigDecimal numberOfTokens, BigInteger gasPrice, BigInteger gasLimit, Web3j web3j) {
		super(geoToken, fromAddress,toAddress, numberOfTokens, gasPrice, gasLimit);
		this.web3j=web3j;
		setStatus(Status.OUTGOING);
	}
	public OutgoingTransaction(String fromAddress, String toAddress, BigDecimal numberOfTokens, BigInteger gasPrice, BigInteger gasLimit, Web3j web3j) {
		super(fromAddress,toAddress, numberOfTokens, gasPrice, gasLimit);
		this.web3j=web3j;
		setStatus(Status.OUTGOING);
	}
	public void run() {								//send transactions then poll for transaction to be mined, run in a new thread to avoid freezing up the UI layer
		send();
	}
	public TransactionReceipt getTransactionReceipt() {
		return transactionReceipt;
	}
	protected void setTansactionReceipt(TransactionReceipt transactionReceipt) {
		this.transactionReceipt = transactionReceipt;
	}
	protected void poll(Web3j web3j, String transactionHash) {
		PollingTransactionReceiptProcessor pollingTransactionReceiptProcessor = new PollingTransactionReceiptProcessor(web3j, DELAY, ATTEMPTS);
		TransactionReceipt transactionReceipt = null;
		try {
			transactionReceipt = pollingTransactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransactionException e) {
			e.printStackTrace();
		}
		if(transactionReceipt != null) {
			setTansactionReceipt(transactionReceipt);
			setStatus(Status.MINED);
		}else
			setStatus(Status.RECEIPT_TIMEOUT);
	}
	public void send() {
		String transactionHash = null;
		try {
			transactionHash = getGeoToken().transfer(getToAddress(), UnitConverter.getStandardSmallestTokenUnits(getNumberOfTokens())).sendAsync().get().getTransactionHash();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		//JOptionPane.showMessageDialog  (null, "SENDING TX HASH: " + transactionHash,"Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
		poll(web3j, transactionHash);
		//JOptionPane.showMessageDialog  (null,"TX MINED\n" + "transaction: " + this.toString() + "\n" + "Transaction Receipt: " + transactionReceipt,"Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
		if(getStatus() == Status.MINED)
			JOptionPane.showMessageDialog(null, "TX MINED: " + transactionHash,"Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}
	protected Web3j web3j;
}
