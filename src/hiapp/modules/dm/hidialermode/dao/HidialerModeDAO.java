package hiapp.modules.dm.hidialermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomer;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomerStateEnum;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.multinumbermode.bo.PhoneDialInfo;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HidialerModeDAO extends BaseRepository {

    public Boolean getGivenBizCustomersByState(int bizId, List<ShareBatchItem> ShareBatchItems,
                                               List<HidialerModeCustomerStateEnum> shareDataStateList,
                                      /*OUT*/List<HidialerModeCustomer> customerList) {
        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM2", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PHONENUMBER, LASTDIALTIME, NEXTDIALTIME, CALLLOSSCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append("   AND STATE IN (").append(SQLUtil.hidialerModeCustomerStatelistToSqlString(shareDataStateList)).append(")");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HidialerModeCustomer item = new HidialerModeCustomer();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(HidialerModeCustomerStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getDate(9));
                item.setModifyDesc(rs.getString(10));
                item.setIsAppend(rs.getInt(11));
                item.setCustomerCallId(rs.getString(12));
                item.setEndCodeType(rs.getString(13));
                item.setEndCode(rs.getString(14));
                item.setPhoneNumber(rs.getString(15));
                item.setLastDialTime(rs.getDate(16));
                item.setNextDialTime(rs.getDate(17));
                item.setCallLossCount(rs.getInt(18));

                item.setShareBatchStartTime(mapShareBatchIdVsShareBatchItem.get(item.getShareBatchId()).getStartTime());

                customerList.add(item);
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

    public Boolean getGivenBizCustomersByStateAndNextDialTime(int bizId, List<ShareBatchItem> ShareBatchItems,
                                               List<HidialerModeCustomerStateEnum> shareDataStateList,
                                               /*OUT*/List<HidialerModeCustomer> customerList) {
        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM2", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PHONENUMBER, LASTDIALTIME, NEXTDIALTIME, CALLLOSSCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append("   AND STATE IN (").append(SQLUtil.hidialerModeCustomerStatelistToSqlString(shareDataStateList)).append(")");
            sqlBuilder.append("   AND CURPRESETDIALTIME < ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HidialerModeCustomer item = new HidialerModeCustomer();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(HidialerModeCustomerStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getDate(9));
                item.setModifyDesc(rs.getString(10));
                item.setIsAppend(rs.getInt(11));
                item.setCustomerCallId(rs.getString(12));
                item.setEndCodeType(rs.getString(13));
                item.setEndCode(rs.getString(14));
                item.setPhoneNumber(rs.getString(15));
                item.setLastDialTime(rs.getDate(16));
                item.setNextDialTime(rs.getDate(17));
                item.setCallLossCount(rs.getInt(18));

                item.setShareBatchStartTime(mapShareBatchIdVsShareBatchItem.get(item.getShareBatchId()).getStartTime());

                customerList.add(item);
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

    // 更新客户共享状态
    public Boolean updateCustomerShareState(int bizId, List<String> shareBatchIdList, HidialerModeCustomerStateEnum state) {

        String tableName = String.format("HAU_DM_B%dC_DATAM2", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET STATE = ").append(SQLUtil.getSqlString(state.getName()));
        sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIdList)).append(")");
        sqlBuilder.append("   AND BUSINESSID = ").append(SQLUtil.getSqlString(bizId));

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

    public Boolean updateCustomerShareForOutboundResult(HidialerModeCustomer item) {
        String tableName = String.format("HAU_DM_B%dC_DATAM2", item.getBizId());

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
//        sqlBuilder.append(", CURDIALPHONE = ").append(SQLUtil.getSqlString(item.getCurDialPhone()));
//        sqlBuilder.append(", CURDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getCurDialPhoneType()));
//        sqlBuilder.append(", NEXTDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getNextDialPhoneType()));
//        sqlBuilder.append(", CURPRESETDIALTIME = ").append(SQLUtil.getSqlString(item.getCurPresetDialTime()));
        //sqlBuilder.append(", ISAPPEND = ").append(SQLUtil.getSqlString(item.getIsAppend()));

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

    public Boolean updateCustomerShareForExtract(HidialerModeCustomer item) {
        String tableName = String.format("HAU_DM_B%dC_DATAM2", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
//        sqlBuilder.append(", CURDIALPHONE = ").append(SQLUtil.getSqlString(item.getCurDialPhone()));
//        sqlBuilder.append(", CURDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getCurDialPhoneType()));
//        sqlBuilder.append(", NEXTDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getNextDialPhoneType()));
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

    public Boolean insertCustomerShareStateHistory(HidialerModeCustomer item) {

        String tableName = String.format("HAU_DM_B%dC_DATAM2_HIS", item.getBizId());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                        " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                        " PHONENUMBER, LASTDIALTIME, NEXTDIALTIME, CALLLOSSCOUNT ) VALUES ( ");

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
        sqlBuilder.append(SQLUtil.getSqlString(item.getIsAppend())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCustomerCallId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getEndCodeType())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getEndCode())).append(",");

        sqlBuilder.append(SQLUtil.getSqlString(item.getPhoneNumber())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getLastDialTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getNextDialTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCallLossCount()));

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

}
