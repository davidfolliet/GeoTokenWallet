import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//GeoToken
//David Folliet
public class SettingsFileReader {
	private static final String etherscanApiKeyFileName = "etherscan_api_key";
	private static final String infuraApiKeyFileName = "infura_api_key";
	private String readFile(String fileName) {										//Assumes the file has 1 line containing the api key, obtains text file as a resource from jar file
		BufferedReader reader =  null;
		String line = null;
		try {
			InputStream in = getClass().getResourceAsStream(fileName); 
			reader = new BufferedReader(new InputStreamReader(in));
			line = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	public String getEtherscanApiKey() {											//read etherscan api key from settings file
		return readFile(etherscanApiKeyFileName);
	}
	public String getInfuraApiKey() {												//read infura api key from settings file
		return readFile(infuraApiKeyFileName);
	}
}
