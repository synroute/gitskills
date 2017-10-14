package hiapp.modules.dm.singlenumbermode.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
<?xml version="1.0"?>
<AddSetting>
	<RedialState>
		<Item Name="结案" Description="名称:结案;不再拨打;" StateType="结束" StageRedialDelayDays="" LoopRedialFirstDialDayDialCountLimit="" LoopRedialDialCount="" LoopRedialPerdayCountLimit="" LoopRedialCountExceedNextState=""/>
		<Item Name="预约" Description="名称:预约;预约;" StateType="预约" StageRedialDelayDays="" LoopRedialFirstDialDayDialCountLimit="" LoopRedialDialCount="" LoopRedialPerdayCountLimit="" LoopRedialCountExceedNextState=""/>
	</RedialState>
	<EndCodeRedialStrategyM6 StageLimit="1" StageExceedNextState="结案">
		<Item EndCodeType="已联系上结案" EndCode="已联系上结案" RedialStateName="结案"/>
		<Item EndCodeType="已联系上未结案" EndCode="已联系上未结案" RedialStateName="预约"/>
		<Item EndCodeType="无效号码结案" EndCode="无效号码结案" RedialStateName="结案"/>
		<Item EndCodeType="无效号码未结案" EndCode="无效号码未结案" RedialStateName="预约"/>
		<Item EndCodeType="未联系上结案" EndCode="未联系上结案" RedialStateName="结案"/>
		<Item EndCodeType="未联系上未结案" EndCode="未联系上未结案" RedialStateName="预约"/>
		<Item EndCodeType="其他结案" EndCode="其他结案" RedialStateName="结案"/>
		<Item EndCodeType="其他未结案" EndCode="其他未结案" RedialStateName="预约"/>
		<Item EndCodeType="" EndCode="" RedialStateName=""/>
	</EndCodeRedialStrategyM6>
</AddSetting>
*/

public class EndCodeRedialStrategy {

    public void setEndCodeToRedialStateName(String resultCodeType, String resultCode, String redialStateName) {
        EndCodeToRedialStateNameMap.put(resultCodeType + "#" + resultCode, redialStateName);
    }

    public void setRedialStateItem(String name, RedialState redialState) {
        RedialStateMap.put(name, redialState);
    }

    public int getStageLimit() {
        return StageLimit;
    }

    public void setStageLimit(int stageLimit) {
        StageLimit = stageLimit;
    }

    public String getStageExceedNextStateName() {
        return StageExceedNextStateName;
    }

    public void setStageExceedNextStateName(String stageExceedNextStateName) {
        this.StageExceedNextStateName = stageExceedNextStateName;
    }

    public RedialState getNextRedialState(String resultCodeType, String resultCode) {
        String redialStateName = EndCodeToRedialStateNameMap.get(resultCodeType + resultCode);
        return RedialStateMap.get(redialStateName);
    }

    public void addRedialState(RedialState redialState) {
        RedialStateMap.put(redialState.getName(), redialState);
    }

    public void addEndCodeToRedialState(String endCodeType, String endCode, String stateName) {
        EndCodeToRedialStateNameMap.put(endCodeType + endCode, stateName);
    }

    Map<String, RedialState> RedialStateMap = new HashMap<String, RedialState>();

    Map<String, String> EndCodeToRedialStateNameMap = new HashMap<String, String>();  // key <=> EndCodeType + EndCode

    int StageLimit;
    String StageExceedNextStateName;


    public static EndCodeRedialStrategy getInstance(EndCodeRedialStrategyFromDB endCodeRedialStrategyFromDB) {

        EndCodeRedialStrategy endCodeRedialStrategy = new EndCodeRedialStrategy();

        List<RedialState> redialSateList = endCodeRedialStrategyFromDB.getRedialState();
        for (RedialState redialState : redialSateList) {
            endCodeRedialStrategy.addRedialState(redialState);
        }

        EndCodeRedialStrategyFromDB.EndCodeRedialStrategyItem endCodeRedialStrategyItem = endCodeRedialStrategyFromDB.getEndCodeRedialStrategy();

        EndCodeRedialStrategyFromDB.DataShow dataShow = endCodeRedialStrategyItem.getDataShow();
        endCodeRedialStrategy.setStageLimit(dataShow.getStageLimitNum());
        endCodeRedialStrategy.setStageExceedNextStateName(dataShow.getStageExceedNextState());


        List<EndCodeRedialStrategyFromDB.DataInfo> dataInfoList = endCodeRedialStrategyItem.getDataInfo();
        for (EndCodeRedialStrategyFromDB.DataInfo dataInfo : dataInfoList) {
            endCodeRedialStrategy.addEndCodeToRedialState(dataInfo.getEndCodeType(), dataInfo.getEndCode(), dataInfo.getRedialStateName());
        }

        return endCodeRedialStrategy;
    }

}
