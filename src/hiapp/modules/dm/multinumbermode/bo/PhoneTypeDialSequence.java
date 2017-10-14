package hiapp.modules.dm.multinumbermode.bo;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PhoneTypeDialSequence {

    // 拨打顺序 <==> 号码类型
    Map<Integer, Integer> mapPhoneTypeDialSequence;

    public int size() {return mapPhoneTypeDialSequence.size();}

    public Integer get(int dialSequence) { return mapPhoneTypeDialSequence.get(dialSequence);}

    public Integer getDialSequence(int phoneType) {
        for (int i=1; i<=10; i++) {
            if (mapPhoneTypeDialSequence.get(i).equals(phoneType))
                return i;
        }

        return null;
    }
}

