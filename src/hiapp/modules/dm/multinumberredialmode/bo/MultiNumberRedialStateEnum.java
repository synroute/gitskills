package hiapp.modules.dm.multinumberredialmode.bo;

public enum MultiNumberRedialStateEnum {

    CREATED("CREATED"),
    APPENDED("APPENDED"),
    WAIT_DIAL("WAIT_DIAL"),  // 下个号码类型等待拨打状态
    PRESET_DIAL("PRESET_DIAL"),  // 预约拨打状态

    WAIT_NEXT_STAGE_DIAL("WAIT_NEXT_STAGE_DIAL"),

    FINISHED("FINISHED"),  // 本客户拨打完成
    REVERT("REVERT");      // 回退状态

    private MultiNumberRedialStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public MultiNumberRedialStateEnum getFromString(String columnValue) {
        if (MultiNumberRedialStateEnum.CREATED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.CREATED;

        if (MultiNumberRedialStateEnum.APPENDED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.APPENDED;

        if (MultiNumberRedialStateEnum.WAIT_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_DIAL;

        if (MultiNumberRedialStateEnum.PRESET_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.PRESET_DIAL;

        if (MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL;

        if (MultiNumberRedialStateEnum.FINISHED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.FINISHED;

        if (MultiNumberRedialStateEnum.REVERT.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.REVERT;

        return null;
    }

}

