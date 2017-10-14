package hiapp.modules.dm.multinumbermode.bo;

import java.util.Map;

public class EndCodeRedialStrategyM6 {

    // resultCodeType + resultCode <==> EndCodeRedialStrategyM6Item
    Map<String, EndCodeRedialStrategyM6Item>  mapEndCodeRedialStrategy;

    public void load() {

    }

    public EndCodeRedialStrategyM6Item getEndCodeRedialStrategyItem(String resultCodeType, String resultCode) {
        return mapEndCodeRedialStrategy.get(resultCodeType+resultCode);
    }

}


