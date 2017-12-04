package hiapp.modules.dm.util;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomerStateEnum;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;

import java.text.SimpleDateFormat;
import java.util.*;

public class SQLUtil {

    public static String getSqlString(Object obj) {

        if (null == obj)
            return "null";

        if ( obj instanceof String) {
            return "'" + obj + "'";

        } else if (obj instanceof Integer) {
            return obj.toString();

        } else if (obj instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String strCurTime = format.format((Date)obj);
            return "TO_DATE('" + strCurTime + "', 'yyyy-mm-dd hh24:mi:ss')";
        } else if (obj instanceof Calendar) {
            Calendar curTime = (Calendar)obj;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String strCurTime = format.format(curTime.getTime());
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

    public static String stringCollectionToSqlString(Collection<String> stringCollection) {
        StringBuilder sb = new StringBuilder();
        for (String str: stringCollection) {
            sb.append("'").append(str).append("'");
            sb.append(",");
        }

        sb.deleteCharAt(sb.length()-1); // 移除最后的逗号
        return sb.toString();
    }

    public static String shareStatelistToSqlString(List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareCustomerStateList.size(); indx++) {
            SingleNumberModeShareCustomerStateEnum state = shareCustomerStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (shareCustomerStateList.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }

    public static String multiNumberPredictStatelistToSqlString(List<MultiNumberPredictStateEnum> shareCustomerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareCustomerStateList.size(); indx++) {
            MultiNumberPredictStateEnum state = shareCustomerStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (shareCustomerStateList.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }

    public static String hidialerModeCustomerStatelistToSqlString(List<HidialerModeCustomerStateEnum> customerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < customerStateList.size(); indx++) {
            HidialerModeCustomerStateEnum state = customerStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (customerStateList.size() - 1))
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

    public static String shareBatchStatelistToCommaSplitString(List<ShareBatchStateEnum> shareBatchStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareBatchStateList.size(); indx++ ) {
            ShareBatchStateEnum state = shareBatchStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (shareBatchStateList.size()-1))
                sb.append(",");
        }

        return sb.toString();
    }

}
