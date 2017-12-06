package hiapp.modules.dm.multinumberredialmode.bo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EndCodeRedialStrategyM4 {

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;


    //BizId <==> { resultCodeType + resultCode <==> EndCodeRedialStrategyM4Item }
    Map<Integer, Map<String, EndCodeRedialStrategyM4Item>>  mapBizIdVsEndCodeRedialStrategy =
            new HashMap<Integer, Map<String, EndCodeRedialStrategyM4Item>>();

    public void load() {
    }

    public EndCodeRedialStrategyM4Item getEndCodeRedialStrategyItem(int bizId, String resultCodeType, String resultCode) {
        Map<String, EndCodeRedialStrategyM4Item> mapEndCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == mapEndCodeRedialStrategy) {
            mapEndCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, mapEndCodeRedialStrategy);
        }

        return mapEndCodeRedialStrategy.get(resultCodeType+resultCode);
    }

    private Map<String, EndCodeRedialStrategyM4Item> getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        List<EndCodeRedialStrategyM4ItemDB> endCodeRedialStrategyFromDBList = new Gson().fromJson(jsonEndCodeRedialStrategy,
                new TypeToken<List<EndCodeRedialStrategyM4ItemDB>>(){}.getType());

        Map<String, EndCodeRedialStrategyM4Item> mapEndCodeRedialStrategy = new HashMap<String, EndCodeRedialStrategyM4Item>();

        for (EndCodeRedialStrategyM4ItemDB dbItem : endCodeRedialStrategyFromDBList) {
            EndCodeRedialStrategyM4Item item = new EndCodeRedialStrategyM4Item();
            item.setResultCodeType(dbItem.getEndCodeType());
            item.setResultCode(dbItem.getEndCode());
            item.setDescription(dbItem.getDescription());
            item.setMaxRedialNum(dbItem.getRedialCount().isEmpty()?0:Integer.valueOf(dbItem.getRedialCount()));
            item.setRedialDelayMinutes(dbItem.getRedialMinutes().isEmpty()?0:Integer.valueOf(dbItem.getRedialMinutes()));
            item.setPresetDial(dbItem.getPresetDial().equals("true")?true:false);
            item.setPhoneTypeDialFinished(dbItem.getIsPhoneStop().equals("true")?true:false);
            item.setCustomerDialFinished(dbItem.getIsCustStop().equals("true")?true:false);

            mapEndCodeRedialStrategy.put(item.getResultCodeType() + item.getResultCode(), item);
        }

        return mapEndCodeRedialStrategy;
    }

}


