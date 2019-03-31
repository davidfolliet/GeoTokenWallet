import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.JOptionPane;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
//GeoToken
//David Folliet
public class OutgoingEthTransaction extends OutgoingTransaction{
	public OutgoingEthTransaction(Credentials credentials, String toAddress, BigDecimal numberOfTokens, BigInteger gasPrice, BigInteger gasLimit, Web3j web3j) {
		super(credentials.getAddress(), toAddress, numberOfTokens, gasPrice, gasLimit, web3j);
		this.credentials = credentials;
		isEthTransaction = true;
	}
	public void run() {
		send();
	}
	public void send() {
		TransactionReceipt transactionReceipt = null;
		try {
			transactionReceipt = Transfer.sendFunds(web3j, credentials, getToAddress(), getNumberOfTokens(), Convert.Unit.ETHER).send();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransactionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		poll(web3j, transactionReceipt.getTransactionHash());
		if(getStatus() == Status.MINED)
			JOptionPane.showMessageDialog(null, "TX MINED: " + transactionReceipt.getTransactionHash(),"Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}
	private Credentials credentials;
}