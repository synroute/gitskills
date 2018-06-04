package hiapp.modules.exam.utils;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
	public static Gson getGson() {
		Gson gson = new GsonBuilder()
		        .registerTypeAdapter(
		            new TypeToken<Map<String, Object>>(){}.getType(), 
		            new JsonDeserializer<Map<String, Object>>() {
		            @Override
		            public Map<String, Object> deserialize(
		            JsonElement json, Type typeOfT, 
		            JsonDeserializationContext context) throws JsonParseException {

		                Map<String, Object> treeMap = new HashMap<>();
		                JsonObject jsonObject = json.getAsJsonObject();
		                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
		                for (Map.Entry<String, JsonElement> entry : entrySet) {
		                    treeMap.put(entry.getKey(), entry.getValue());
		                }
		                return treeMap;
		            }
		        }).create();
		
		return gson;
	}
	
	public static String getStringcell(Cell cell){
		String value="";
		try{
			if("yyyy/mm/dd".equals(cell.getCellStyle().getDataFormatString()) || "m/d/yy".equals(cell.getCellStyle().getDataFormatString())
			        || "yy/m/d".equals(cell.getCellStyle().getDataFormatString()) || "mm/dd/yy".equals(cell.getCellStyle().getDataFormatString())
			        || "dd-mmm-yy".equals(cell.getCellStyle().getDataFormatString())|| "yyyy/m/d".equals(cell.getCellStyle().getDataFormatString())){
				
				if(cell.getDateCellValue()!=null){
					value= new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
				}
			}else if("m/d/yy h:mm".equals(cell.getCellStyle().getDataFormatString())||"yyyy/m/d h:mm".equals(cell.getCellStyle().getDataFormatString())){
				if(cell.getDateCellValue()!=null){
					value= new SimpleDateFormat("yyyy/MM/dd hh:mm").format(cell.getDateCellValue());
				}
			}
		}catch(IllegalStateException e){
			value=cell.getRichStringCellValue().toString();
		}
	
		return value;
	}
	
    public static Integer getIntegerValue(String value) {
    	Integer result=-1;
    	if("".equals(value)||value==null) {
    		return result;
    	}
    	if(value.contains(".")) {
    		Double douValue=Double.valueOf(value);
    		result=Integer.parseInt(new DecimalFormat("0").format(douValue));
    	}else {
    		result=Integer.valueOf(value);
    	}
    	
    	return result;
    }
}
