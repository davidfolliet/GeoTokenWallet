import java.math.BigDecimal;
import java.math.BigInteger;

//GeoToken
//David Folliet
public class UnitConverter {
	public final static int STANDARD_TOKEN_NUMBER_OF_DECIMALS = 18;
	public UnitConverter() {
		setEthApiHandler();
	}
	public void setEthApiHandler() {                               //can be used by ui to update eth price
		ethApiHandler = new EthApiHandler();
	}
	public EthApiHandler getEthApiHandler() {
		return (EthApiHandler) ethApiHandler;
	}	
	public static BigInteger getStandardSmallestTokenUnits(BigDecimal numberOfTokens) {                          
		return numberOfTokens.multiply(new BigDecimal(Math.pow(10, STANDARD_TOKEN_NUMBER_OF_DECIMALS))).toBigInteger();
	}
	public static boolean isValidStandardNumberOfTokens(BigDecimal numberOfTokens) {                                       
		if(numberOfTokens.equals(new BigDecimal("0")))
			return false;
		return numberOfTokens.multiply(new BigDecimal(Math.pow(10, STANDARD_TOKEN_NUMBER_OF_DECIMALS))).intValue() % 1 == 0;
	}
	public String getUsdValue(String ethValue) {
		return new BigDecimal(ethValue).multiply(new BigDecimal(getEthApiHandler().getPriceUsd())).toPlainString();
	}
	public String getBtcValue(String ethValue) {
		return new BigDecimal(ethValue).multiply(new BigDecimal(getEthApiHandler().getPriceBtc())).toPlainString();
	}
	public static BigDecimal getGasFromHexValue(String hexGasValue) {
		char [] gasChars = hexGasValue.toCharArray();	//trim the leading 0x
		hexGasValue = "";
		for(int i=2; i<gasChars.length; i++)
			hexGasValue += gasChars[i];
		BigInteger gas = new BigInteger(hexGasValue, 16);
		return new BigDecimal(gas.toString()); 
	}
	public static BigDecimal getTransactionFeeEth(String gasUsedHex, BigInteger gasPrice){
		return getGasFromHexValue(gasUsedHex).multiply(new BigDecimal(gasPrice.toString())).divide(new BigDecimal(Math.pow(10, STANDARD_TOKEN_NUMBER_OF_DECIMALS)));
	}
	
	private ApiHandler ethApiHandler;
}
