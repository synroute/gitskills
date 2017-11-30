package hiapp.modules.dm.hidialermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomer;
import hiapp.modules.dm.hidialermode.bo.HidialerModeCustomerStateEnum;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.multinumbermode.bo.PhoneDialInfo;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
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
                    " MODIFYUSERID, MODIFYTIME, MODIFYDSP, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PHONENUMBER, LASTDIALDAY, NEXTDIALTIME, CALLLOSSCOUNT, REDIALCOUNT FROM " + tableName);
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
                item.setRedialCount(rs.getInt(19));

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
                    " MODIFYUSERID, MODIFYTIME, MODIFYDSP, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PHONENUMBER, LASTDIALDAY, NEXTDIALTIME, CALLLOSSCOUNT, REDIALCOUNT FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append("   AND STATE IN (").append(SQLUtil.hidialerModeCustomerStatelistToSqlString(shareDataStateList)).append(")");
            sqlBuilder.append("   AND NEXTDIALTIME < ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));

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
                item.setRedialCount(rs.getInt(19));

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
        sqlBuilder.append(", MODIFYDSP = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CUSTOMERCALLID = ").append(SQLUtil.getSqlString(item.getCustomerCallId()));
        sqlBuilder.append(", CALLLOSSCOUNT = ").append(SQLUtil.getSqlString(item.getCallLossCount()));
        sqlBuilder.append(", REDIALCOUNT = ").append(SQLUtil.getSqlString(item.getRedialCount()));
        sqlBuilder.append(", NEXTDIALTIME = ").append(SQLUtil.getSqlString(item.getNextDialTime()));
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
        sqlBuilder.append(", MODIFYDSP = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
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
                        " MODIFYUSERID, MODIFYTIME, MODIFYDSP, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                        " PHONENUMBER, LASTDIALDAY, NEXTDIALTIME, CALLLOSSCOUNT, REDIALCOUNT ) VALUES ( ");

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
        sqlBuilder.append(SQLUtil.getSqlString(item.getCallLossCount())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getRedialCount()));

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

    public ManualModeCustomer getPoolItem(int bizId, String sourceId, String importBatchId, String customerId)
    {
        ManualModeCustomer item = new ManualModeCustomer();

        Connection dbConn = null;
        PreparedStatement stmt = null;

        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, SOURCEID, IID, CID, DATAPOOlIDLAST, DATAPOOlIDCUR, " +
                    "AREALAST, AREACUR, ISRECOVER, OPERATIONNAME, MODIFYUSERID, MODIFYTIME FROM " + tableName);
            sqlBuilder.append(" WHERE SOURCEID = ").append(SQLUtil.getSqlString(sourceId));
            sqlBuilder.append("   AND IID = ").append(SQLUtil.getSqlString(importBatchId));
            sqlBuilder.append("   AND CID = ").append(SQLUtil.getSqlString(customerId));

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                item.setId(rs.getInt(1));
                item.setSourceId(rs.getString(2));
                item.setImportBatchId(rs.getString(3));
                item.setCustomerId(rs.getString(4));
                item.setDataPoolIdLast(rs.getInt(5));
                item.setDataPoolIdCur(rs.getInt(6));
                item.setAreaTypeLast(AreaTypeEnum.getFromInt(rs.getInt(7)));
                item.setAreaTypeCur(AreaTypeEnum.getFromInt(rs.getInt(8)));
                item.setIsRecover(rs.getInt(9));
                item.setOperationName(OperationNameEnum.getFromString(rs.getString(10)));
                item.setModifyUserId(rs.getString(11));
                item.setModifyTime(rs.getDate(12));

                item.setBizId(bizId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return item;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return item;
    }

    public Boolean updatePool(ManualModeCustomer item) {

        String tableName = String.format("HAU_DM_B%dC_POOL", item.getBizId());

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append("  DATAPOOlIDLAST = ").append(SQLUtil.getSqlString(item.getDataPoolIdLast()));
        sqlBuilder.append(", DATAPOOlIDCUR = ").append(SQLUtil.getSqlString(item.getDataPoolIdCur()));
        sqlBuilder.append(", AREALAST = ").append(SQLUtil.getSqlString(item.getAreaTypeLast().getId()));
        sqlBuilder.append(", AREACUR = ").append(SQLUtil.getSqlString(item.getAreaTypeCur().getId()));
        sqlBuilder.append(", ISRECOVER = ").append(SQLUtil.getSqlString(item.getIsRecover()));
        sqlBuilder.append(", OPERATIONNAME = ").append(SQLUtil.getSqlString(item.getOperationName().getName()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(" WHERE ID = ").append(SQLUtil.getSqlString(item.getId()));

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

    public Boolean insertPoolOperation(ManualModeCustomer item, OperationNameEnum operationType) {

        String tableName = String.format("HAU_DM_B%dC_POOL_ORE", item.getBizId());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID,SOURCEID,IID,CID,OPERATIONNAME,DATAPOOLIDLAST,DATAPOOLIDCUR," +
                "AREALAST,AREACUR,ISRECOVER, MODIFYUSERID,MODIFYTIME ) VALUES ( ");

        sqlBuilder.append("S_" + tableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getSourceId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getImportBatchId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCustomerId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(operationType.getName())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getDataPoolIdLast())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getDataPoolIdCur())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getAreaTypeLast().getId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getAreaTypeCur().getId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getIsRecover())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyUserId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyTime()));
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
