package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStatement implements StatementStrategy {
    private User user;

    public AddStatement(User user){
        this.user=user;
    }

    @Override
    public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
        PreparedStatement pstmt=conn.prepareStatement("insert into users(id,name,password) values(?,?,?)");
        pstmt.setString(1,user.getId());
        pstmt.setString(2,user.getName());
        pstmt.setString(3,user.getPassword());

        return pstmt;
    }
}
