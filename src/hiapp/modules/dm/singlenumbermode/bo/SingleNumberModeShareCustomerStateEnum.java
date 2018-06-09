package hiapp.modules.dm.singlenumbermode.bo;

public enum SingleNumberModeShareCustomerStateEnum {
    CREATED("CREATED"),
    APPENDED("APPENDED"),
    WAIT_NEXT_STAGE_DIAL("WAIT_NEXT_STAGE_DIAL"),   //等待下阶段拨打状态，有下次拨打时间
    PRESET_DIAL("PRESET_DIAL"),   // 预约拨打状态，有下次拨打时间
    LOSTCALL_WAIT_REDIAL("LOSTCALL_WAIT_REDIAL"),   //未接通，等待重拨状态，没有下次拨打时间，通过拨打次数控制
    FINISHED("FINISHED"),
    CANCELLED("CANCELLED"),
    REVERT("REVERT");         // 回退状态

    private SingleNumberModeShareCustomerStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public SingleNumberModeShareCustomerStateEnum getFromString(String columnValue) {
        if (SingleNumberModeShareCustomerStateEnum.CREATED.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.CREATED;

        if (SingleNumberModeShareCustomerStateEnum.APPENDED.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.APPENDED;

        if (SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.WAIT_NEXT_STAGE_DIAL;

        if (SingleNumberModeShareCustomerStateEnum.PRESET_DIAL.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.PRESET_DIAL;

        if (SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL;

        if (SingleNumberModeShareCustomerStateEnum.FINISHED.getName().equals(columnValue) || "拨打完成".equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.FINISHED;

        if (SingleNumberModeShareCustomerStateEnum.CREATED.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.CREATED;

        if (SingleNumberModeShareCustomerStateEnum.REVERT.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.REVERT;

        if (SingleNumberModeShareCustomerStateEnum.CANCELLED.getName().equals(columnValue))
           return SingleNumberModeShareCustomerStateEnum.CANCELLED;

        return null;
    }
}
