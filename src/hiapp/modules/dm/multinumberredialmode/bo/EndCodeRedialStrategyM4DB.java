package hiapp.modules.dm.multinumberredialmode.bo;

import hiapp.modules.dm.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndCodeRedialStrategyM4DB {
    MultiNumberDetail MultiNumberDetail;
    List<EndCodeRedialStrategyItem> EndCodeRedialStrategy;

    public int getStageCount() {
        return MultiNumberDetail.getStageCount();
    }

    public int getStageDelayDays() {
        return MultiNumberDetail.getStageDelayDays();
    }

    public Map<Integer, Map<Integer, Integer>> getDailyPhoneTypeDialCount() {
        Map<Integer, Map<Integer, Integer>> mapDailyPhoneTypeDialCount = new HashMap<Integer, Map<Integer, Integer>>();

        for (int i = 1; i<= Constants.StageDayNum; i++) {
            List<DayOrderItem> dayPhoneTypeDialCountList = MultiNumberDetail.getDayPhoneTypeDialCount(i);
            if (null == dayPhoneTypeDialCountList)
                continue;

            Map<Integer, Integer> mapOnePhoneTypeDialCount = new HashMap<Integer, Integer>();
            for (DayOrderItem item : dayPhoneTypeDialCountList) {
                mapOnePhoneTypeDialCount.put(item.getDialType(), item.getDialCount());
            }
            mapDailyPhoneTypeDialCount.put(i, mapOnePhoneTypeDialCount);
        }

        return mapDailyPhoneTypeDialCount;
    }

    public Map<String, MultiNumberRedialStrategyEnum> getEndCodeRedialStrategy() {
        Map<String, MultiNumberRedialStrategyEnum> mapEndCodeRedialStrategy = new HashMap<String, MultiNumberRedialStrategyEnum>();
        for (EndCodeRedialStrategyItem strategyItem : EndCodeRedialStrategy) {
            if ("true".equals(strategyItem.IsCustStop))
                mapEndCodeRedialStrategy.put(strategyItem.EndCodeType + strategyItem.EndCode, MultiNumberRedialStrategyEnum.IsCustStop);
            else if ("true".equals(strategyItem.IsPhoneStop))
                mapEndCodeRedialStrategy.put(strategyItem.EndCodeType + strategyItem.EndCode, MultiNumberRedialStrategyEnum.IsPhoneStop);
            else if ("true".equals(strategyItem.isPresetDial))
                mapEndCodeRedialStrategy.put(strategyItem.EndCodeType + strategyItem.EndCode, MultiNumberRedialStrategyEnum.IsPresetDial);

        }
        return mapEndCodeRedialStrategy;
    }
}

class MultiNumberDetail {
    Attribute Attribute;
    Map<String, List<DayOrderItem>> DayOrder;

    public int getStageCount() {
        return Attribute.getStageCount();
    }

    public int getStageDelayDays() {
        return Attribute.getStageDelayDays();
    }

    public List<DayOrderItem> getDayPhoneTypeDialCount(int dayIndex) {
        return DayOrder.get("DayOrder" + dayIndex);
    }
}

class Attribute {
    public int getStageCount() {
        if (null == StageCount || StageCount.isEmpty())
            return 0;

        return Integer.valueOf(StageCount);
    }

    public String getLoopType() {
        return LoopType;
    }

    public int getStageDelayDays() {
        if (null == StageDelayDays || StageDelayDays.isEmpty())
            return 0;

        return Integer.valueOf(StageDelayDays);
    }

    private String StageCount;
    private String LoopType;
    private String StageDelayDays;
}

class DayOrderItem {
    public int getDialType() {
        if (null == dialType || dialType.isEmpty())
            return 0;

        return Integer.valueOf(dialType);
    }

    public String getName() {
        return name;
    }

    public String getNameCh() {
        return nameCh;
    }

    public int getDialCount() {
        if (null == DialCount || DialCount.isEmpty())
            return 0;

        return Integer.valueOf(DialCount);
    }

    private String dialType;
    private String name;
    private String nameCh;
    private String DialCount;
}

class EndCodeRedialStrategyItem {
    String EndCodeType;
    String EndCode;
    String IsCustStop; //"true",
    String IsPhoneStop;
    String isPresetDial;
    int sortNum;
}