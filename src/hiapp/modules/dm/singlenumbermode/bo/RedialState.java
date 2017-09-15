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

    public RedialStateTypeEnum getRedialStateType() {
        return redialStateType;
    }

    public void setRedialStateType(RedialStateTypeEnum redialStateType) {
        this.redialStateType = redialStateType;
    }

    public int getStageRedialDelayDays() {
        return stageRedialDelayDays;
    }

    public void setStageRedialDelayDays(int stageRedialDelayDays) {
        this.stageRedialDelayDays = stageRedialDelayDays;
    }

    public int getLoopRedialFirstDialDayDialCountLimit() {
        return loopRedialFirstDialDayDialCountLimit;
    }

    public void setLoopRedialFirstDialDayDialCountLimit(int loopRedialFirstDialDayDialCountLimit) {
        this.loopRedialFirstDialDayDialCountLimit = loopRedialFirstDialDayDialCountLimit;
    }

    public int getLoopRedialDialCount() {
        return loopRedialDialCount;
    }

    public void setLoopRedialDialCount(int loopRedialDialCount) {
        this.loopRedialDialCount = loopRedialDialCount;
    }

    public int getLoopRedialPerdayCountLimit() {
        return loopRedialPerdayCountLimit;
    }

    public void setLoopRedialPerdayCountLimit(int loopRedialPerdayCountLimit) {
        this.loopRedialPerdayCountLimit = loopRedialPerdayCountLimit;
    }

    public RedialState getLoopRedialCountExceedNextState() {
        return loopRedialCountExceedNextState;
    }

    public void setLoopRedialCountExceedNextState(RedialState loopRedialCountExceedNextState) {
        this.loopRedialCountExceedNextState = loopRedialCountExceedNextState;
    }

    String  name; //="结案"
    String  description; //="名称:结案;不再拨打"
    RedialStateTypeEnum redialStateType; //="结束"
    int stageRedialDelayDays; //=""
    int loopRedialFirstDialDayDialCountLimit; //=""
    int loopRedialDialCount; //=""
    int loopRedialPerdayCountLimit; //=""
    RedialState loopRedialCountExceedNextState; //=""
}
