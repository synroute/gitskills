package hiapp.modules.dm.hidialermode.bo;

public enum HidialerModeCustomerStateEnum {

    CREATED("CREATED"),
    APPENDED("APPENDED"),
    EXTRACTED("EXTRACTED"),       // 已经被HiDialer抽取
    WAIT_REDIAL("WAIT_REDIAL"),   // 等待重拨状态, 外呼策略处理结果
    LOSS_WAIT_REDIAL("LOSS_WAIT_REDIAL"),   //呼损重拨
    HIDIALER_LOSS_WAIT_REDIAL("HIDIALER_LOSS_WAIT_REDIAL"),   //HiDialer呼损重拨

    FINISHED("FINISHED"),     // 本客户拨打完成
    LOSS_FINISHED("LOSS_FINISHED"),     // 呼损重拨完成
    REVERT("REVERT"),         // 回退状态
    CANCELLED("CANCELLED"),   // 取消状态

    PHONECONNECTED("PHONECONNECTED"),
    SCREENPOPUP("SCREENPOPUP");

    private HidialerModeCustomerStateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

    static public HidialerModeCustomerStateEnum getFromString(String columnValue) {
        if (HidialerModeCustomerStateEnum.CREATED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.CREATED;

        if (HidialerModeCustomerStateEnum.APPENDED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.APPENDED;

        if (HidialerModeCustomerStateEnum.WAIT_REDIAL.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.WAIT_REDIAL;

        if (HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.LOSS_WAIT_REDIAL;

        if (HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.HIDIALER_LOSS_WAIT_REDIAL;

        if (HidialerModeCustomerStateEnum.FINISHED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.FINISHED;

        if (HidialerModeCustomerStateEnum.REVERT.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.REVERT;

        if (HidialerModeCustomerStateEnum.CANCELLED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.CANCELLED;

        if (HidialerModeCustomerStateEnum.PHONECONNECTED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.PHONECONNECTED;

        if (HidialerModeCustomerStateEnum.SCREENPOPUP.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.SCREENPOPUP;

        if (HidialerModeCustomerStateEnum.EXTRACTED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.EXTRACTED;

        if (HidialerModeCustomerStateEnum.LOSS_FINISHED.getName().equals(columnValue))
            return HidialerModeCustomerStateEnum.LOSS_FINISHED;

        return null;
    }

}

