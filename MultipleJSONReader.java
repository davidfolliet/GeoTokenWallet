import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//David Folliet
public class MultipleJSONReader extends JSONReader {
	public Object getDataSet(String data) {
		List list = new LinkedList<Map>();					//create a list of multiple datasets 
		data = getDataTrimFrontAndBack(data, '{', '}');
		String[] pieces = data.split("\\["); 				// split header on '['
		list.add((Map) super.getDataSet(pieces[0])); 		// insert header (front)
		pieces = pieces[1].split("},");
		for (int i = 0; i < pieces.length; i++) 			// insert each JSON object
			list.add((Map) super.getDataSet(pieces[i]));	
		return list;
	}
}
