import java.util.LinkedHashMap;
import java.util.Map;

//David Folliet
public class JSONReader {
	public Object getDataSet(String data) {
		Map map = new LinkedHashMap<String, String>();
		data = data.trim();
		data = JSONReader.getDataTrimFrontAndBack(data, '[', ']');
		data = JSONReader.getDataTrimFrontAndBack(data, '{', '}');
		String[] pieces = data.split(",|, ");						//split on ,
		for(int i=0; i<pieces.length; i++) {						//split each piece on : and store in map
			String[] pair = pieces[i].split(":");
			if(pair.length > 1)
				map.put(getDataTrimFrontAndBack(pair[0].trim(), '"', '"'), getDataTrimFrontAndBack(pair[1].trim(), '"', '"'));          //removes quotation marks and white space upon insertion
		}	
		return map;
	}
	protected static String getDataTrimFrontAndBack(String data, char front, char back) {
		String trimmedData = "";
		char[] ch = data.toCharArray();
		if(ch[0] != front && ch[ch.length-1] != back)
			return data;
		else if(ch[0] != front && ch[ch.length-1] == back) {
			for(int i=0; i<ch.length-1; i++) 
				trimmedData += ch[i];
		}else if(ch[0] == front && ch[ch.length-1] != back) {
			for(int i=1; i<ch.length; i++) 
				trimmedData += ch[i];
		}else {
			for(int i=1; i<ch.length-1; i++) 
				trimmedData += ch[i];
		}
		return trimmedData;
	}

}
