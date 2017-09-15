package hiapp.modules.dm.singlenumbermode.bo;

public enum RedialStateTypeEnum {

    //结束
    REDIAL_STATE_FINISHED("redial_state_finished"),

    //阶段重拨
    REDIAL_STATE_PHASE("redial_state_phase"),

    //预约
    REDIAL_STATE_PRESET("redial_state_preset"),

    //未接通重拨
    REDIAL_STATE_LOSTCALL("redial_state_lostcall");

    private RedialStateTypeEnum(String name) {
        m_name = name;
    }

    private String m_name;

}
