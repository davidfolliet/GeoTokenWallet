import java.util.Map;

//GeoToken
//David Folliet
public class ApiHandler {												//Base class for handling APIs
	public ApiHandler(JSONReader reader, String apiUri) {
		apiResponse = reader.getDataSet(WebGetter.getPage(apiUri));		//utilizes JSONReader (or subclass MultipleJSONReader) and WebGetter
	}
	protected Object getValue(String key) {   
		return ((Map) apiResponse).get(key);							//casts apiResponse to a Map object and returns value associated with key
	}
	protected Object getApiResponse() {									//getter for sub classes
		return apiResponse;
	}
	private Object apiResponse;											//Polymorphic: Can be a map or a list of maps
}
