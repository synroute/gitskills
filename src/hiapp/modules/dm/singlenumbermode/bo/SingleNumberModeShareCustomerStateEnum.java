package hiapp.modules.dm.singlenumbermode.bo;

public enum SingleNumberModeShareCustomerStateEnum {
    CREATED("created"),
    APPENDED("appended"),
    WAIT_NEXT_PHASE_DAIL("wait_next_phase_dail"),   //等待下阶段拨打状态，有下次拨打时间
    PRESET_DIAL("preset_dial"),   // 预约拨打状态，有下次拨打时间
    LOSTCALL_WAIT_REDIAL("lostcall_wait_redial"),   //未接通，等待重拨状态，没有下次拨打时间，通过拨打次数控制
    FINISHED("finished"),
    REVERT("revert");         // 回退状态

    private SingleNumberModeShareCustomerStateEnum(String name) {
        m_name = name;
    }

    private String m_name;
}
