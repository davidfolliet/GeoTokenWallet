//GeoToken
//David Folliet
public class EtherscanApiHandler extends ApiHandler{
	//private final static String ROPSTEN_ETHERSCAN_API_URI = "https://api-ropsten.etherscan.io/api?module=account&action=tokentx&address=0xF735626041da9f5B4F2d8D90f351a2d25aa04ED9&startblock=0&endblock=999999999&sort=asc&apikey= ";	
	private final static String ETHERSCAN_API_URI_BASE = "https://api.etherscan.io/api?module=account&action=tokentx&address=0xF735626041da9f5B4F2d8D90f351a2d25aa04ED9&startblock=0&endblock=999999999&sort=asc&apikey=";
	public EtherscanApiHandler() {
		super(new MultipleJSONReader(), getApiUri());
	}
	public String getTransactions() {
		return getApiResponse().toString();
	}
	private static String getApiUri() {
		return ETHERSCAN_API_URI_BASE + new SettingsFileReader().getEtherscanApiKey();
	}
}
