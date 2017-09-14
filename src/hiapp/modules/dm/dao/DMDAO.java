package hiapp.modules.dm.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DMDAO {

    /*
     *  所有业务的共享批次
     */
    public List<ShareBatchItem> getCurDayShareBatchItemsByState(List<ShareBatchStateEnum> shareBatchStateList) {
        return null;
    }

    public  void activateCurDayShareBatchByStartTime() {

    }

}


