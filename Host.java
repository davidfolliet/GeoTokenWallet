//GeoToken
//David Folliet
public class Host {
	public final static String LOCAL_HOST = "http://localhost";
	public final static String INFURA_HOST_MAINNET = "https://mainnet.infura.io/";
	public final static String INFURA_HOST_TESTNET = "https://ropsten.infura.io/";
	private static final int PORT = 8545;
	public Host() {
		infuraApiKey = new SettingsFileReader().getInfuraApiKey();
	}
	public String getLocalHost() {
		return LOCAL_HOST + ':' + PORT;
	}
	public String getInfuraHostMainNet() {
		return INFURA_HOST_MAINNET + infuraApiKey + ':' + PORT;
	}
	public String getInfuraHostTestNet() {
		return INFURA_HOST_TESTNET + infuraApiKey + ':' + PORT;
	}
	private String infuraApiKey;
}
