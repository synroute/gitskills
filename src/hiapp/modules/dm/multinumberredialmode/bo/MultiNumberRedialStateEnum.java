package hiapp.modules.dm.multinumberredialmode.bo;

public enum MultiNumberRedialStateEnum {

    CREATED("CREATED"),
    APPENDED("APPENDED"),
    EXTRACTED("EXTRACTED"),       // 已经被HiDialer抽取
    NEXT_PHONETYPE_WAIT_DIAL("NEXT_PHONETYPE_WAIT_DIAL"), // 下个号码类型等待拨打状态
    PRESET_DIAL("PRESET_DIAL"),   // 预约拨打状态
    WAIT_REDIAL("WAIT_REDIAL"),   // 等待重拨状态, 外呼策略处理结果
    LOSS_WAIT_REDIAL("LOSS_WAIT_REDIAL"),   //呼损重拨状态
    HIDIALER_LOSS_WAIT_REDIAL("HIDIALER_LOSS_WAIT_REDIAL"),   //HIDIALER呼损重拨状态
    FINISHED("FINISHED"),      // 本客户拨打完成
    REVERT("REVERT"),         // 回退状态

    PHONECONNECTED("PHONECONNECTED"),
    SCREENPOPUP("SCREENPOPUP");

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

        if (MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.NEXT_PHONETYPE_WAIT_DIAL;

        if (MultiNumberRedialStateEnum.PRESET_DIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.PRESET_DIAL;

        if (MultiNumberRedialStateEnum.WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.WAIT_REDIAL;

        if (MultiNumberRedialStateEnum.LOSS_WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.LOSS_WAIT_REDIAL;

        if (MultiNumberRedialStateEnum.HIDIALER_LOSS_WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.HIDIALER_LOSS_WAIT_REDIAL;

        if (MultiNumberRedialStateEnum.FINISHED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.FINISHED;

        if (MultiNumberRedialStateEnum.REVERT.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.REVERT;


        if (MultiNumberRedialStateEnum.PHONECONNECTED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.PHONECONNECTED;

        if (MultiNumberRedialStateEnum.SCREENPOPUP.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.SCREENPOPUP;

        if (MultiNumberRedialStateEnum.EXTRACTED.getName().equals(columnValue))
            return MultiNumberRedialStateEnum.EXTRACTED;

        return null;
    }

}

