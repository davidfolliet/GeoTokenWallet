
//GeoToken
//David Folliets
public class EthApiHandler extends ApiHandler{
	private final static String ETH_API_URI = "https://api.coinmarketcap.com/v1/ticker/ethereum/";
	private final static String PRICE_USD = "price_usd";
	private final static String PRICE_BTC = "price_btc";
	public EthApiHandler() {
		super(new JSONReader(), ETH_API_URI);
	}	
	public String getPriceUsd() {
		return (String) getValue(PRICE_USD);
	}
	public String getPriceBtc() {
		return (String) getValue(PRICE_BTC);
	}
}
