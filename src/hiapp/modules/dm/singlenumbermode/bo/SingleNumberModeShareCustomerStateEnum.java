package hiapp.modules.dm.singlenumbermode.bo;

public enum SingleNumberModeShareCustomerStateEnum {
    CREATED("created"),
    APPENDED("appended"),
    USING("using"),
    WAITREDIAL("waitredial"),
    PRESET("preset"),
    FINISHED("finished"),
    REVERT("revert");

    private SingleNumberModeShareCustomerStateEnum(String name) {
        m_name = name;
    }

    private String m_name;
}
