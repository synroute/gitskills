package hiapp.modules.dm.singlenumbermode.bo;

public enum RedialStateTypeEnum {

    //结束
    REDIAL_STATE_FINISHED("结束"), //"redial_state_finished"),

    //阶段重拨
    REDIAL_STATE_STAGE("阶段重拨"), //"redial_state_stage"),

    //预约
    REDIAL_STATE_PRESET("预约"), //redial_state_preset"),

    //未接通重拨
    REDIAL_STATE_LOSTCALL("未接通循环拨打"); //"redial_state_lostcall");

    private RedialStateTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public RedialStateTypeEnum getFromString(String name) {
        if (RedialStateTypeEnum.REDIAL_STATE_LOSTCALL.getName().equals(name))
            return RedialStateTypeEnum.REDIAL_STATE_LOSTCALL;

        if (RedialStateTypeEnum.REDIAL_STATE_STAGE.getName().equals(name))
            return RedialStateTypeEnum.REDIAL_STATE_STAGE;

        if (RedialStateTypeEnum.REDIAL_STATE_PRESET.getName().equals(name))
            return RedialStateTypeEnum.REDIAL_STATE_PRESET;

        if (RedialStateTypeEnum.REDIAL_STATE_FINISHED.getName().equals(name))
            return RedialStateTypeEnum.REDIAL_STATE_FINISHED;

        return null;
    }


}
