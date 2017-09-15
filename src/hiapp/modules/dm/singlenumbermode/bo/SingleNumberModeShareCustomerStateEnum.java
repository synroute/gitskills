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
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public SingleNumberModeShareCustomerStateEnum getFromString(String columnValue) {
        if (SingleNumberModeShareCustomerStateEnum.CREATED.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.CREATED;

        if (SingleNumberModeShareCustomerStateEnum.APPENDED.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.APPENDED;

        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_PHASE_DAIL;

        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.PRESET_DIAL;

        if (SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL;

        if (SingleNumberModeShareCustomerStateEnum.FINISHED.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.FINISHED;

        if (SingleNumberModeShareCustomerStateEnum.CREATED.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.CREATED;

        if (SingleNumberModeShareCustomerStateEnum.REVERT.equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.REVERT;

        return null;
    }
}
