package hiapp.modules.dm.bo;

/*
 * 共享批次信息表HASYS_DM_SID, 状态值
 */
public enum ShareBatchStateEnum {
    ENABLE("enable"), //启用
    ACTIVE("active"), //激活
    PAUSE("pause"),
    STOP("stop"),
    EXPIRED("expired");

    private ShareBatchStateEnum(String name) {
        m_name = name;
    }

    private String m_name;
}
