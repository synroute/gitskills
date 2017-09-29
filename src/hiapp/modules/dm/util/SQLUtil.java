package hiapp.modules.dm.util;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SQLUtil {

    public static String getSqlString(Object obj) {

        if (null == obj)
            return "null";

        if ( obj instanceof String) {
            return "'" + obj + "'";

        } else if (obj instanceof Integer) {
            return obj.toString();

        } else if (obj instanceof Date) {
            Calendar curTime = Calendar.getInstance();
            curTime.setTime((Date)obj);
            String strCurTime = curTime.get(Calendar.YEAR) + "/" + (curTime.get(Calendar.MONTH)+1) + "/" + curTime.get(Calendar.DAY_OF_MONTH)
                    + " " + curTime.get(Calendar.HOUR_OF_DAY) + ":" + curTime.get(Calendar.MINUTE) + ":" + curTime.get(Calendar.SECOND);

            return "TO_DATE('" + strCurTime + "', 'yyyy-mm-dd hh24:mi:ss')";
        }

        return "";
    }

    public static String integerListToSqlString(List<Integer> integerList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < integerList.size(); indx++) {
            sb.append(integerList.get(indx));
            if (indx < (integerList.size() - 1))
                sb.append(",");
        }

        return sb.toString();
    }

    public static String stringListToSqlString(List<String> stringList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < stringList.size(); indx++) {
            sb.append("'").append(stringList.get(indx)).append("'");
            if (indx < (stringList.size() - 1))
                sb.append(",");
        }

        return sb.toString();
    }

    public static String shareBatchStatelistToSqlString(List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareCustomerStateList.size(); indx++) {
            SingleNumberModeShareCustomerStateEnum state = shareCustomerStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (shareCustomerStateList.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }

    public static String shareBatchItemlistToSqlString(List<ShareBatchItem> shareBatchItems) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareBatchItems.size(); indx++) {
            ShareBatchItem item = shareBatchItems.get(indx);
            sb.append("'").append(item.getShareBatchId()).append("'");
            if (indx < (shareBatchItems.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }
}
