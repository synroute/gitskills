package hiapp.modules.dm.singlenumbermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 修改操作，需要产生历史
 */

@Repository
public class SingleNumberModeDAO extends BaseRepository {
    /*
     * 仅处理的激活状态的共享批次包括的客户
     */
    public Boolean getShareDataItemsByState(List<ShareBatchItem> ShareBatchItems,
                                            List<SingleNumberModeShareCustomerStateEnum> shareDataStateList,
                                            /*OUT*/ List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        int id;
        int bizId;
        String shareBatchId;
        String importBatchId;
        String customerId;
        SingleNumberModeShareCustomerStateEnum state;
        int modifyId;
        String modifyUserId;
        Date modifyTime;
        String modifyDesc;
        String customerCallId; //客户呼叫流水号
        String endCodeType;
        String endCode;
        Date lastDailTime;    //最近一次拨打时间
        Date nextDialTime;    //下次拨打时间
        int curDayLostCallCount;   //当天未接通次数
        int curRedialStageCount; //仅用于阶段拨打
        Boolean userUseState; //是否已经被坐席人员抽取
        Boolean isLoaded;   //是否已经加载到内存
        Date lostCallFirstDay; //第一次未接通日期
        Date lostCallCurDay; //当前未接通日期
        int lostCallTotalCount; //未接通总次数

        // 非本表字段
        Date shareBatchBeginTime; //

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BusinessID, ShareId, IID, CID, State, ModifyID, " +
                    "ModifyUserId, ModifyTime, ModifyDesc, CustomerCallID, EndCodeType, EndCode, LastDialTime, " +
                    "NextDialTime, CurDayLostCallCount, CurRedialStageCount, UserUseState, LostCallFirstDay, LostCallCurDay, " +
                    "LostCallTotalCount FROM HASYS_DM_B1C_DATAM3 WHERE ");
            sqlBuilder.append(" ShareId IN (").append(shareBatchItemlistToCommaSplitString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND State IN (").append(shareBatchStatelistToCommaSplitString(shareDataStateList)).append(")");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getTime(9));
                item.setModifyDesc(rs.getString(10));
                item.setCustomerCallId(rs.getString(11));
                item.setEndCodeType(rs.getString(12));
                item.setEndCode(rs.getString(13));
                item.setLastDailTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setCurDayLostCallCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getBoolean(18));
                item.setLostCallFirstDay(rs.getTime(19));
                item.setLostCallCurDay(rs.getTime(20));
                item.setLostCallTotalCount(rs.getInt(21));

                item.setShareBatchStartTime(mapShareBatchIdVsShareBatchItem.get(item.getShareBatchId()).getStartTime());

                shareCustomerItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public void setLoadedFlagShareDataItemsByState(String shareBatchId,
                                                   List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户
    }

    // 当天应该拨打的项
    public Boolean getShareDataItemsByStateAndNextDialTime(List<ShareBatchItem> ShareBatchItems,
                                                           List<SingleNumberModeShareCustomerStateEnum> shareDataStateList,
                                                           /*OUT*/List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        try {
            dbConn = this.getDbConnection();

            Calendar curDay = Calendar.getInstance();
            curDay.setTime(new Date());
            curDay.add(Calendar.DAY_OF_MONTH, 1);
            curDay.set(Calendar.HOUR_OF_DAY, 0);
            curDay.set(Calendar.MINUTE, 0);
            curDay.set(Calendar.SECOND, 0);
            curDay.set(Calendar.MILLISECOND, 0);

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BusinessID, ShareId, IID, CID, State, ModifyID, " +
                    "ModifyUserId, ModifyTime, ModifyDesc, CustomerCallID, EndCodeType, EndCode, LastDialTime, " +
                    "NextDialTime, CurDayLostCallCount, CurRedialStageCount, UserUseState, LostCallFirstDay, LostCallCurDay, " +
                    "LostCallTotalCount FROM HASYS_DM_B1C_DATAM3 WHERE ");
            sqlBuilder.append(" ShareId IN (").append(shareBatchItemlistToCommaSplitString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND State IN (").append(shareBatchStatelistToCommaSplitString(shareDataStateList)).append(")");
            sqlBuilder.append(" AND NextDialTime < ").append("TO_DATA(").append(curDay).append(",'yyyy-MM-dd')");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getTime(9));
                item.setModifyDesc(rs.getString(10));
                item.setCustomerCallId(rs.getString(11));
                item.setEndCodeType(rs.getString(12));
                item.setEndCode(rs.getString(13));
                item.setLastDailTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setCurDayLostCallCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getBoolean(18));
                item.setLostCallFirstDay(rs.getTime(19));
                item.setLostCallCurDay(rs.getTime(20));
                item.setLostCallTotalCount(rs.getInt(21));

                item.setShareBatchStartTime(mapShareBatchIdVsShareBatchItem.get(item.getShareBatchId()).getStartTime());

                shareCustomerItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public void setLoadedFlagShareDataItemsByStateAndNextDialTime(String shareBatchId,
                                                                  List<SingleNumberModeShareCustomerStateEnum> shareDataStateList) {
        // 未加载的客户
    }

    public Boolean setAgentOccupied(int bizId, String shareBatchId, String customerId) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append("UserUseState = ").append("true");
        sqlBuilder.append("WHERE BusinessID = ").append(bizId);
        sqlBuilder.append(" ShareID = ").append(shareBatchId);
        sqlBuilder.append(" CID = ").append(customerId);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    // 从已加载复位成未加载状态
    public Boolean resetLoadedFlag() {

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append("IsMemoryLoading = ").append(0);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }


    int id;
    int bizId;
    String shareBatchId;
    String importBatchId;
    int customerId;
    SingleNumberModeShareCustomerStateEnum state;
    int modifyId;
    int modifyUserId;
    Date modifyTime;
    String modifyDesc;
    String customerCallId; //客户呼叫流水号
    String endCodeType;
    String endCode;
    Date lastDialTime;    //最近一次拨打时间
    Boolean userUseState;   //是否已经被坐席人员抽取
    Boolean isMemoryLoading;   //是否已经加载到内存

    Date nextDialTime;    //下次拨打时间

    int curRedialStageCount; //仅用于阶段拨打

    int curDayLostCallCount;   //当天未接通次数
    Date lostCallFirstDay; //第一次未接通日期
    Date lostCallCurDay; //当前未接通日期
    int lostCallTotalCount; //未接通总次数

    public Boolean updateCustomerShareStateToFinish() {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(state);
        sqlBuilder.append(" EndCodeType = ").append(endCodeType);
        sqlBuilder.append(", EndCode = ").append(endCode);
        sqlBuilder.append(", ModifyId = ").append(modifyId);
        sqlBuilder.append(", ModifyUserId = ").append(modifyUserId);
        sqlBuilder.append(", ModifyTime = ").append(modifyTime);
        sqlBuilder.append(", ModifyDesc = ").append(modifyDesc);
        sqlBuilder.append(", CustomerCallId = ").append(customerCallId);
        sqlBuilder.append(", LastDialTime = ").append(lastDialTime);
        sqlBuilder.append(", UserUseState = ").append(userUseState);
        sqlBuilder.append(", IsMemoryLoading = ").append(isMemoryLoading);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean updateCustomerShareStateToPreset() {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(state);
        sqlBuilder.append(" EndCodeType = ").append(endCodeType);
        sqlBuilder.append(", EndCode = ").append(endCode);
        sqlBuilder.append(", ModifyId = ").append(modifyId);
        sqlBuilder.append(", ModifyUserId = ").append(modifyUserId);
        sqlBuilder.append(", ModifyTime = ").append(modifyTime);
        sqlBuilder.append(", ModifyDesc = ").append(modifyDesc);
        sqlBuilder.append(", CustomerCallId = ").append(customerCallId);
        sqlBuilder.append(", LastDialTime = ").append(lastDialTime);
        sqlBuilder.append(", NextDialTime = ").append(nextDialTime);
        sqlBuilder.append(", UserUseState = ").append(userUseState);
        sqlBuilder.append(", IsMemoryLoading = ").append(isMemoryLoading);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean updateCustomerShareStateToStage() {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(state);
        sqlBuilder.append(" EndCodeType = ").append(endCodeType);
        sqlBuilder.append(", EndCode = ").append(endCode);
        sqlBuilder.append(", ModifyId = ").append(modifyId);
        sqlBuilder.append(", ModifyUserId = ").append(modifyUserId);
        sqlBuilder.append(", ModifyTime = ").append(modifyTime);
        sqlBuilder.append(", ModifyDesc = ").append(modifyDesc);
        sqlBuilder.append(", CustomerCallId = ").append(customerCallId);
        sqlBuilder.append(", LastDialTime = ").append(lastDialTime);
        sqlBuilder.append(", UserUseState = ").append(userUseState);
        sqlBuilder.append(", IsMemoryLoading = ").append(isMemoryLoading);

        sqlBuilder.append(", CurRedialStageCount = ").append(curRedialStageCount);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean updateCustomerShareStateToLostCall() {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(state);
        sqlBuilder.append(" EndCodeType = ").append(endCodeType);
        sqlBuilder.append(", EndCode = ").append(endCode);
        sqlBuilder.append(", ModifyId = ").append(modifyId);
        sqlBuilder.append(", ModifyUserId = ").append(modifyUserId);
        sqlBuilder.append(", ModifyTime = ").append(modifyTime);
        sqlBuilder.append(", ModifyDesc = ").append(modifyDesc);
        sqlBuilder.append(", CustomerCallId = ").append(customerCallId);
        sqlBuilder.append(", LastDialTime = ").append(lastDialTime);
        sqlBuilder.append(", UserUseState = ").append(userUseState);
        sqlBuilder.append(", IsMemoryLoading = ").append(isMemoryLoading);

        sqlBuilder.append(", CurDayLostCallCount = ").append(curDayLostCallCount);
        sqlBuilder.append(", LostCallFirstDay = ").append(lostCallFirstDay);
        sqlBuilder.append(", LostCallCurDay = ").append(lostCallCurDay);
        sqlBuilder.append(", LostCallTotalCount = ").append(lostCallTotalCount);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean insertCustomerShareStateHistory() {

        int id = 0;
        int bizId = 0;
        String shareBatchId = "";
        String importBatchId = "";
        int customerId = 0;
        SingleNumberModeShareCustomerStateEnum state = SingleNumberModeShareCustomerStateEnum.FINISHED;
        int modifyId = 0;
        int modifyUserId = 0;
        Date modifyTime = null;
        String modifyDesc = "";
        String customerCallId = ""; //客户呼叫流水号
        String endCodeType = "";
        String endCode = "";
        Date lastDialTime = null;    //最近一次拨打时间
        Boolean userUseState = false;   //是否已经被坐席人员抽取
        Boolean isMemoryLoading = false;   //是否已经加载到内存

        Date nextDialTime = null;    //下次拨打时间

        int curRedialStageCount = 0; //仅用于阶段拨打

        int curDayLostCallCount = 0;   //当天未接通次数
        Date lostCallFirstDay = null; //第一次未接通日期
        Date lostCallCurDay = null; //当前未接通日期
        int lostCallTotalCount = 0; //未接通总次数

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO HASYS_DM_B1C_DATAM3_HIS (ID, BusinessID, ShareID," +
                "IID, CID, State, ModifyId, ModifyUserId, ModifyTime, ModifyDesc, CustomerCallId, LastDialTime, " +
                "UserUseState, IsMemoryLoading, NextDialTime, CurRedialStageCount, CurDayLostCallCount, LostCallFirstDay," +
                "LostCallCurDay, LostCallTotalCount) VALUES ( ");
        sqlBuilder.append("'").append(id).append("',");
        sqlBuilder.append("'").append(bizId).append("',");
        sqlBuilder.append("'").append(shareBatchId).append("',");
        sqlBuilder.append("'").append(importBatchId).append("',");
        sqlBuilder.append("'").append(customerId).append("',");
        sqlBuilder.append("'").append(state.getName()).append("',");
        sqlBuilder.append("'").append(modifyId).append("',");
        sqlBuilder.append("'").append(modifyUserId).append("',");
        sqlBuilder.append("'").append(modifyTime).append("',");
        sqlBuilder.append("'").append(modifyDesc).append("',");
        sqlBuilder.append("'").append(customerCallId).append("',");
        sqlBuilder.append("'").append(endCodeType).append("',");
        sqlBuilder.append("'").append(endCode).append("',");
        sqlBuilder.append("'").append(lastDialTime).append("',");
        sqlBuilder.append("'").append(userUseState).append("',");
        sqlBuilder.append("'").append(isMemoryLoading).append("',");
        sqlBuilder.append("'").append(nextDialTime).append("',");
        sqlBuilder.append("'").append(curRedialStageCount).append("',");
        sqlBuilder.append("'").append(curDayLostCallCount).append("',");
        sqlBuilder.append("'").append(lostCallFirstDay).append("',");
        sqlBuilder.append("'").append(lostCallCurDay).append("',");
        sqlBuilder.append("'").append(lostCallTotalCount);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean clearPreviousDayLostCallCount() {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" CurDayLostCallCount = ").append(0);
        sqlBuilder.append(" WHERE trunc(LostCallCurDay) < trunc(sysdate)");

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    // 中间重启时，去除当天未接通次数满的项
    public Boolean getXXX(List<ShareBatchItem> ShareBatchItems,
                                            List<SingleNumberModeShareCustomerStateEnum> shareDataStateList,
                                            /*OUT*/ List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        // 非本表字段
        Date shareBatchBeginTime; //

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BusinessID, ShareId, IID, CID, State, ModifyID, " +
                    "ModifyUserId, ModifyTime, ModifyDesc, CustomerCallID, EndCodeType, EndCode, LastDialTime, " +
                    "NextDialTime, CurDayLostCallCount, CurRedialStageCount, UserUseState, LostCallFirstDay, LostCallCurDay, " +
                    "LostCallTotalCount FROM HASYS_DM_B1C_DATAM3 WHERE ");
            sqlBuilder.append(" ShareId IN (").append(shareBatchItemlistToCommaSplitString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND State IN (").append(shareBatchStatelistToCommaSplitString(shareDataStateList)).append(")");
            sqlBuilder.append(" AND CurDayLostCallCount > 0");
            sqlBuilder.append(" AND trunc(LostCallCurDay) = trunc(sysdate)");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SingleNumberModeShareCustomerItem item = new SingleNumberModeShareCustomerItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(SingleNumberModeShareCustomerStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getTime(9));
                item.setModifyDesc(rs.getString(10));
                item.setCustomerCallId(rs.getString(11));
                item.setEndCodeType(rs.getString(12));
                item.setEndCode(rs.getString(13));
                item.setLastDailTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setCurDayLostCallCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getBoolean(18));
                item.setLostCallFirstDay(rs.getTime(19));
                item.setLostCallCurDay(rs.getTime(20));
                item.setLostCallTotalCount(rs.getInt(21));

                item.setShareBatchStartTime(mapShareBatchIdVsShareBatchItem.get(item.getShareBatchId()).getStartTime());

                shareCustomerItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }



    ////////////////////////////////////////////////////////////////////////////////

    private String shareBatchStatelistToCommaSplitString(List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareCustomerStateList.size(); indx++) {
            SingleNumberModeShareCustomerStateEnum state = shareCustomerStateList.get(indx);
            sb.append(state.getName());
            if (indx < (shareCustomerStateList.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }

    private String shareBatchItemlistToCommaSplitString(List<ShareBatchItem> shareBatchItems) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareBatchItems.size(); indx++) {
            ShareBatchItem item = shareBatchItems.get(indx);
            sb.append(item.getShareBatchId());
            if (indx < (shareBatchItems.size() - 1))
                sb.append(",");
        }
        return sb.toString();
    }

    private String integerListToCommaSplitString(List<Integer> integerList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < integerList.size(); indx++) {
            sb.append(integerList.get(indx));
            if (indx < (integerList.size() - 1))
                sb.append(",");
        }

        return sb.toString();
    }

}