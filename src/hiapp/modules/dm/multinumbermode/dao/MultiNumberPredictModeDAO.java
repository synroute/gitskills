package hiapp.modules.dm.multinumbermode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberCustomer;
import hiapp.modules.dm.multinumbermode.bo.MultiNumberPredictStateEnum;
import hiapp.modules.dm.multinumbermode.bo.PhoneDialInfo;
import hiapp.modules.dm.multinumbermode.bo.PhoneTypeDialSequence;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MultiNumberPredictModeDAO extends BaseRepository {

    @Autowired
    PhoneTypeDialSequence phoneTypeDialSequence;

    public Boolean getGivenBizCustomersByState(int bizId, List<ShareBatchItem> ShareBatchItems,
                                               List<MultiNumberPredictStateEnum> shareDataStateList,
                                      /*OUT*/List<MultiNumberCustomer> customerList) {
        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM6", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PT1_PHONENUMBER, PT1_LASTDIALTIME, PT1_CAUSEPRESETDIALCOUNT, PT1_DIALCOUNT, " +
                    " PT2_PHONENUMBER, PT2_LASTDIALTIME, PT2_CAUSEPRESETDIALCOUNT, PT2_DIALCOUNT, " +
                    " PT3_PHONENUMBER, PT3_LASTDIALTIME, PT3_CAUSEPRESETDIALCOUNT, PT3_DIALCOUNT, " +
                    " PT4_PHONENUMBER, PT4_LASTDIALTIME, PT4_CAUSEPRESETDIALCOUNT, PT4_DIALCOUNT, " +
                    " PT5_PHONENUMBER, PT5_LASTDIALTIME, PT5_CAUSEPRESETDIALCOUNT, PT5_DIALCOUNT, " +
                    " PT6_PHONENUMBER, PT6_LASTDIALTIME, PT6_CAUSEPRESETDIALCOUNT, PT6_DIALCOUNT, " +
                    " PT7_PHONENUMBER, PT7_LASTDIALTIME, PT7_CAUSEPRESETDIALCOUNT, PT7_DIALCOUNT, " +
                    " PT8_PHONENUMBER, PT8_LASTDIALTIME, PT8_CAUSEPRESETDIALCOUNT, PT8_DIALCOUNT, " +
                    " PT9_PHONENUMBER, PT9_LASTDIALTIME, PT9_CAUSEPRESETDIALCOUNT, PT9_DIALCOUNT, " +
                    " PT10_PHONENUMBER, PT10_LASTDIALTIME, PT10_CAUSEPRESETDIALCOUNT, PT10_DIALCOUNT, " +
                    " CURDIALPHONE, CURPRESETDIALTIME, CURDIALPHONETYPE, NEXTDIALPHONETYPE " +
                    "FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE IN (").append(SQLUtil.multiNumberPredictStatelistToSqlString(shareDataStateList)).append(")");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MultiNumberCustomer item = new MultiNumberCustomer();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(MultiNumberPredictStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getTime(9));
                item.setModifyDesc(rs.getString(10));
                item.setIsAppend(rs.getInt(11));
                item.setCustomerCallId(rs.getString(12));
                item.setEndCodeType(rs.getString(13));
                item.setEndCode(rs.getString(14));

                for (int i=0; i<10; i++) {
                    PhoneDialInfo phoneDialInfo = new PhoneDialInfo();
                    phoneDialInfo.setPhoneNumber(rs.getString(15 + i*4));
                    phoneDialInfo.setLastDialTime(rs.getTime(16 + i*4));
                    phoneDialInfo.setCausePresetDialCount(rs.getInt(17 + i*4));
                    phoneDialInfo.setDialCount(rs.getInt(18 + i*4));
                    item.setDialInfo(i + 1, phoneDialInfo);
                }

                item.setCurDialPhone(rs.getString(55));
                item.setCurPresetDialTime(rs.getTime(56));
                item.setCurDialPhoneType(rs.getInt(57));
                item.setNextDialPhoneType(rs.getInt(58));

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
                                               List<MultiNumberPredictStateEnum> shareDataStateList,
                                               /*OUT*/List<MultiNumberCustomer> customerList) {
        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_DATAM6", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                    " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                    " PT1_PHONENUMBER, PT1_LASTDIALTIME, PT1_CAUSEPRESETDIALCOUNT, PT1_DIALCOUNT, " +
                    " PT2_PHONENUMBER, PT2_LASTDIALTIME, PT2_CAUSEPRESETDIALCOUNT, PT2_DIALCOUNT, " +
                    " PT3_PHONENUMBER, PT3_LASTDIALTIME, PT3_CAUSEPRESETDIALCOUNT, PT3_DIALCOUNT, " +
                    " PT4_PHONENUMBER, PT4_LASTDIALTIME, PT4_CAUSEPRESETDIALCOUNT, PT4_DIALCOUNT, " +
                    " PT5_PHONENUMBER, PT5_LASTDIALTIME, PT5_CAUSEPRESETDIALCOUNT, PT5_DIALCOUNT, " +
                    " PT6_PHONENUMBER, PT6_LASTDIALTIME, PT6_CAUSEPRESETDIALCOUNT, PT6_DIALCOUNT, " +
                    " PT7_PHONENUMBER, PT7_LASTDIALTIME, PT7_CAUSEPRESETDIALCOUNT, PT7_DIALCOUNT, " +
                    " PT8_PHONENUMBER, PT8_LASTDIALTIME, PT8_CAUSEPRESETDIALCOUNT, PT8_DIALCOUNT, " +
                    " PT9_PHONENUMBER, PT9_LASTDIALTIME, PT9_CAUSEPRESETDIALCOUNT, PT9_DIALCOUNT, " +
                    " PT10_PHONENUMBER, PT10_LASTDIALTIME, PT10_CAUSEPRESETDIALCOUNT, PT10_DIALCOUNT, " +
                    " CURDIALPHONE, CURPRESETDIALTIME, CURDIALPHONETYPE, NEXTDIALPHONETYPE " +
                    "FROM " + tableName);
            sqlBuilder.append(" WHERE SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND STATE IN (").append(SQLUtil.multiNumberPredictStatelistToSqlString(shareDataStateList)).append(")");
            sqlBuilder.append(" AND CURPRESETDIALTIME < ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MultiNumberCustomer item = new MultiNumberCustomer();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setImportBatchId(rs.getString(4));
                item.setCustomerId(rs.getString(5));
                item.setState(MultiNumberPredictStateEnum.getFromString(rs.getString(6)));
                item.setModifyId(rs.getInt(7));
                item.setModifyUserId(rs.getString(8));
                item.setModifyTime(rs.getTime(9));
                item.setModifyDesc(rs.getString(10));
                item.setIsAppend(rs.getInt(11));
                item.setCustomerCallId(rs.getString(12));
                item.setEndCodeType(rs.getString(13));
                item.setEndCode(rs.getString(14));

                for (int i=0; i<10; i++) {
                    PhoneDialInfo phoneDialInfo = new PhoneDialInfo();
                    phoneDialInfo.setPhoneNumber(rs.getString(15 + i*4));
                    phoneDialInfo.setLastDialTime(rs.getTime(16 + i*4));
                    phoneDialInfo.setCausePresetDialCount(rs.getInt(17 + i*4));
                    phoneDialInfo.setDialCount(rs.getInt(18 + i*4));
                    item.setDialInfo(i + 1, phoneDialInfo);
                }

                item.setCurDialPhone(rs.getString(55));
                item.setCurPresetDialTime(rs.getTime(56));
                item.setCurDialPhoneType(rs.getInt(57));
                item.setNextDialPhoneType(rs.getInt(58));

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
    public Boolean updateCustomerShareState(int bizId, List<String> shareBatchIdList, MultiNumberPredictStateEnum state) {

        String tableName = String.format("HAU_DM_B%dC_DATAM6", bizId);

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

    public Boolean updateCustomerShareForOutboundResult(MultiNumberCustomer item) {
        String tableName = String.format("HAU_DM_B%dC_DATAM6", item.getBizId());

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
        sqlBuilder.append(", CURDIALPHONE = ").append(SQLUtil.getSqlString(item.getCurDialPhone()));
        sqlBuilder.append(", CURDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getCurDialPhoneType()));
        sqlBuilder.append(", NEXTDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getNextDialPhoneType()));
        sqlBuilder.append(", CURPRESETDIALTIME = ").append(SQLUtil.getSqlString(item.getCurPresetDialTime()));
        //sqlBuilder.append(", ISAPPEND = ").append(SQLUtil.getSqlString(item.getIsAppend()));

        int curPhoneType = item.getCurDialPhoneType();
        //int curPhoneDialSeq = phoneTypeDialSequence.getDialSequenceByPhoneType(item.getBizId(), item.getCurDialPhoneType());
        PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(curPhoneType);
        sqlBuilder.append(", PT").append(curPhoneType).append("_PHONENUMBER = ").append(SQLUtil.getSqlString(phoneDialInfo.getPhoneNumber()));
        sqlBuilder.append(", PT").append(curPhoneType).append("_LASTDIALTIME = ").append(SQLUtil.getSqlString(phoneDialInfo.getLastDialTime()));
        sqlBuilder.append(", PT").append(curPhoneType).append("_CAUSEPRESETDIALCOUNT = ").append(SQLUtil.getSqlString(phoneDialInfo.getCausePresetDialCount()));
        sqlBuilder.append(", PT").append(curPhoneType).append("_DIALCOUNT = ").append(SQLUtil.getSqlString(phoneDialInfo.getDialCount()));

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

    public Boolean updateCustomerShareForExtract(MultiNumberCustomer item) {
        String tableName = String.format("HAU_DM_B%dC_DATAM6", item.getBizId());

        //
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET ");
        sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(item.getState().getName()));
        sqlBuilder.append(", MODIFYID = ").append(SQLUtil.getSqlString(item.getModifyId()));
        sqlBuilder.append(", MODIFYUSERID = ").append(SQLUtil.getSqlString(item.getModifyUserId()));
        sqlBuilder.append(", MODIFYTIME = ").append(SQLUtil.getSqlString(item.getModifyTime()));
        sqlBuilder.append(", MODIFYDESC = ").append(SQLUtil.getSqlString(item.getModifyDesc()));
        sqlBuilder.append(", CURDIALPHONE = ").append(SQLUtil.getSqlString(item.getCurDialPhone()));
        sqlBuilder.append(", CURDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getCurDialPhoneType()));
        sqlBuilder.append(", NEXTDIALPHONETYPE = ").append(SQLUtil.getSqlString(item.getNextDialPhoneType()));
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

    public Boolean insertCustomerShareStateHistory(MultiNumberCustomer item) {

        String tableName = String.format("HAU_DM_B%dC_DATAM6_HIS", item.getBizId());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, BUSINESSID, SHAREID, IID, CID, STATE, MODIFYID, " +
                        " MODIFYUSERID, MODIFYTIME, MODIFYDESC, ISAPPEND, CUSTOMERCALLID, ENDCODETYPE, ENDCODE, " +
                        " PT1_PHONENUMBER, PT1_LASTDIALTIME, PT1_CAUSEPRESETDIALCOUNT, PT1_DIALCOUNT, " +
                        " PT2_PHONENUMBER, PT2_LASTDIALTIME, PT2_CAUSEPRESETDIALCOUNT, PT2_DIALCOUNT, " +
                        " PT3_PHONENUMBER, PT3_LASTDIALTIME, PT3_CAUSEPRESETDIALCOUNT, PT3_DIALCOUNT, " +
                        " PT4_PHONENUMBER, PT4_LASTDIALTIME, PT4_CAUSEPRESETDIALCOUNT, PT4_DIALCOUNT, " +
                        " PT5_PHONENUMBER, PT5_LASTDIALTIME, PT5_CAUSEPRESETDIALCOUNT, PT5_DIALCOUNT, " +
                        " PT6_PHONENUMBER, PT6_LASTDIALTIME, PT6_CAUSEPRESETDIALCOUNT, PT6_DIALCOUNT, " +
                        " PT7_PHONENUMBER, PT7_LASTDIALTIME, PT7_CAUSEPRESETDIALCOUNT, PT7_DIALCOUNT, " +
                        " PT8_PHONENUMBER, PT8_LASTDIALTIME, PT8_CAUSEPRESETDIALCOUNT, PT8_DIALCOUNT, " +
                        " PT9_PHONENUMBER, PT9_LASTDIALTIME, PT9_CAUSEPRESETDIALCOUNT, PT9_DIALCOUNT, " +
                        " PT10_PHONENUMBER, PT10_LASTDIALTIME, PT10_CAUSEPRESETDIALCOUNT, PT10_DIALCOUNT, " +
                        " CURDIALPHONE, CURPRESETDIALTIME, CURDIALPHONETYPE, NEXTDIALPHONETYPE) VALUES ( ");

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

        for (int i=1; i<=10; i++) {
            Integer phoneType = phoneTypeDialSequence.getPhoneTypeByPhoneDialSequence(item.getBizId(), i);

            PhoneDialInfo phoneDialInfo = item.getDialInfoByPhoneType(phoneType);
            sqlBuilder.append(null == phoneDialInfo ? "null" : SQLUtil.getSqlString(phoneDialInfo.getPhoneNumber())).append(",");
            sqlBuilder.append(null == phoneDialInfo ? "null" : SQLUtil.getSqlString(phoneDialInfo.getLastDialTime())).append(",");
            sqlBuilder.append(null == phoneDialInfo ? "null" : SQLUtil.getSqlString(phoneDialInfo.getCausePresetDialCount())).append(",");
            sqlBuilder.append(null == phoneDialInfo ? "null" : SQLUtil.getSqlString(phoneDialInfo.getDialCount())).append(",");
        }

        sqlBuilder.append(SQLUtil.getSqlString(item.getCurDialPhone())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCurPresetDialTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCurDialPhoneType())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getNextDialPhoneType()));

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
