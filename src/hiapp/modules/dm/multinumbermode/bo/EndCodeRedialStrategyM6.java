package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EndCodeRedialStrategyM6 {

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;


    //BizId <==> { resultCodeType + resultCode <==> EndCodeRedialStrategyM6Item }
    Map<Integer, Map<String, EndCodeRedialStrategyM6Item>>  mapBizIdVsEndCodeRedialStrategy;

    public void load() {

    }

    public EndCodeRedialStrategyM6Item getEndCodeRedialStrategyItem(int bizId, String resultCodeType, String resultCode) {
        /*Map<String, EndCodeRedialStrategyM6Item> mapEndCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == mapEndCodeRedialStrategy) {
            mapEndCodeRedialStrategy = new HashMap<String, EndCodeRedialStrategyM6Item>();
            mapBizIdVsEndCodeRedialStrategy.put(bizId, mapEndCodeRedialStrategy);
        }

        mapEndCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, mapEndCodeRedialStrategy);
        }


        return mapEndCodeRedialStrategy.get(resultCodeType+resultCode);*/
        return null;
    }


    private EndCodeRedialStrategyM6 getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        //EndCodeRedialStrategyFromDB endCodeRedialStrategyFromDB = new Gson().fromJson(jsonEndCodeRedialStrategy,
        //        EndCodeRedialStrategyFromDB.class);

        //return hiapp.modules.dm.multinumbermode.bo.EndCodeRedialStrategyM6.getInstance(endCodeRedialStrategyFromDB);
        return null;
    }

}


