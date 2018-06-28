package hiapp.modules.dm.manualmode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomerPool;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.OperationNameEnum;
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
public class ManualModeDAO extends BaseRepository {

    @Autowired
    private ManualModeCustomerPool manualModeCustomerPool;

    //手动分配超时处理
    public void getManualDistributeCustomers(Integer bizId, String disBatchId, String tempIds, String tempTableName) {
        Connection dbConn = null;
        PreparedStatement stmt = null;
        String[] arrTempId=tempIds.split(",");

        try {
            dbConn = this.getDbConnection();
            String sql;
            if(arrTempId.length==1){
                sql="select IID, CID from "+tempTableName+" where IFCHECKED=1 and TEMPID = " + arrTempId[0];
            }else{
                sql="select IID, CID from "+tempTableName+" where IFCHECKED=1 and TEMPID in (";
                for (int i = 0; i < arrTempId.length; i++) {
                    String tempId=arrTempId[i];
                    if(tempId==null||"".equals(tempId)){
                        continue;
                    }
                    sql+=Integer.valueOf(tempId)+",";
                }
                sql=sql.substring(0,sql.length()-1)+")";
            }

            System.out.println(sql.toString());

            stmt = dbConn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ManualModeCustomer basic = new ManualModeCustomer();
                basic.setBizId(bizId);
                basic.setSourceId(disBatchId);
                basic.setImportBatchId(rs.getString(1));
                basic.setCustomerId(rs.getString(2));
                basic.setBizId(bizId);
                //标记为已被抽取，不然无法删除
                basic.setExtracted(true);
                manualModeCustomerPool.addCustomer(basic);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }
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
