package com._51discuss.xa_test;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.MysqlXAConnection;
import com.mysql.cj.jdbc.MysqlXid;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.*;
import java.util.UUID;

/**
 *
CREATE TABLE `user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) CHARACTER SET utf8mb4 NOT NULL COMMENT 'User nick name',
  `user_mobile` char(11) CHARACTER SET utf8mb4 NOT NULL,
  `user_pwd` varchar(64) COLLATE utf8mb4_bin NOT NULL,
  `user_create_time` datetime(3) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_mobile_UNIQUE` (`user_mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 * @author Zeal
 */
public class LocalXaTest {

    public LocalXaTest() {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
            throw new IllegalStateException(ex.toString(), ex);
        }
    }

    public void multiInserts() throws Exception {
        String jdbcUrl = "jdbc:mysql://localhost/test?serverTimezone=GMT%2B8&autoReconnect=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&allowMultiQueries=true";
        String jdbcUser = "root";
        String jdbcPassword = null;
        XAConnection connection1 = null;
        XAConnection connection2 = null;
        try {
            connection1 = getConnection(jdbcUrl, jdbcUser, jdbcPassword, true);
            connection2 = getConnection(jdbcUrl, jdbcUser, jdbcPassword, true);
            XAResource resource1 = connection1.getXAResource();
            XAResource resource2 = connection2.getXAResource();
            final byte[] gtrid = UUID.randomUUID().toString().getBytes();
            final int formatId = 1;
            Xid xid1 = null;
            Xid xid2 = null;
            try {
                //======================================================================================================
                //First time, it will succeed
                xid1 = insertUser(connection1, gtrid, formatId, "user1", "13555555555", "123456");
                //With duplicate user, it should roll back
                xid2 = insertUser(connection2, gtrid, formatId, "user2", "13555555556", "123456");
                //======================================================================================================
                //Second time, it will roll back
                //The second time, it should roll back
//                xid1 = insertUser(connection1, gtrid, formatId, "user3", "13555555557", "123456");
//                //With duplicate user, it should roll back
//                xid2 = insertUser(connection2, gtrid, formatId, "user1", "13555555556", "123456");
                //2PC
                int prepare1 = resource1.prepare(xid1);
                int prepare2 = resource2.prepare(xid2);
                final boolean onePhase = false;
                if (prepare1 == XAResource.XA_OK && prepare2 == XAResource.XA_OK) {
                    resource1.commit(xid1, onePhase);
                    resource2.commit(xid2, onePhase);
                } else {
                    resource1.rollback(xid1);
                    resource2.rollback(xid2);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                if (xid1 != null) {
                    resource1.rollback(xid1);
                }
                if (xid2 != null) {
                    resource2.rollback(xid2);
                }
                throw e;
            }

        } finally {
            close(connection1).close(connection2);
        }
    }


    private Xid insertUser(XAConnection xaConnection, final byte[] gtrid, final int formatId, String userName, String userMobile, String userPwd) throws Exception {
        Connection connection = xaConnection.getConnection();
        XAResource resource = xaConnection.getXAResource();
        String sql = "INSERT INTO `user`(`user_name`,`user_mobile`,`user_pwd`,`user_create_time`) VALUES(?,?,?,?)";
        byte[] bqual = UUID.randomUUID().toString().getBytes();
        MysqlXid xid = new MysqlXid(gtrid, bqual, formatId);
        resource.start(xid, XAResource.TMNOFLAGS);
        try (PreparedStatement psm = connection.prepareStatement(sql)) {
            psm.setString(1, userName);
            psm.setString(2, userMobile);
            psm.setString(3, userPwd);
            psm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            psm.executeUpdate();
            resource.end(xid, XAResource.TMSUCCESS);
            return xid;
        }
    }

    private XAConnection getConnection(String jdbcUrl, String jdbcUser, String jdbcPassword, boolean logXaCommand) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
        return new MysqlXAConnection((JdbcConnection) connection, logXaCommand);
    }

    private LocalXaTest close(XAConnection closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public static void main(String[] args) throws Exception {
        LocalXaTest test = new LocalXaTest();
        test.multiInserts();
    }
}
