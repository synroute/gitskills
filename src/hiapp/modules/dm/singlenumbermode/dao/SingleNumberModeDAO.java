package hiapp.modules.dm.singlenumbermode.dao;

import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SingleNumberModeDAO {
    public List<SingleNumberModeShareCustomerItem> getShareDataItemsByState(String shareBatchId,
                                                  List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户
        return new ArrayList<SingleNumberModeShareCustomerItem>();
    }

    public void setLoadedFlagShareDataItemsByState(String shareBatchId,
                                                   List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户
    }

    // 当天应该拨打的项
    public List<SingleNumberModeShareCustomerItem> getShareDataItemsByStateAndNextDialTime(String shareBatchId,
                                                   List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户

        return new ArrayList<SingleNumberModeShareCustomerItem>();
    }

    public void setLoadedFlagShareDataItemsByStateAndNextDialTime(String shareBatchId,
                                                                       List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户
    }

    // 从已加载复位成未加载状态
    public void resetLoadedFlag() {

    }


    // 对于需要再次拨打的，在更新状态到使用中的同时清除下次拨打时间？
}
