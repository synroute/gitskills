package hiapp.modules.dm.hidialermode.bo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EndCodeRedialStrategyM2 {

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;


    //BizId <==> { resultCodeType + resultCode <==> EndCodeRedialStrategyM6Item }
    Map<Integer, Map<String, EndCodeRedialStrategyM2Item>>  mapBizIdVsEndCodeRedialStrategy =
            new HashMap<Integer, Map<String, EndCodeRedialStrategyM2Item>>();

    public void load() {
    }

    public EndCodeRedialStrategyM2Item getEndCodeRedialStrategyItem(int bizId, String resultCodeType, String resultCode) {
        Map<String, EndCodeRedialStrategyM2Item> mapEndCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == mapEndCodeRedialStrategy) {
            mapEndCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, mapEndCodeRedialStrategy);
        }

        return mapEndCodeRedialStrategy.get(resultCodeType+resultCode);
    }

    private Map<String, EndCodeRedialStrategyM2Item> getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        List<EndCodeRedialStrategyM2ItemDB> endCodeRedialStrategyFromDBList = new Gson().fromJson(jsonEndCodeRedialStrategy,
                new TypeToken<List<EndCodeRedialStrategyM2ItemDB>>(){}.getType());

        Map<String, EndCodeRedialStrategyM2Item> mapEndCodeRedialStrategy = new HashMap<String, EndCodeRedialStrategyM2Item>();

        for (EndCodeRedialStrategyM2ItemDB dbItem : endCodeRedialStrategyFromDBList) {
            EndCodeRedialStrategyM2Item item = new EndCodeRedialStrategyM2Item();
            item.setResultCodeType(dbItem.getEndCodeType());
            item.setResultCode(dbItem.getEndCode());
            item.setMaxRedialNum(dbItem.getRedialCount().isEmpty()?0:Integer.valueOf(dbItem.getRedialCount()));
            item.setRedialDelayMinutes(dbItem.getRedialMinutes().isEmpty()?0:Integer.valueOf(dbItem.getRedialMinutes()));
            item.setCustomerDialFinished(dbItem.getIsCustStop().equals("true")?true:false);

            mapEndCodeRedialStrategy.put(item.getResultCodeType() + item.getResultCode(), item);
        }

        return mapEndCodeRedialStrategy;
    }

}


