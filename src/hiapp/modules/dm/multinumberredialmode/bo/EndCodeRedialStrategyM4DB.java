package hiapp.modules.dm.multinumberredialmode.bo;

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

    public List<Map<String, Integer>> getDailyPhoneTypeDialCount() {
        List<Map<String, Integer>> listDailyPhoneTypeDialCount = new ArrayList<Map<String, Integer>>();

        for (int i=1; i<=15; i++) {
            List<DayOrderItem> dayPhoneTypeDialCountList = MultiNumberDetail.getDayPhoneTypeDialCount(i);
            Map<String, Integer> mapOnePhoneTypeDialCount = new HashMap<String, Integer>();
            for (DayOrderItem item : dayPhoneTypeDialCountList) {
                mapOnePhoneTypeDialCount.put(item.name, item.DialCount);
            }
            listDailyPhoneTypeDialCount.add(mapOnePhoneTypeDialCount);
        }

        return null;
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
        return Attribute.StageCount;
    }

    public int getStageDelayDays() {
        return Attribute.StageDelayDays;
    }

    public List<DayOrderItem> getDayPhoneTypeDialCount(int dayIndex) {
        return DayOrder.get("DayOrder" + dayIndex);
    }
}

class Attribute {
    int StageCount;
    String LoopType;
    int StageDelayDays;
}

class DayOrderItem {
    String name;
    String nameCh;
    int DialCount;
}

class EndCodeRedialStrategyItem {
    String EndCodeType;
    String EndCode;
    String IsCustStop; //"true",
    String IsPhoneStop;
    String isPresetDial;
    int sortNum;
}