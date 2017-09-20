package hiapp.modules.dm.singlenumbermode.bo;

public enum RedialStateTypeEnum {

    //结束
    REDIAL_STATE_FINISHED("结束"), //"redial_state_finished"),

    //阶段重拨
    REDIAL_STATE_PHASE("阶段重拨"), //"redial_state_phase"),

    //预约
    REDIAL_STATE_PRESET("预约"), //redial_state_preset"),

    //未接通重拨
    REDIAL_STATE_LOSTCALL("未接通循环外拨"); //"redial_state_lostcall");

    private RedialStateTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

}
