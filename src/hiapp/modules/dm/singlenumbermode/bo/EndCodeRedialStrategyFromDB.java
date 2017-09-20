package hiapp.modules.dm.singlenumbermode.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndCodeRedialStrategyFromDB {

    public List<RedialState> getRedialSate() {
        return RedialSate;
    }

    public EndCodeRedialStrategyItem getEndCodeRedialStrategy() {
        return EndCodeRedialStrategy.get(0);
    }


    class EndCodeRedialStrategyItem {
        public DataShow getDataShow() {
            return dataShow;
        }

        public void setDataShow(DataShow dataShow) {
            this.dataShow = dataShow;
        }

        public void addDataInfo(DataInfo dataInfoItem) {
            dataInfo.add(dataInfoItem);
        }

        public List<DataInfo> getDataInfo() {
            return dataInfo;
        }

        DataShow dataShow;
        List<DataInfo> dataInfo = new ArrayList<DataInfo>();
    }

    class DataShow {
        public int getStageLimit() {
            return stageLimit;
        }

        public void setStageLimit(int stageLimit) {
            this.stageLimit = stageLimit;
        }

        public String getStageExceedNextState() {
            return stageExceedNextState;
        }

        public void setStageExceedNextState(String stageExceedNextState) {
            this.stageExceedNextState = stageExceedNextState;
        }

        int stageLimit;
        String stageExceedNextState;
    }

    class DataInfo {
        public String getEndCodeType() {
            return endCodeType;
        }

        public void setEndCodeType(String endCodeType) {
            this.endCodeType = endCodeType;
        }

        public String getEndCode() {
            return endCode;
        }

        public void setEndCode(String endCode) {
            this.endCode = endCode;
        }

        public String getEndCodedescription() {
            return endCodedescription;
        }

        public void setEndCodedescription(String endCodedescription) {
            this.endCodedescription = endCodedescription;
        }

        public String getRedialStateName() {
            return redialStateName;
        }

        public void setRedialStateName(String redialStateName) {
            this.redialStateName = redialStateName;
        }

        public String getRedialStateDec() {
            return redialStateDec;
        }

        public void setRedialStateDec(String redialStateDec) {
            this.redialStateDec = redialStateDec;
        }

        String endCodeType;
        String endCode;
        String endCodedescription;
        String redialStateName;
        String redialStateDec;
    }

    public void addRedialState(RedialState state) {
        RedialSate.add(state);
    }

    public void addEndCodeRedialStrategyItem() {
        DataShow dataShow = new DataShow();
        dataShow.setStageLimit(10);
        dataShow.setStageExceedNextState("nextStateName");

        DataInfo dataInfo = new DataInfo();
        dataInfo.setEndCode("endCode");
        dataInfo.setEndCodeType("endCodeType");
        dataInfo.setEndCodedescription("enddesc");
        dataInfo.setRedialStateName("stateName");
        dataInfo.setRedialStateDec("statdesc");

        EndCodeRedialStrategyItem endCodeRedialStrategyItem = new EndCodeRedialStrategyItem();
        endCodeRedialStrategyItem.setDataShow(dataShow);
        endCodeRedialStrategyItem.addDataInfo(dataInfo);
        endCodeRedialStrategyItem.addDataInfo(dataInfo);

        EndCodeRedialStrategy.add(endCodeRedialStrategyItem);
    }


    List<RedialState> RedialSate = new ArrayList<RedialState>();

    List<EndCodeRedialStrategyItem> EndCodeRedialStrategy = new ArrayList<EndCodeRedialStrategyItem>();
}


