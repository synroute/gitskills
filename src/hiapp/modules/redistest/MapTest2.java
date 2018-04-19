package hiapp.modules.redistest;

import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shtou on 2018/4/16.
 */
public class MapTest2 {
    public static void main(String[] args) {
        Map<Integer, HidialerModeCustomer> map1 = new HashMap<>();
        HidialerModeCustomer modeCustomer = new HidialerModeCustomer();
        modeCustomer.setEndCode("abc");
        map1.put(1,modeCustomer);
    }
}
