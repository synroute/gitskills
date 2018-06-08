package hiapp.modules.dm.multinumberredialmode.bo;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MultiNumberRedialStrategy {

    @Autowired
    private DmBizOutboundConfigRepository dmBizOutboundConfig;


    //BizId <==> EndCodeRedialStrategyM4
    Map<Integer, EndCodeRedialStrategyM4>  mapBizIdVsEndCodeRedialStrategy = new HashMap<Integer, EndCodeRedialStrategyM4>();

    public void load() {
    }

    public EndCodeRedialStrategyM4 getEndCodeRedialStrategyItem(int bizId) {
        EndCodeRedialStrategyM4 endCodeRedialStrategy = mapBizIdVsEndCodeRedialStrategy.get(bizId);
        if (null == endCodeRedialStrategy) {
            endCodeRedialStrategy = getEndCodeRedialStrategyByBizId(bizId);
            mapBizIdVsEndCodeRedialStrategy.put(bizId, endCodeRedialStrategy);
        }

        return endCodeRedialStrategy;
    }

    private EndCodeRedialStrategyM4 getEndCodeRedialStrategyByBizId(int bizId) {
        String jsonEndCodeRedialStrategy = dmBizOutboundConfig.dmGetAllBizOutboundSetting(bizId);

        EndCodeRedialStrategyM4DB endCodeRedialStrategyFromDB = new Gson().fromJson(jsonEndCodeRedialStrategy,
                new TypeToken<EndCodeRedialStrategyM4DB>(){}.getType());


        EndCodeRedialStrategyM4 endCodeRedialStrategy = EndCodeRedialStrategyM4.getInstance(endCodeRedialStrategyFromDB);
        return endCodeRedialStrategy;
    }

}
