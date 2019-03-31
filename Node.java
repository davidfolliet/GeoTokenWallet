import java.io.IOException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.NoOpProcessor;

//GeoToken
//David Folliet
public class Node {
	public Node(byte network, String hostName, Account account) {
		web3j = Web3j.build(new HttpService(hostName)); 
		transactionManager = new RawTransactionManager(web3j, account.getCredentials(), network, new NoOpProcessor(web3j));
	}
	public String getClientVersion() {
		try {
			return web3j.web3ClientVersion().send().getWeb3ClientVersion();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Web3j getWeb3j() {
		return web3j;
	}
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	private Web3j web3j;
	private TransactionManager transactionManager;
	
}
