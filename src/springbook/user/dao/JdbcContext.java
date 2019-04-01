package springbook.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStarategy(StatementStrategy stmt) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = this.dataSource.getConnection();
            pstmt = stmt.makePreparedStatement(conn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {

                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    public void excuteSql(final String query) throws SQLException {
        workWithStatementStarategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
                PreparedStatement pstmt = conn.prepareStatement(query);
                return pstmt;
            }
        });
    }

    public void excuteAddSql(final String query, Object... args) throws SQLException {
        workWithStatementStarategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
                PreparedStatement pstmt = conn.prepareStatement("insert into users(id,name,password) values(?,?,?)");
                for (int i = 1; i <= args.length; i++) {
                    if (args[i - 1] instanceof String) {
                        pstmt.setString(i, args[i - 1] + "");
                    } else if (Integer.class.isInstance(args[i])) {
                        pstmt.setInt(i, (Integer) args[i - 1]);
                    } else if (args[i] instanceof Short) {
                        pstmt.setShort(i, (Short) args[i - 1]);
                    } else if (args[i] instanceof Long) {
                        pstmt.setLong(i, (Long) args[i - 1]);
                    }
                }
                return pstmt;
            }
        });


    }
}
