package hiapp.modules.dm.multinumberredialmode.bo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hiapp.modules.dmsetting.data.DmBizOutboundConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndCodeRedialStrategyM4 {
    int stageCount;
    String loopType;
    int stageDelayDays;

    List<Map<Integer, Integer>> dailyPhoneTypeDialCount;

    // resultCodeType + resultCode <==> MultiNumberRedialStrategyEnum
    Map<String, MultiNumberRedialStrategyEnum> mapEndCodeRedialStrategy;

    public int getMaxStageCount() {
        return stageCount;
    }

    public int getStageDelayDays() {
        return  stageDelayDays;
    }

    public Map<Integer, Integer> getPhoneTypeVsDialCount(int dayIndex) {
        return dailyPhoneTypeDialCount.get(dayIndex-1);
    }

    public MultiNumberRedialStrategyEnum getEndCodeRedialStrategy(String resultCodeType, String resultCode) {
        return mapEndCodeRedialStrategy.get(resultCodeType + resultCode);
    }

    public static EndCodeRedialStrategyM4 getInstance(EndCodeRedialStrategyM4DB strategyM4DB) {
        EndCodeRedialStrategyM4 strategyM4 = new EndCodeRedialStrategyM4();

        strategyM4.stageCount = strategyM4DB.getStageCount();
        strategyM4.stageDelayDays = strategyM4DB.getStageDelayDays();

        strategyM4.mapEndCodeRedialStrategy = strategyM4DB.getEndCodeRedialStrategy();
        strategyM4.dailyPhoneTypeDialCount = strategyM4DB.getDailyPhoneTypeDialCount();
        return strategyM4;
    }
}

