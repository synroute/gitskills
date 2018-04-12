package hiapp.modules.exam.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
}
