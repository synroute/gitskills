package hiapp.modules.dm.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class DMDAO extends BaseRepository {

    /*
     *  所有业务的共享批次
     */
    public Boolean getAllActiveShareBatchItems(/*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                                        "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STATE ='").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShareBatchItem item = new ShareBatchItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setShareBatchName(rs.getString(4));
                item.setCreateUserId(rs.getString(5));
                item.setCreateTime(rs.getTime(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
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

    /**
     *
     */
    public Boolean getActiveShareBatchItems(List<String> shareBatchIdList, /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STATE = '").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(" AND SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIdList)).append(")");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShareBatchItem item = new ShareBatchItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setShareBatchName(rs.getString(4));
                item.setCreateUserId(rs.getString(5));
                item.setCreateTime(rs.getTime(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
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

    /*
     *  取得当天启用 且未处理的共享批次, 处理后续启用的共享批次
     */
    public Boolean getNeedActiveShareBatchItems( /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STARTTIME <= ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));
            sqlBuilder.append(" AND STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShareBatchItem item = new ShareBatchItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setShareBatchName(rs.getString(4));
                item.setCreateUserId(rs.getString(5));
                item.setCreateTime(rs.getTime(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
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

    public  Boolean activateShareBatchByStartTime() {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            // 激活共享批次
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ACTIVE.getName()));
            sqlBuilder.append(" WHERE STARTTIME <= ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));
            sqlBuilder.append(" AND STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));

            System.out.println(sqlBuilder.toString());

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

    // 获取并标记 过期的共享批次
    public  Boolean expireShareBatchsByEndTime(/*OUT*//*List<String> expiredShareBatchIds*/) {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            StringBuilder sqlBuilder;
            /*
            // 获取过期的共享批次
            sqlBuilder = new StringBuilder("SELECT shareBatchId FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" (State = ").append(ShareBatchStateEnum.ENABLE.getName());
            sqlBuilder.append(" OR State = ").append(ShareBatchStateEnum.ACTIVE.getName());
            sqlBuilder.append(") AND EndTime < ").append("TO_DATA(").append(curDay).append(",'yyyy-MM-dd')");
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expiredShareBatchIds.add(rs.getString(1));
            }
            */

            // 过期处理..共享批次
            sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.EXPIRED.getName()));
            sqlBuilder.append(" WHERE (STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));
            sqlBuilder.append(" OR STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ACTIVE.getName()));
            sqlBuilder.append(") AND ENDTIME < ").append(SQLUtil.getSqlString(DateUtil.getCurDayStartSqlString()));

            System.out.println(sqlBuilder.toString());

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

    /**
     *
     * @param bizId
     * @param sourceId  :  来源编号是指分配编号或共享编号
     * @param importBatchId
     * @param customerId
     * @param modifyId
     * @param modifyUserId
     * @param dialType  :  拨打类型 拨打提交；修改提交
     * @param dialTime
     * @param customerCallId  :  呼叫流水号
     * @return
     */
    public Boolean insertDMResult(int bizId, String sourceId, String importBatchId, String customerId, int modifyId,
                                  String modifyUserId, String dialType, String dialTime, String customerCallId) {

        String tableName = String.format("HAU_DM_B%dC_Result", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, MODIFYID, MODIFYUSERID, MODIFYTIME, MODIFYLAST, DIALTYPE, " +
                                "DIALTIME, CUSTOMERCALLID) VALUES ( ");
        sqlBuilder.append("S_" + tableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(sourceId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(importBatchId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(customerId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(modifyId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(modifyUserId)).append(",");;
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append("1").append(","); // MODIFYLAST
        sqlBuilder.append(SQLUtil.getSqlString(dialType)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(dialTime)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(customerCallId));
        sqlBuilder.append(")");

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

    public Boolean updateDMResult(int bizId, String shareBatchId, String importBatchId, String customerId) {

        String tableName = String.format("HAU_DM_B%dC_Result", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET MODIFYLAST = ").append("0");
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
        sqlBuilder.append(" AND SOURCEID = ").append(SQLUtil.getSqlString(shareBatchId));
        sqlBuilder.append(" AND CID = ").append(SQLUtil.getSqlString(customerId));

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

    public Boolean insertPresetItem(int bizId, DMBizPresetItem presetItem) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PresetTime", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO" + presetTimeTableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, PRESETTIME, STATE, COMMENT, MODIFYID, MODIFYUSERID, MODIFYTIME, " +
                                      "MODIFYDESC，MODIFYLAST, PHONETYPE) VALUES ( ");

        sqlBuilder.append("S_" + presetTimeTableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getSourceId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getImportId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getCustomerId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getPresetTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getState())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getComment())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyUserId())).append(",");
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyDesc())).append(",");
        sqlBuilder.append("1").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getPhoneType()));
        sqlBuilder.append(")");

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

    public Boolean updatePresetState(int bizId, String shareBatchId, String customerId, String newState) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PresetTime", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + presetTimeTableName);
        sqlBuilder.append(" SET STATE = ").append(SQLUtil.getSqlString(newState));
        sqlBuilder.append(" MODIFYLAST = 0");
        sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
        sqlBuilder.append(" SOURCEID = ").append(SQLUtil.getSqlString(shareBatchId));
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


}


