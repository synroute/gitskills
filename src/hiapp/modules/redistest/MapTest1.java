package hiapp.modules.redistest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shtou on 2018/4/16.
 */
public class MapTest1 {
    public static void main(String[] args) {
        Map<Integer, Map<Integer, String>> map1 = new HashMap<>();
        for (int i = 0; i < 5; i ++ ){
            HashMap<Integer, String> map = new HashMap<>();
            map.put(1, i+"a");
            map1.put(1, map);
        }
        System.out.println(map1);
    }
}
