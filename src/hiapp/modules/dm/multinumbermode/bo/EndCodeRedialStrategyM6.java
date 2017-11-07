package hiapp.modules.dm.multinumbermode.bo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hiapp.modules.dm.singlenumbermode.bo.EndCodeRedialStrategyFromDB;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EndCodeRedialStrategyM6 {

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;


    //BizId <==> { resultCodeType + resultCode <==> EndCodeRedialStrategyM6Item }
    Map<Integer, Map<String, EndCodeRedialStrategyM6Item>>  mapBizIdVsEndCodeRedialStrategy =
            new HashMap<Integer, Map<String, EndCodeRedialStrategyM6Item>>();

    public void load() {
    }

    public EndCodeRedialStrategyM6Item getEndCodeRedialStrategyItem(int bizId, String resultCodeType, String resultCode) {
        Map<String, EndCodeRedialStrategyM6Item> mapEndCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == mapEndCodeRedialStrategy) {
            mapEndCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, mapEndCodeRedialStrategy);
        }

        return mapEndCodeRedialStrategy.get(resultCodeType+resultCode);
    }

    private Map<String, EndCodeRedialStrategyM6Item> getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        List<EndCodeRedialStrategyM6ItemDB> endCodeRedialStrategyFromDBList = new Gson().fromJson(jsonEndCodeRedialStrategy,
                new TypeToken<List<EndCodeRedialStrategyM6ItemDB>>(){}.getType());

        Map<String, EndCodeRedialStrategyM6Item> mapEndCodeRedialStrategy = new HashMap<String, EndCodeRedialStrategyM6Item>();

        for (EndCodeRedialStrategyM6ItemDB dbItem : endCodeRedialStrategyFromDBList) {
            EndCodeRedialStrategyM6Item item = new EndCodeRedialStrategyM6Item();
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


