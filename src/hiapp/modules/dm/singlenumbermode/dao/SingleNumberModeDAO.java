package hiapp.modules.dm.singlenumbermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.util.DateUtil;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.serviceresult.ServiceResultCode;
import hiapp.modules.dm.util.SQLUtil;
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
    public Boolean getGivenBizShareDataItemsByState(int bizId, List<ShareBatchItem> ShareBatchItems,
                                                    List<SingleNumberModeShareCustomerStateEnum> shareDataStateList,
                                            /*OUT*/ List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    "MODIFYUSERID, MODIFYTIME, MODIFYDESC, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, LASTDIALTIME, " +
                    "NEXTDIALTIME, THISDAYDIALEDCOUNT, CURREDIALSTAGECOUNT, USERUSESTATE, LOSTCALLFIRSTDAY, LOSTCALLCURDAY, " +
                    "LOSTCALLTOTALCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE IN (").append(SQLUtil.shareStatelistToSqlString(shareDataStateList)).append(")");

            System.out.println("==>  " + sqlBuilder.toString());

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
                item.setLastDialTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setLostCallCurDayCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getString(18));
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
    public Boolean getGivenBizShareDataItemsByStateAndNextDialTime(int bizId,
                                                                   List<ShareBatchItem> ShareBatchItems,
                                                                   List<SingleNumberModeShareCustomerStateEnum> shareDataStateList,
                                                           /*OUT*/List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    "MODIFYUSERID, MODIFYTIME, MODIFYDESC, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, LASTDIALTIME, " +
                    "NEXTDIALTIME, THISDAYDIALEDCOUNT, CURREDIALSTAGECOUNT, USERUSESTATE, LOSTCALLFIRSTDAY, LOSTCALLCURDAY, " +
                    "LOSTCALLTOTALCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE IN (").append(SQLUtil.shareStatelistToSqlString(shareDataStateList)).append(")");
            sqlBuilder.append(" AND NEXTDIALTIME < ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));

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
                item.setLastDialTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setLostCallCurDayCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getString(18));
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

    public Boolean setUserUseState(int bizId, String shareBatchId, String customerId) {

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET USERUSESTATE = ").append("true");
        sqlBuilder.append("WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
        sqlBuilder.append(" SHAREID = ").append(SQLUtil.getSqlString(shareBatchId));
        sqlBuilder.append(" CID = ").append(SQLUtil.getSqlString(customerId));

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

    // 更新客户共享状态
    public Boolean updateCustomerShareState(int bizId, List<String> shareBatchIdList, String state) {

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET STATE = ").append(SQLUtil.getSqlString(state));
        sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIdList)).append(")");

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
        String tableName = String.format("HAU_DM_B%dC_DATAM3", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", ENDCODETYPE = ").append(SQLUtil.getSqlString(item.getEndCodeType()));
        sqlBuilder.append(", ENDCODE = ").append(SQLUtil.getSqlString(item.getEndCode()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CUSTOMERCALLID = ").append(SQLUtil.getSqlString(item.getCustomerCallId()));
        sqlBuilder.append(", LASTDIALTIME = ").append(SQLUtil.getSqlString(item.getLastDialTime()));
        sqlBuilder.append(", USERUSESTATE = ").append(SQLUtil.getSqlString(item.getUserUseState()));
        sqlBuilder.append(", ISMEMORYLOADIN = ").append(SQLUtil.getSqlString(item.getIsLoaded()));
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(item.getBizId()));
        sqlBuilder.append("  AND IID = ").append(SQLUtil.getSqlString(item.getImportBatchId()));
        sqlBuilder.append("  AND CID = ").append(SQLUtil.getSqlString(item.getCustomerId()));

        System.out.println(sqlBuilder.toString());

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
        String tableName = String.format("HAU_DM_B%dC_DATAM3", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", ENDCODETYPE = ").append(SQLUtil.getSqlString(item.getEndCodeType()));
        sqlBuilder.append(", ENDCODE = ").append(SQLUtil.getSqlString(item.getEndCode()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CUSTOMERCALLID = ").append(SQLUtil.getSqlString(item.getCustomerCallId()));
        sqlBuilder.append(", LASTDIALTIME = ").append(SQLUtil.getSqlString(item.getLastDialTime()));
        sqlBuilder.append(", NEXTDIALTIME = ").append(SQLUtil.getSqlString(item.getNextDialTime()));
        sqlBuilder.append(", USERUSESTATE = ").append(SQLUtil.getSqlString(item.getUserUseState()));
        sqlBuilder.append(", ISMEMORYLOADIN = ").append(SQLUtil.getSqlString(item.getIsLoaded()));
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(item.getBizId()));
        sqlBuilder.append("  AND IID = ").append(SQLUtil.getSqlString(item.getImportBatchId()));
        sqlBuilder.append("  AND CID = ").append(SQLUtil.getSqlString(item.getCustomerId()));

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
        String tableName = String.format("HAU_DM_B%dC_DATAM3", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", ENDCODETYPE = ").append(SQLUtil.getSqlString(item.getEndCodeType()));
        sqlBuilder.append(", ENDCODE = ").append(SQLUtil.getSqlString(item.getEndCode()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CUSTOMERCALLID = ").append(SQLUtil.getSqlString(item.getCustomerCallId()));
        sqlBuilder.append(", LASTDIALTIME = ").append(SQLUtil.getSqlString(item.getLastDialTime()));
        sqlBuilder.append(", USERUSESTATE = ").append(SQLUtil.getSqlString(item.getUserUseState()));
        sqlBuilder.append(", ISMEMORYLOADIN = ").append(SQLUtil.getSqlString(item.getIsLoaded()));
        sqlBuilder.append(", CURREDIALSTAGECOUNT = ").append(SQLUtil.getSqlString(item.getCurRedialStageCount()));
        sqlBuilder.append(", NEXTDIALTIME = ").append(SQLUtil.getSqlString(item.getNextDialTime()));
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(item.getBizId()));
        sqlBuilder.append("  AND IID = ").append(SQLUtil.getSqlString(item.getImportBatchId()));
        sqlBuilder.append("  AND CID = ").append(SQLUtil.getSqlString(item.getCustomerId()));

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            int rowNum = stmt.executeUpdate();
            System.out.println(rowNum);
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
        String tableName = String.format("HAU_DM_B%dC_DATAM3", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", ENDCODETYPE = ").append(SQLUtil.getSqlString(item.getEndCodeType()));
        sqlBuilder.append(", ENDCODE = ").append(SQLUtil.getSqlString(item.getEndCode()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CUSTOMERCALLID = ").append(SQLUtil.getSqlString(item.getCustomerCallId()));
        sqlBuilder.append(", LASTDIALTIME = ").append(SQLUtil.getSqlString(item.getLastDialTime()));
        sqlBuilder.append(", USERUSESTATE = ").append(SQLUtil.getSqlString(item.getUserUseState()));
        sqlBuilder.append(", ISMEMORYLOADIN = ").append(SQLUtil.getSqlString(item.getIsLoaded()));
        sqlBuilder.append(", THISDAYDIALEDCOUNT = ").append(SQLUtil.getSqlString(item.getLostCallCurDayCount()));
        sqlBuilder.append(", LOSTCALLFIRSTDAY = ").append(SQLUtil.getSqlString(item.getLossCallFirstDay()));
        sqlBuilder.append(", LOSTCALLCURDAY = ").append(SQLUtil.getSqlString(item.getLostCallCurDay()));
        sqlBuilder.append(", LOSTCALLTOTALCOUNT = ").append(SQLUtil.getSqlString(item.getLostCallTotalCount()));
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(item.getBizId()));
        sqlBuilder.append("  AND IID = ").append(SQLUtil.getSqlString(item.getImportBatchId()));
        sqlBuilder.append("  AND CID = ").append(SQLUtil.getSqlString(item.getCustomerId()));

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

        String tableName = String.format("HAU_DM_B%dC_DATAM3_HIS", item.getBizId());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, MODIFYUSERID, MODIFYTIME, MODIFYDESC, " +
                "CUSTOMERCALLID, ENDCODETYPE, ENDCODE, LASTDIALTIME, USERUSESTATE, ISMEMORYLOADIN, NEXTDIALTIME, CURREDIALSTAGECOUNT, " +
                "THISDAYDIALEDCOUNT, LOSTCALLFIRSTDAY, LOSTCALLCURDAY, LOSTCALLTOTALCOUNT) VALUES ( ");
        sqlBuilder.append("S_" + tableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getBizId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getShareBatchId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getImportBatchId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCustomerId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getState().getName())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyUserId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyDesc())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCustomerCallId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getEndCodeType())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getEndCode())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLastDialTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getUserUseState())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getIsLoaded())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getNextDialTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCurRedialStageCount())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLostCallCurDayCount())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLostCallFirstDay())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLostCallCurDay())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLostCallTotalCount()));
        sqlBuilder.append(")");

        System.out.println(sqlBuilder.toString());

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

    public Boolean clearPreviousDayLostCallCount(int bizId) {

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" THISDAYDIALEDCOUNT = ").append(0);
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

    // 已经弃用
    // 中间重启时，去除当天未接通次数满的项
    public Boolean getCurrentLostCallStateCustomers(int bizId,
                                                    List<ShareBatchItem> ShareBatchItems,
                          /*OUT*/ List<SingleNumberModeShareCustomerItem> shareCustomerItems) {

        String tableName = String.format("HAU_DM_B%dC_DATAM3", bizId);

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
                    "NEXTDIALTIME, THISDAYDIALEDCOUNT, CURREDIALSTAGECOUNT, USERUSESTATE, LOSTCALLFIRSTDAY, LOSTCALLCURDAY, " +
                    "LOSTCALLTOTALCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(" SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE = ").append(SQLUtil.getSqlString(SingleNumberModeShareCustomerStateEnum.LOSTCALL_WAIT_REDIAL.getName()));
            sqlBuilder.append(" AND THISDAYDIALEDCOUNT > 0");
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
                item.setLastDialTime(rs.getTime(14));
                item.setNextDialTime(rs.getTime(15));
                item.setLostCallCurDayCount(rs.getInt(16));
                item.setCurRedialStageCount(rs.getInt(17));
                item.setUserUseState(rs.getString(18));
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

}