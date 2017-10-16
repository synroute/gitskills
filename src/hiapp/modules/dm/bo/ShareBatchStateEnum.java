package hiapp.modules.dm.bo;

/*
 * 共享批次信息表HASYS_DM_SID, 状态值
 */
public enum ShareBatchStateEnum {
    ENABLE("enable"), //启用
    ACTIVE("active"), //激活
    PAUSE("pause"),
    STOP("stop"),
    EXPIRED("expired"),
    RECOVER("recover");//回收

    private ShareBatchStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public ShareBatchStateEnum getFromString(String columnValue) {
        if (ShareBatchStateEnum.ENABLE.getName().equals(columnValue))
            return ShareBatchStateEnum.ENABLE;

        if (ShareBatchStateEnum.ACTIVE.getName().equals(columnValue))
            return ShareBatchStateEnum.ACTIVE;

        if (ShareBatchStateEnum.PAUSE.getName().equals(columnValue))
            return ShareBatchStateEnum.PAUSE;

        if (ShareBatchStateEnum.STOP.getName().equals(columnValue))
            return ShareBatchStateEnum.STOP;

        if (ShareBatchStateEnum.EXPIRED.getName().equals(columnValue))
            return ShareBatchStateEnum.EXPIRED;

        return null;
    }
}
