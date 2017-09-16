package hiapp.modules.dm.singlenumbermode.bo;

import org.springframework.stereotype.Component;

import java.util.Map;

/*
<?xml version="1.0"?>
<AddSetting>
	<RedialState>
		<Item Name="结案" Description="名称:结案;不再拨打;" StateType="结束" StageRedialDelayDays="" LoopRedialFirstDialDayDialCountLimit="" LoopRedialDialCount="" LoopRedialPerdayCountLimit="" LoopRedialCountExceedNextState=""/>
		<Item Name="预约" Description="名称:预约;预约;" StateType="预约" StageRedialDelayDays="" LoopRedialFirstDialDayDialCountLimit="" LoopRedialDialCount="" LoopRedialPerdayCountLimit="" LoopRedialCountExceedNextState=""/>
	</RedialState>
	<EndCodeRedialStrategy StageLimit="1" StageExceedNextState="结案">
		<Item EndCodeType="已联系上结案" EndCode="已联系上结案" RedialStateName="结案"/>
		<Item EndCodeType="已联系上未结案" EndCode="已联系上未结案" RedialStateName="预约"/>
		<Item EndCodeType="无效号码结案" EndCode="无效号码结案" RedialStateName="结案"/>
		<Item EndCodeType="无效号码未结案" EndCode="无效号码未结案" RedialStateName="预约"/>
		<Item EndCodeType="未联系上结案" EndCode="未联系上结案" RedialStateName="结案"/>
		<Item EndCodeType="未联系上未结案" EndCode="未联系上未结案" RedialStateName="预约"/>
		<Item EndCodeType="其他结案" EndCode="其他结案" RedialStateName="结案"/>
		<Item EndCodeType="其他未结案" EndCode="其他未结案" RedialStateName="预约"/>
		<Item EndCodeType="" EndCode="" RedialStateName=""/>
	</EndCodeRedialStrategy>
</AddSetting>
*/

@Component
public class EndCodeRedialStrategy {

    Map<String, RedialState> m_mapEndCodeVsRedialState;  // key <=> EndCodeType + EndCode
    int StageLimit;
    RedialState stageExceedNextState;

    public RedialState getNextRedialState(String resultCodeType, String resultCode) {
        return m_mapEndCodeVsRedialState.get(resultCodeType + resultCode);
    }
}
