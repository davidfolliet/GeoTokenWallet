import java.util.regex.Pattern;

//GeoToken
//David Folliet
public class GasStation {
	public final static String MIN_GT_GAS_LIMIT = "50000";                   
	public final static String DEFAULT_TOKEN_GAS_LIMIT = "200000";        //MEW DEFAULT VALUE FOR SENDING TOKENS
	public final static String DEFAULT_ETH_GAS_LIMIT = "21000";		      //MEW DEFAULT VALUE FOR SENDING ETH   //ALSO MINIMUM FOR ETH
	public GasStation() {
		setGasApiHandler();
	}
	public void setGasApiHandler() {
		gasApiHandler = new GasApiHandler();							
	}
	public GasApiHandler getGasApiHandler() {
		return (GasApiHandler) gasApiHandler;
	}
	public static String getGasAsRoundedInteger(String realNumber) {
		String[] parts = realNumber.split(Pattern.quote("."));
		char[] floatingValue = parts[1].toCharArray();
		if(floatingValue[0] >= '1') {                  				//rounds x.y to x++ if y>=1
			char[] chars = parts[0].toCharArray();
			chars[chars.length-1]++;
			return new String(chars);
		}
		return parts[0];											//returns x otherwise
	}
	private ApiHandler gasApiHandler;
}
