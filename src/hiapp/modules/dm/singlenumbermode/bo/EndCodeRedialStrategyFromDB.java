package hiapp.modules.dm.singlenumbermode.bo;

import java.util.ArrayList;
import java.util.List;

public class EndCodeRedialStrategyFromDB {

    public List<RedialState> getRedialState() {
        return RedialState;
    }

    public EndCodeRedialStrategyItem getEndCodeRedialStrategy() {
        return EndCodeRedialStrategy.get(0);
    }


    class EndCodeRedialStrategyItem {
        public DataShow getDataShow() {
            return dataShow.get(0);
        }

        public void addDataShow(DataShow dataShowItem) {
            dataShow.add(dataShowItem);
        }

        public void addDataInfo(DataInfo dataInfoItem) {
            dataInfo.add(dataInfoItem);
        }

        public List<DataInfo> getDataInfo() {
            return dataInfo;
        }

        List<DataShow> dataShow = new ArrayList<DataShow>();
        List<DataInfo> dataInfo = new ArrayList<DataInfo>();
    }

    class DataShow {
        public String getStageLimit() {
            return stageLimit;
        }

        public int getStageLimitNum() {
            return Integer.parseInt(stageLimit);
        }

        public void setStageLimit(String stageLimit) {
            this.stageLimit = stageLimit;
        }

        public String getStageExceedNextState() {
            return stageExceedNextState;
        }

        public void setStageExceedNextState(String stageExceedNextState) {
            this.stageExceedNextState = stageExceedNextState;
        }

        String stageLimit;
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

        public String getEndCodeDescription() {
            return endCodeDescription;
        }

        public void setEndCodeDescription(String endCodeDescription) {
            this.endCodeDescription = endCodeDescription;
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
        String endCodeDescription;
        String redialStateName;
        String redialStateDec;
    }

    public void addRedialState(RedialState state) {
        RedialState.add(state);
    }

    public void addEndCodeRedialStrategyItem() {
        DataShow dataShow = new DataShow();
        dataShow.setStageLimit("10");
        dataShow.setStageExceedNextState("nextStateName");

        DataInfo dataInfo = new DataInfo();
        dataInfo.setEndCode("endCode");
        dataInfo.setEndCodeType("endCodeType");
        dataInfo.setEndCodeDescription("enddesc");
        dataInfo.setRedialStateName("stateName");
        dataInfo.setRedialStateDec("statdesc");

        EndCodeRedialStrategyItem endCodeRedialStrategyItem = new EndCodeRedialStrategyItem();
        endCodeRedialStrategyItem.addDataShow(dataShow);
        endCodeRedialStrategyItem.addDataInfo(dataInfo);
        endCodeRedialStrategyItem.addDataInfo(dataInfo);

        EndCodeRedialStrategy.add(endCodeRedialStrategyItem);
    }


    List<RedialState> RedialState = new ArrayList<RedialState>();

    List<EndCodeRedialStrategyItem> EndCodeRedialStrategy = new ArrayList<EndCodeRedialStrategyItem>();
}


