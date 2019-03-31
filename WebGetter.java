import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

//David Folliet
public class WebGetter {
	public static String getPage(String uri) {
		URL url=null;
		try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpsURLConnection connection = null;
		try {
			connection = (HttpsURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.addRequestProperty(RequestIdenentifier.KEY, RequestIdenentifier.VALUE);
        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
        String data = "";
        String line;
        try {
			while ((line = reader.readLine()) != null) 
			    data += line;
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return data;
	}	
	private final static class RequestIdenentifier{				//inner class
		public final static String KEY = "User-Agent";
		public final static String VALUE = "Mozilla/4.76";		
	}
}
