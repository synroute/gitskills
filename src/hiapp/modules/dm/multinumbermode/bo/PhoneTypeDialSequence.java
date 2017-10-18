package hiapp.modules.dm.multinumbermode.bo;

import hiapp.modules.dmsetting.DMBizPhoneType;
import hiapp.modules.dmsetting.data.DmBizPhoneTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PhoneTypeDialSequence {

    @Autowired
    DmBizPhoneTypeRepository dmBizPhoneTypeRepository;

    // BizId <==> {拨打顺序 <==> 号码类型}
    Map<Integer, Map<Integer, Integer>> mapPhoneTypeDialSequence;
    
    public Integer getPhoneTypeNum(int bizId) {
        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = getBizPhoneTypeDialSequence(bizId);
        return mapOneBizPhoneTypeDialSeq.size();
    }
    
    public Integer getPhoneType(int bizId, int phoneDialSequence) {
        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = getBizPhoneTypeDialSequence(bizId);
        return mapOneBizPhoneTypeDialSeq.get(phoneDialSequence);
    }

    public Integer getDialSequence(int bizId, int phoneType) {
        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = getBizPhoneTypeDialSequence(bizId);

        for (int i = 1; i <= 10; i++) {
            if (mapOneBizPhoneTypeDialSeq.get(i).equals(phoneType))
                return i;
        }

        return null;
    }

    public Integer getNextDialPhoneType(int bizId, int curDialPhoneType) {
        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = getBizPhoneTypeDialSequence(bizId);

        for (int dialIndex = 1; dialIndex <= mapOneBizPhoneTypeDialSeq.size(); dialIndex++) {
            if (curDialPhoneType != mapOneBizPhoneTypeDialSeq.get(dialIndex))
                continue;

            if (dialIndex == mapOneBizPhoneTypeDialSeq.size())
                return null;

            return mapOneBizPhoneTypeDialSeq.get(dialIndex + 1);
        }

        return null;
    }

    public Integer getPhoneDialSequence(int bizId, int phoneType) {

        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = getBizPhoneTypeDialSequence(bizId);

        for (int dialIndex = 1; dialIndex <= mapOneBizPhoneTypeDialSeq.size(); dialIndex++) {
            if (phoneType != mapOneBizPhoneTypeDialSeq.get(dialIndex))
                continue;

            return dialIndex;
        }

        return null;
    }

    private Map<Integer, Integer> getBizPhoneTypeDialSequence(int bizId) {
        Map<Integer, Integer> mapOneBizPhoneTypeDialSeq = mapPhoneTypeDialSequence.get(bizId);
        if (null != mapOneBizPhoneTypeDialSeq)
            return  mapOneBizPhoneTypeDialSeq;

        mapOneBizPhoneTypeDialSeq = new HashMap<Integer, Integer>();

        List<DMBizPhoneType> oneBizPhoneTypeList = dmBizPhoneTypeRepository.dmGetAllBizPhoneType(String.valueOf(bizId));
        for (DMBizPhoneType dmBizPhoneType : oneBizPhoneTypeList) {
            mapOneBizPhoneTypeDialSeq.put(dmBizPhoneType.getDialOrder(), dmBizPhoneType.getDialType());
        }

        mapPhoneTypeDialSequence.put(bizId, mapOneBizPhoneTypeDialSeq);
        return mapOneBizPhoneTypeDialSeq;
    }

}
