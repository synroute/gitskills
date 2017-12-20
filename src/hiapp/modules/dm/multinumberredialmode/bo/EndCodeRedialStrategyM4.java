package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.Constants;

import java.util.Map;

public class EndCodeRedialStrategyM4 {
    int stageCount;
    String loopType;
    int stageDelayDays;

    // StageDayIndex <==> {phoneType <==> dialNum}
    Map<Integer, Map<Integer, Integer>> mapDailyPhoneTypeDialCount;

    // resultCodeType + resultCode <==> MultiNumberRedialStrategyEnum
    Map<String, MultiNumberRedialStrategyEnum> mapEndCodeRedialStrategy;

    public int getMaxStageCount() {
        return stageCount;
    }

    public int getStageDelayDays() {
        return  stageDelayDays;
    }

    /**
     *
     * @param dayIndex
     * @return  phoneType <==> dialNum
     */
    public Map<Integer, Integer> getGivenDayDialConfig(int dayIndex) {
        return mapDailyPhoneTypeDialCount.get(dayIndex);
    }

    public Boolean hasGivenDayDialConfig(int dayIndex) {
        if (!mapDailyPhoneTypeDialCount.containsKey(dayIndex))
            return false;

        // 如果所有号码类型的拨打次数都为零，那么认为该天没有配置策略
        Map<Integer, Integer> mapPhoneTypeDialNum = mapDailyPhoneTypeDialCount.get(dayIndex);
        for (Integer dialNum : mapPhoneTypeDialNum.values()) {
            if (null != dialNum && dialNum != 0)
                return true;
        }

        return false;
    }

    public Boolean hasDayConfigSince(int curDayIndex) {
        for (int dayIndex=curDayIndex + 1; dayIndex <= Constants.StageDayNum; dayIndex++ ) {
            if (hasGivenDayDialConfig(dayIndex))
                return true;
        }

        return false;
    }

    public MultiNumberRedialStrategyEnum getEndCodeRedialStrategy(String resultCodeType, String resultCode) {
        return mapEndCodeRedialStrategy.get(resultCodeType + resultCode);
    }

    public static EndCodeRedialStrategyM4 getInstance(EndCodeRedialStrategyM4DB strategyM4DB) {
        EndCodeRedialStrategyM4 strategyM4 = new EndCodeRedialStrategyM4();

        strategyM4.stageCount = strategyM4DB.getStageCount();
        strategyM4.stageDelayDays = strategyM4DB.getStageDelayDays();

        strategyM4.mapEndCodeRedialStrategy = strategyM4DB.getEndCodeRedialStrategy();
        strategyM4.mapDailyPhoneTypeDialCount = strategyM4DB.getDailyPhoneTypeDialCount();
        return strategyM4;
    }
}

