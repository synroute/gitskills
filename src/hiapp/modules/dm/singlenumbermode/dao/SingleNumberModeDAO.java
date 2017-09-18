package hiapp.modules.dm.singlenumbermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;
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

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BusinessID, ShareId, IID, CID, State, ModifyID, " +
                    "ModifyUserId, ModifyTime, ModifyDesc, CustomerCallID, EndCodeType, EndCode, LastDialTime, " +
                    "NextDialTime, LOSTCALLCURDAYCOUNT, CurRedialStageCount, UserUseState, LostCallFirstDay, LostCallCurDay, " +
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
                item.setLostCallCurDayCount(rs.getInt(16));
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
                    "NextDialTime, LostCallCurDayCount, CurRedialStageCount, UserUseState, LostCallFirstDay, LostCallCurDay, " +
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
                item.setLostCallCurDayCount(rs.getInt(16));
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

    public Boolean updateCustomerShareStateToFinish(SingleNumberModeShareCustomerItem item) {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(item.getState());
        sqlBuilder.append(" EndCodeType = ").append(item.getEndCodeType());
        sqlBuilder.append(", EndCode = ").append(item.getEndCode());
        sqlBuilder.append(", ModifyId = ").append(item.getModifyId());
        sqlBuilder.append(", ModifyUserId = ").append(item.getModifyUserId());
        sqlBuilder.append(", ModifyTime = ").append(item.getModifyTime());
        sqlBuilder.append(", ModifyDesc = ").append(item.getModifyDesc());
        sqlBuilder.append(", CustomerCallId = ").append(item.getCustomerCallId());
        sqlBuilder.append(", LastDialTime = ").append(item.getLastDailTime());
        sqlBuilder.append(", UserUseState = ").append(item.getUserUseState());
        sqlBuilder.append(", IsMemoryLoading = ").append(item.getIsLoaded());

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

    public Boolean updateCustomerShareStateToPreset(SingleNumberModeShareCustomerItem item) {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(item.getState());
        sqlBuilder.append(" EndCodeType = ").append(item.getEndCodeType());
        sqlBuilder.append(", EndCode = ").append(item.getEndCode());
        sqlBuilder.append(", ModifyId = ").append(item.getModifyId());
        sqlBuilder.append(", ModifyUserId = ").append(item.getModifyUserId());
        sqlBuilder.append(", ModifyTime = ").append(item.getModifyTime());
        sqlBuilder.append(", ModifyDesc = ").append(item.getModifyDesc());
        sqlBuilder.append(", CustomerCallId = ").append(item.getCustomerCallId());
        sqlBuilder.append(", LastDialTime = ").append(item.getLastDailTime());
        sqlBuilder.append(", NextDialTime = ").append(item.getNextDialTime());
        sqlBuilder.append(", UserUseState = ").append(item.getUserUseState());
        sqlBuilder.append(", IsLoaded = ").append(item.getIsLoaded());

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

    public Boolean updateCustomerShareStateToStage(SingleNumberModeShareCustomerItem item) {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(item.getState());
        sqlBuilder.append(" EndCodeType = ").append(item.getEndCodeType());
        sqlBuilder.append(", EndCode = ").append(item.getEndCode());
        sqlBuilder.append(", ModifyId = ").append(item.getModifyId());
        sqlBuilder.append(", ModifyUserId = ").append(item.getModifyUserId());
        sqlBuilder.append(", ModifyTime = ").append(item.getModifyTime());
        sqlBuilder.append(", ModifyDesc = ").append(item.getModifyDesc());
        sqlBuilder.append(", CustomerCallId = ").append(item.getCustomerCallId());
        sqlBuilder.append(", LastDialTime = ").append(item.getLastDailTime());
        sqlBuilder.append(", UserUseState = ").append(item.getUserUseState());
        sqlBuilder.append(", IsLoaded = ").append(item.getIsLoaded());

        sqlBuilder.append(", CurRedialStageCount = ").append(item.getCurRedialStageCount());

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

    public Boolean updateCustomerShareStateToLostCall(SingleNumberModeShareCustomerItem item) {
        // endCodeType endCode
        // lastDialTime  nextDailTime

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_DATAM3 SET ");
        sqlBuilder.append(" State = ").append(item.getState());
        sqlBuilder.append(" EndCodeType = ").append(item.getEndCodeType());
        sqlBuilder.append(", EndCode = ").append(item.getEndCode());
        sqlBuilder.append(", ModifyId = ").append(item.getModifyId());
        sqlBuilder.append(", ModifyUserId = ").append(item.getModifyUserId());
        sqlBuilder.append(", ModifyTime = ").append(item.getModifyTime());
        sqlBuilder.append(", ModifyDesc = ").append(item.getModifyDesc());
        sqlBuilder.append(", CustomerCallId = ").append(item.getCustomerCallId());
        sqlBuilder.append(", LastDialTime = ").append(item.getLastDailTime());
        sqlBuilder.append(", UserUseState = ").append(item.getUserUseState());
        sqlBuilder.append(", IsMemoryLoading = ").append(item.getIsLoaded());

        sqlBuilder.append(", LostCallCurDayCount = ").append(item.getLostCallCurDayCount());
        sqlBuilder.append(", LostCallFirstDay = ").append(item.getLossCallFirstDay());
        sqlBuilder.append(", LostCallCurDay = ").append(item.getLostCallCurDay());
        sqlBuilder.append(", LostCallTotalCount = ").append(item.getLostCallTotalCount());

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

    public Boolean insertCustomerShareStateHistory(SingleNumberModeShareCustomerItem item) {

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO HASYS_DM_B1C_DATAM3_HIS (ID, BusinessID, ShareID," +
                "IID, CID, State, ModifyId, ModifyUserId, ModifyTime, ModifyDesc, CustomerCallId, LastDialTime, " +
                "UserUseState, IsMemoryLoading, NextDialTime, CurRedialStageCount, LostCallCurDayCount, LostCallFirstDay," +
                "LostCallCurDay, LostCallTotalCount) VALUES ( ");
        sqlBuilder.append("'").append(item.getId()).append("',");
        sqlBuilder.append("'").append(item.getBizId()).append("',");
        sqlBuilder.append("'").append(item.getShareBatchId()).append("',");
        sqlBuilder.append("'").append(item.getImportBatchId()).append("',");
        sqlBuilder.append("'").append(item.getCustomerId()).append("',");
        sqlBuilder.append("'").append(item.getState().getName()).append("',");
        sqlBuilder.append("'").append(item.getModifyId()).append("',");
        sqlBuilder.append("'").append(item.getModifyUserId()).append("',");
        sqlBuilder.append("'").append(item.getModifyTime()).append("',");
        sqlBuilder.append("'").append(item.getModifyDesc()).append("',");
        sqlBuilder.append("'").append(item.getCustomerCallId()).append("',");
        sqlBuilder.append("'").append(item.getEndCodeType()).append("',");
        sqlBuilder.append("'").append(item.getEndCode()).append("',");
        sqlBuilder.append("'").append(item.getLastDailTime()).append("',");
        sqlBuilder.append("'").append(item.getUserUseState()).append("',");
        sqlBuilder.append("'").append(item.getIsLoaded()).append("',");
        sqlBuilder.append("'").append(item.getNextDialTime()).append("',");
        sqlBuilder.append("'").append(item.getCurRedialStageCount()).append("',");
        sqlBuilder.append("'").append(item.getLostCallCurDayCount()).append("',");
        sqlBuilder.append("'").append(item.getLostCallFirstDay()).append("',");
        sqlBuilder.append("'").append(item.getLostCallCurDay()).append("',");
        sqlBuilder.append("'").append(item.getLostCallTotalCount());

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
        sqlBuilder.append(" LOSTCALLCURDAYCOUNT = ").append(0);
        sqlBuilder.append(" WHERE trunc(LOSTCALLCURDAY) < trunc(sysdate)");

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
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    "MODIFYUSERID, MODIFYTIME, MODIFYDESC, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, LASTDIALTIME, " +
                    "NEXTDIALTIME, LOSTCALLCURDAYCOUNT, CURREDIALSTAGECOUNT, USERUSESTATE, LOSTCALLFIRSTDAY, LOSTCALLCURDAY, " +
                    "LOSTCALLTOTALCOUNT FROM HASYS_DM_B1C_DATAM3 WHERE ");
            sqlBuilder.append(" SHAREID IN (").append(shareBatchItemlistToCommaSplitString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE IN (").append(shareBatchStatelistToCommaSplitString(shareDataStateList)).append(")");
            sqlBuilder.append(" AND LOSTCALLCURDAYCOUNT > 0");
            sqlBuilder.append(" AND trunc(LOSTCALLCURDAY) = trunc(sysdate)");

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
                item.setLostCallCurDayCount(rs.getInt(16));
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

    public ServiceResultCode updateShareBatchState(List<String> shareBatchIds, String state) {
        String sql = "";
        PreparedStatement stmt = null;
        Connection dbConn = null;
        try {
            dbConn=this.getDbConnection();
            sql=String.format("UPDATA HASYS_DM_SID SET STATE ='%s' WHERE SHAREID IN ('%s')",
                                        state, stringListToCommaSplitString(shareBatchIds));
            stmt = dbConn.prepareStatement(sql);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceResultCode.SUCCESS;
    }



    ////////////////////////////////////////////////////////////////////////////////

    private String shareBatchStatelistToCommaSplitString(List<SingleNumberModeShareCustomerStateEnum> shareCustomerStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareCustomerStateList.size(); indx++) {
            SingleNumberModeShareCustomerStateEnum state = shareCustomerStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
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

    private String stringListToCommaSplitString(List<String> stringList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < stringList.size(); indx++) {
            sb.append(stringList.get(indx));
            if (indx < (stringList.size() - 1))
                sb.append(",");
        }

        return sb.toString();
    }
}