package hiapp.modules.dm.singlenumbermode.bo;

public enum SingleNumberModeShareCustomerStateEnum {
    CREATED("created"),
    APPENDED("appended"),
    USING("using"),
    WAITREDIAL("waitredial"), // 等待再次拨打状态
    PRESET("preset"),         // 预约状态
    FINISHED("finished"),
    REVERT("revert");         // 回退状态

    private SingleNumberModeShareCustomerStateEnum(String name) {
        m_name = name;
    }

    private String m_name;
}
