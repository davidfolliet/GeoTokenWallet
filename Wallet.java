import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.ChainId;

//GeoToken Wallet
//David Folliet
public class Wallet {
	private final static String GEOTOKEN_CONTRACT_ADDRESS = "0xb5a00e30602ee03074f4f8e996adaf9dc96224a4"; 	
	public Wallet(byte network, String hostName, Host host) { 
		this.network = network;					//network: mainnet/testnet
		this.hostName = hostName;				//hostName: keep track of current node host name
		this.host = host;						//host: mostly static info for hosts (api key read from file for infura node is not static)
		transactions = new Transactions();		//transactions: container for transactions
	}
	public Wallet(byte network, String hostName, String privateKey, Host host) throws CipherException  {
		this(network, hostName, host);
		account = new Account(privateKey);
		setNode(network, hostName);
	}
	public Wallet(byte network, String hostName, String password, String walletFilepath, Host host) throws CipherException {  
		this(network, hostName, host);
		account = new Account(password, walletFilepath);
		setNode(network, hostName);
	}
	private void setNode(byte network, String hostName) {         
		node = new Node(network, hostName, account);
		setGeoToken();
	}
	private void setGeoToken() {
		 geoToken = GeoToken.load(GEOTOKEN_CONTRACT_ADDRESS, node.getWeb3j(), node.getTransactionManager(), account.getGasPrice(), account.getGasLimit());
	}
	public void setHostName(String hostName) {    //change host node
		setNode(network, hostName);
	}
	public void setNetwork(byte network) {        //change to testnet, mainnet, ...                 
		if(this.network == ChainId.MAINNET && network == ChainId.ROPSTEN && hostName.equals(host.getInfuraHostMainNet()))
			setNode(network, host.getInfuraHostTestNet());
		else if(this.network == ChainId.ROPSTEN && network == ChainId.MAINNET && hostName.equals(host.getInfuraHostTestNet()))
			setNode(network, host.getInfuraHostMainNet());
		else
			setNode(network, hostName);
	}
	public Node getNode() {
		return node;
	}
	public Account getAccount() {
		return account;
	}
	public Transactions getTransactions() {
		return transactions;
	}
	public String getGeoTokenBalance() throws ExecutionException {
		try {	
			return new BigDecimal(geoToken.balanceOf(account.getCredentials().getAddress()).sendAsync().get())
					.divide(new BigDecimal(Math.pow(10, UnitConverter.STANDARD_TOKEN_NUMBER_OF_DECIMALS))).toPlainString();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getRawEthereumBalance() {
		try {
			return node.getWeb3j().ethGetBalance(account.getCredentials().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get().getBalance().toString();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getEthereumBalance() {                                      
		return new BigDecimal(getRawEthereumBalance()).divide(new BigDecimal(Math.pow(10, UnitConverter.STANDARD_TOKEN_NUMBER_OF_DECIMALS))).toPlainString();
	}
	public void initiateTransaction(boolean isEthereumTransaction) {						//default values
		account.setGasPrice();
		if(isEthereumTransaction)
			account.setGasLimitDefaultEth();
		else
			account.setGasLimitDefaultToken();	
	}
	public void initiateTransaction(BigInteger gasPrice, BigInteger gasLimit) {				//user set values
		account.setGasPrice(gasPrice);
		account.setGasLimit(gasLimit);	
	}
	public boolean isSufficientGeotokenBalanceGeotokenTransfer(BigInteger gasPrice, BigInteger gasLimit, BigDecimal numberOfTokens) throws ExecutionException {
		return new BigDecimal(getGeoTokenBalance()).compareTo(numberOfTokens) >= 0;
	}
	public boolean isSufficientEthereumBalanceGeotokenTransfer(BigInteger gasPrice, BigInteger gasLimit, BigDecimal numberOfTokens) {
		return new BigDecimal(getRawEthereumBalance()).compareTo(new BigDecimal (gasPrice.multiply(gasLimit).toString())) >= 0;
	}
	public boolean isSufficientBalanceGeotokenTransfer(BigInteger gasPrice, BigInteger gasLimit, BigDecimal numberOfTokens) throws ExecutionException {
		return isSufficientGeotokenBalanceGeotokenTransfer(gasPrice, gasLimit, numberOfTokens) 
				&& isSufficientEthereumBalanceGeotokenTransfer(gasPrice, gasLimit, numberOfTokens);
	}
	public boolean isSufficientBalanceGeotokenTransfer(BigDecimal numberOfTokens) throws ExecutionException {
		return isSufficientBalanceGeotokenTransfer(account.getGasPrice(), account.getGasLimit(), numberOfTokens);
	}
	public boolean isSufficientGeotokenBalanceGeotokenTransfer(BigDecimal numberOfTokens) throws ExecutionException{
		return isSufficientGeotokenBalanceGeotokenTransfer(account.getGasPrice(), account.getGasLimit(), numberOfTokens);
	}
	public boolean isSufficientEthereumBalanceGeotokenTransfer(BigDecimal numberOfTokens){
		return isSufficientEthereumBalanceGeotokenTransfer(account.getGasPrice(), account.getGasLimit(), numberOfTokens);
	}
	
	public boolean isSufficientBalanceEthereumTransfer(BigInteger gasPrice, BigInteger gasLimit, BigDecimal numberOfTokens) {
		return new BigDecimal(getRawEthereumBalance())
				.compareTo(numberOfTokens.multiply(new BigDecimal(Math.pow(10, UnitConverter.STANDARD_TOKEN_NUMBER_OF_DECIMALS)))
						.add(new BigDecimal (gasPrice.multiply(gasLimit).toString())))
							>= 0;
	}
	public boolean isSufficientBalanceEthereumTransfer(BigDecimal numberOfTokens) {
		return isSufficientBalanceEthereumTransfer(account.getGasPrice(), account.getGasLimit(), numberOfTokens);
	}
	public Transaction sendGeoTokenTransaction(String toAddress, BigDecimal numberOfTokens) throws ExecutionException { 		
		setGeoToken();
		if(!isSufficientBalanceGeotokenTransfer(numberOfTokens))					
			return null;
		OutgoingTransaction outgoingTransaction = new OutgoingTransaction(geoToken, account.getCredentials().getAddress(), toAddress, numberOfTokens, account.getGasPrice(), account.getGasLimit(), node.getWeb3j());
		Thread tx = new Thread(outgoingTransaction);
		tx.start();
		//transactions.insert(outgoingTransaction.getTransactionReceipt().getTransactionHash(), outgoingTransaction);
		return outgoingTransaction;
	}
	public Transaction sendEthTransaction(String toAddress, BigDecimal numberOfTokens) {	
		if(!isSufficientBalanceEthereumTransfer(numberOfTokens))					
			return null;
		OutgoingEthTransaction outgoingEthTransaction = new OutgoingEthTransaction(account.getCredentials(), toAddress, numberOfTokens, account.getGasPrice(), account.getGasLimit(), node.getWeb3j());
		Thread tx = new Thread(outgoingEthTransaction);
		tx.start();
		//transactions.insert(outgoingEthTransaction.getTransactionReceipt().getTransactionHash(), outgoingEthTransaction);
		return outgoingEthTransaction;
	}
	private Node node;
	private Account account;
	private GeoToken geoToken;
	private String hostName;
	private byte network;
	private Transactions transactions;
	private Host host;
}