package hiapp.modules.dm.manualmode.bo;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.dao.DMDAO;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CustomerSharePool {

    @Autowired
    DMDAO dmDAO;

    @Autowired
    ManualModeDAO manualModeDAO;

    int MANUAL_MODE = 3;

    private void initialize() {

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();
        dmDAO.getGivenModeActiveShareBatchItems(MANUAL_MODE, shareBatchItems);



    }
}
