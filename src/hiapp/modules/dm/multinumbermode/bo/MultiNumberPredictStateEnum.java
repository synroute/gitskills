package hiapp.modules.dm.multinumbermode.bo;

public enum MultiNumberPredictStateEnum {

    CREATED("CREATED"),
    APPENDED("APPENDED"),
    WAIT_DIAL("WAIT_DIAL"),       // 等待拨打状态
    PRESET_DIAL("PRESET_DIAL"),   // 预约拨打状态
    WAIT_REDIAL("WAIT_REDIAL"),   // 等待重拨状态
    LOSS_WAIT_REDIAL("LOSS_WAIT_REDIAL"),   //呼损重拨状态
    FINISHED("FINISHED"),      // 本客户拨打完成
    REVERT("REVERT"),         // 回退状态

    PHONECONNECTED("PHONECONNECTED"),
    SCREENPOPUP("SCREENPOPUP");

    private MultiNumberPredictStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public MultiNumberPredictStateEnum getFromString(String columnValue) {
        if (MultiNumberPredictStateEnum.CREATED.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.CREATED;

        if (MultiNumberPredictStateEnum.APPENDED.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.APPENDED;

        if (MultiNumberPredictStateEnum.WAIT_DIAL.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.WAIT_DIAL;

        if (MultiNumberPredictStateEnum.PRESET_DIAL.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.PRESET_DIAL;

        if (MultiNumberPredictStateEnum.WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.WAIT_REDIAL;

        if (MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.LOSS_WAIT_REDIAL;

        if (MultiNumberPredictStateEnum.FINISHED.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.FINISHED;

        if (MultiNumberPredictStateEnum.REVERT.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.REVERT;


        if (MultiNumberPredictStateEnum.PHONECONNECTED.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.PHONECONNECTED;

        if (MultiNumberPredictStateEnum.SCREENPOPUP.getName().equals(columnValue))
            return MultiNumberPredictStateEnum.SCREENPOPUP;

        return null;
    }

}

