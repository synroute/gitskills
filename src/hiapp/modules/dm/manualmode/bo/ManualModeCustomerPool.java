package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dmmanager.DataPoolRecord;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ManualModeCustomerPool {

    @Autowired
    DMDAO dmDAO;

    @Autowired
    ManualModeDAO manualModeDAO;

    // BizId <==> {shareBatchId <==> List<DataPoolRecord>}
    Map<Integer, Map<String, List<DataPoolRecord>>> mapPresetDialCustomerSharePool;

    public void initialize() {
    }

    public void addCustomer(DataPoolRecord customer) {

    }


}
