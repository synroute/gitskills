package hiapp.modules.dm.multinumbermode.bo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

public class EndCodeRedialStrategy {

    // resultCodeType + resultCode <==> EndCodeRedialStrategyItem
    Map<String, EndCodeRedialStrategyItem>  mapEndCodeRedialStrategy;

    public void load() {

    }

}

class EndCodeRedialStrategyItem {
    String resultCodeType;
    String resultCode;
    String description;
    Boolean customerDialFinished;
    Boolean phoneTypeDialFinished;
    int redialDelayMinutes;
    int maxRedialNum;
    Boolean presetDial;
}
