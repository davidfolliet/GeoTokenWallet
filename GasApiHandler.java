import java.math.BigInteger;

//GeoToken
//David Folliet
public class GasApiHandler extends ApiHandler{
	private final static String GAS_API_URI = "https://www.ethgasstation.info/json/ethgasAPI.json";
	private final static String AVERAGE = "average";
	private final static String SAFE_LOW = "safeLow";
	private final static String FAST = "fast";
	private final static String API_CONVERSION_FACTOR = "100000000";
	public GasApiHandler() {
		super(new JSONReader(), GAS_API_URI);
	}	
	private static BigInteger getGasValue(String apiValue) {
		BigInteger value = new BigInteger(GasStation.getGasAsRoundedInteger(apiValue));
		return value.multiply(new BigInteger(API_CONVERSION_FACTOR));
	}
	public BigInteger getAverage() {
		return getGasValue((String) getValue(AVERAGE));
	}
	
	public BigInteger getSafeLow() {
		return getGasValue((String) getValue(SAFE_LOW));
	}
	
	public BigInteger getFast() {
		return getGasValue((String) getValue(FAST));
	}	
}
