package hiapp.modules.dm.manualmode.timeoutpro;

/**
 * Created by shizhenshuang on 2018/6/19.
 */
public enum TimeoutTypeEnum {
    THE_DATA_POOL("拥有本数据池权限的人"),
    UP_DATA_POOL("拥有上级数据池权限的人"),
    BOTH_DATA_POOL("拥有本数据池权限和拥有上级数据池权限的人");

    private String name;

    public String getName() {
        return name;
    }

    TimeoutTypeEnum(String name) {
        this.name = name;
    }
}
