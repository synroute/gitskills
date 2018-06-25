package hiapp.modules.dm.manualmode.dao;

import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.DataPoolRecordOperation;
import hiapp.modules.dmmanager.OperationNameEnum;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ManualModeDAO extends BaseRepository {

    public List<ManualModeCustomer> getManualDistributeCustomers(Integer bizId) {

        Connection dbConn = null;
        PreparedStatement stmt = null;



        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        List<ManualModeCustomer> customerBasicList = new ArrayList<>();
        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT a.ID, SOURCEID, IID, CID FROM " + tableName);
            sqlBuilder.append(" a LEFT JOIN HASYS_DM_DID ON SOURCEID=DID");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ManualModeCustomer basic = new ManualModeCustomer();
                basic.setId(rs.getInt(1));
                basic.setSourceId(rs.getString(2));
                basic.setImportBatchId(rs.getString(3));
                basic.setCustomerId(rs.getString(4));
                basic.setBizId(bizId);
                //标记为已被抽取，不然无法删除
                basic.setExtracted(true);

                customerBasicList.add(basic);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return customerBasicList;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return customerBasicList;
    }

    public Boolean getGivenBizShareCustomers(int bizId, List<ShareBatchItem> ShareBatchItems,
                                            /*OUT*/ List<ManualModeCustomer> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, SOURCEID, IID, CID, DATAPOOlIDLAST, DATAPOOlIDCUR, " +
                                  "AREALAST, AREACUR, ISRECOVER, OPERATIONNAME, MODIFYUSERID, MODIFYTIME FROM " + tableName);
            sqlBuilder.append(" WHERE SOURCEID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append("   AND AREACUR = ").append(AreaTypeEnum.SA.getId());

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ManualModeCustomer item = new ManualModeCustomer();
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

                ShareBatchItem shareBatchItem = mapShareBatchIdVsShareBatchItem.get(item.getSourceId());
                item.setShareBatchStartTime(shareBatchItem.getStartTime());

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

    public Boolean getGivenBizShareCustomersByOperationType(int bizId,
                                                            List<ShareBatchItem> ShareBatchItems,
                                            OperationNameEnum operationName,
                                            /*OUT*/ List<ManualModeCustomer> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, SOURCEID, IID, CID, DATAPOOlIDLAST, DATAPOOlIDCUR, " +
                    "AREALAST, AREACUR, ISRECOVER, OPERATIONNAME, MODIFYUSERID, MODIFYTIME FROM " + tableName);
            sqlBuilder.append(" WHERE SOURCEID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append("   AND AREACUR = ").append(SQLUtil.getSqlString(AreaTypeEnum.SA.getId()));
            sqlBuilder.append("   AND OPERATIONNAME = ").append(SQLUtil.getSqlString(operationName.getName()));

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ManualModeCustomer item = new ManualModeCustomer();
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

                ShareBatchItem shareBatchItem = mapShareBatchIdVsShareBatchItem.get(item.getSourceId());
                item.setShareBatchStartTime(shareBatchItem.getStartTime());

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

    public Boolean updateCustomerOperationName(int bizId, List<Integer> appendedCustomerIdList, OperationNameEnum operationName) {

        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET OPERATIONNAME = ").append(SQLUtil.getSqlString(operationName.getName()));
        sqlBuilder.append(" WHERE ID IN (").append(SQLUtil.integerListToSqlString(appendedCustomerIdList)).append(")");

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
