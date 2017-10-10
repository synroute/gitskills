package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.DataPoolRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class CustomerSharePool {

    @Autowired
    DMDAO dmDAO;

    @Autowired
    ManualModeDAO manualModeDAO;

    int MANUAL_MODE = 3;

    // BizId <==> {shareBatchId <==> List<DataPoolRecord>}
    Map<Integer, Map<String, List<DataPoolRecord>>> mapPresetDialCustomerSharePool;

    private void initialize() {

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        dmDAO.getGivenModeActiveShareBatchItems(MANUAL_MODE, shareBatchItems);

        int bizId = 0;

        List<ShareBatchItem> ShareBatchItems = null;
        List<DataPoolRecord> shareCustomerItems = new ArrayList<DataPoolRecord>();

        manualModeDAO.getGivenBizShareCustomers(bizId, ShareBatchItems, shareCustomerItems);

    }
}
