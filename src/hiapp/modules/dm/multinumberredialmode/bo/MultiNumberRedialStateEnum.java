package hiapp.modules.dm.multinumberredialmode.bo;

public enum MultiNumberRedialStateEnum {

    CREATED("CREATED"),
    APPENDED("APPENDED"),
    NEXT_PHONETYPE_WAIT_DIAL("NEXT_PHONETYPE_WAIT_DIAL"),  // 下个号码类型等待拨打状态
    PRESET_DIAL("PRESET_DIAL"),  // 预约拨打状态

    WAIT_REDIAL("WAIT_REDIAL"),  // 当前号码重新拨打状态，无等待时间

    WAIT_NEXT_DAY_DIAL("WAIT_NEXT_DAY_DIAL"),  // 当天拨打完毕，等待下一天拨打
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

        if (MultiNumberRedialStateEnum.WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_REDIAL;

        if (MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL;

        if (MultiNumberRedialStateEnum.PRESET_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.PRESET_DIAL;

        if (MultiNumberRedialStateEnum.WAIT_NEXT_DAY_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_NEXT_DAY_DIAL;

        if (MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_NEXT_STAGE_DIAL;

        if (MultiNumberRedialStateEnum.FINISHED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.FINISHED;

        if (MultiNumberRedialStateEnum.REVERT.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.REVERT;

        return null;
    }

}

