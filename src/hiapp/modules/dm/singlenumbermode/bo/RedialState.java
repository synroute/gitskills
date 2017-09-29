package hiapp.modules.dm.singlenumbermode.bo;

public class RedialState {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RedialStateTypeEnum getStateTypeEnum() {
        return RedialStateTypeEnum.getFromString(stateType);
    }

    public String getStateType() { return stateType; }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    public String getLoopRedialCountExceedNextState() {
        return loopRedialCountExceedNextState;
    }

    public void setLoopRedialCountExceedNextState(String loopRedialCountExceedNextState) {
        this.loopRedialCountExceedNextState = loopRedialCountExceedNextState;
    }

    public String getStageRedialDelayDays() {
        return stageRedialDelayDays;
    }

    public int getStageRedialDelayDaysNum() {
        return Integer.parseInt(stageRedialDelayDays);
    }


    public void setStageRedialDelayDays(String stageRedialDelayDays) {
        this.stageRedialDelayDays = stageRedialDelayDays;
    }

    public String getLoopRedialFirstDialDayDialCountLimit() {
        return loopRedialFirstDialDayDialCountLimit;
    }

    public int getLoopRedialFirstDialDayDialCountLimitNum() {
        return Integer.parseInt(loopRedialFirstDialDayDialCountLimit);
    }

    public void setLoopRedialFirstDialDayDialCountLimit(String loopRedialFirstDialDayDialCountLimit) {
        this.loopRedialFirstDialDayDialCountLimit = loopRedialFirstDialDayDialCountLimit;
    }

    public String getLoopRedialDialCount() {
        return loopRedialDialCount;
    }

    public int getLoopRedialDialCountNum() {
        return Integer.parseInt(loopRedialDialCount);
    }

    public void setLoopRedialDialCount(String loopRedialDialCount) {
        this.loopRedialDialCount = loopRedialDialCount;
    }

    public String getLoopRedialPerdayCountLimit() {
        return loopRedialPerdayCountLimit;
    }

    public int getLoopRedialPerdayCountLimitNum() {
        return Integer.parseInt(loopRedialPerdayCountLimit);
    }

    public void setLoopRedialPerdayCountLimit(String loopRedialPerdayCountLimit) {
        this.loopRedialPerdayCountLimit = loopRedialPerdayCountLimit;
    }

    String name; //="结案"
    String stateType; //="结束"
    String stageRedialDelayDays; //=""
    String loopRedialFirstDialDayDialCountLimit; //=""
    String loopRedialDialCount; //=""
    String loopRedialPerdayCountLimit; //=""
    String loopRedialCountExceedNextState; //=""
    String description; //="名称:结案;不再拨打"

}
