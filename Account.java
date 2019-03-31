import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

//GeoToken
//David Folliet

public class Account {
	/* Constructors */
	public Account() {																											//default constructor, initialize variables
		gasStation = new GasStation();
		setGasPrice(new BigInteger("0"));
		setGasLimit(new BigInteger("0"));
	}
	public Account(String privateKey) {																							//load private key
		this();
		credentials = Credentials.create(privateKey);
	}
	public Account(String password, String walletFilepath) throws CipherException {												//load private key file
		this();
		try {
			credentials = WalletUtils.loadCredentials(password, walletFilepath);
		} catch (IOException e) {																								//catch cipher exception in gui to handle it (exception thrown) 
			e.printStackTrace();
		}
	}
	/* Public Member Functions */
	public String createWalletFile(String password, String destinationDirectory) throws CipherException {						//creates private key file from existing private key
		try {
			return WalletUtils.generateWalletFile(password, credentials.getEcKeyPair(), new File(destinationDirectory), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* Public Static Member Functions */
	public static String createNewWalletFile(String password, String destinationDirectory) {									//creates private key file from new private key
		try {
			return WalletUtils.generateFullNewWalletFile(password, new File(destinationDirectory));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (CipherException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* Getters and Setters */
	public BigInteger getGasPrice() {
		return gasPrice;
	}
	public BigInteger getGasLimit() {
		return gasLimit;
	}
	public Credentials getCredentials() {
		return credentials;
	}
	public void setGasLimit(BigInteger gasLimit) {
		this.gasLimit = gasLimit;
	}
	public void setGasLimitDefaultEth() {
		 setGasLimit(new BigInteger(GasStation.DEFAULT_ETH_GAS_LIMIT));
	}
	public void setGasLimitDefaultToken() {
		 setGasLimit(new BigInteger(GasStation.DEFAULT_TOKEN_GAS_LIMIT));
	}
	public void setGasLimitMinimumGT() {
		 setGasLimit(new BigInteger(GasStation.MIN_GT_GAS_LIMIT));
	}
	public void setGasPrice(BigInteger gasPrice) {       
		this.gasPrice = gasPrice;
	}
	public void setGasPrice() {  
		setGasPrice(gasStation.getGasApiHandler().getAverage());
	}
	public void setCheapGasPrice() {  
		setGasPrice(gasStation.getGasApiHandler().getSafeLow());
	}
	public void setFastestGasPrice() {  
		setGasPrice(gasStation.getGasApiHandler().getFast());
	}
	/* Private Members */
	private BigInteger gasPrice;
	private BigInteger gasLimit;
	private GasStation gasStation;
	private Credentials credentials;
}
