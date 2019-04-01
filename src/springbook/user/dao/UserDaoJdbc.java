package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;
import springbook.user.sqlservice.SqlService;

public class UserDaoJdbc implements UserDao{
    private JdbcTemplate jdbcTemplate;
    private Map<String,String> sqlMap;
    private SqlService sqlService;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate=new JdbcTemplate(dataSource);
    }
    public void setSqlService(SqlService sqlService){
        this.sqlService=sqlService;
    }

    public void setSqlMap(Map<String,String> sqlMap){
        this.sqlMap=sqlMap;
    }
    private RowMapper<User> userRowMapper=new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user=new User();
            user.setId(resultSet.getString("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setLevel(Level.valueOf(resultSet.getInt("level")));
            user.setLogin(resultSet.getInt("login"));
            user.setRecommend(resultSet.getInt("recommend"));
            user.setEmail(resultSet.getString("email"));
            return user;
        }
    };


//    public void add(final User user) throws  DuplicateKeyException {
//        try {
//            this.jdbcTemplate.update("insert into users (id,name,password,level,login,recommend,email) values(?,?,?,?,?,?,?)",
//                    user.getId(),
//                    user.getName(),
//                    user.getPassword(),
//                    user.getLevel().intValue(),
//                    user.getLogin(),
//                    user.getRecommend(),
//                    user.getEmail());
////        this.jdbcContext.excuteAddSql("\"insert into (id,name,password) values(?,?,?)",user.getId(),user.getName(),user.getPassword());
//        }catch (DuplicateKeyException e){
//            // 예외를 전환할 때는 원인이 되는 예외를 중첩하는 것이 좋다
//            throw e;
//        }
//    }

    public void add(User user){
        this.jdbcTemplate.update(this.sqlService.getSql("userAdd"),
                user.getId(),
                    user.getName(),
                    user.getPassword(),
                    user.getLevel().intValue(),
                    user.getLogin(),
                    user.getRecommend(),
                    user.getEmail());
    }

    public User get(String id)  {
        return this.jdbcTemplate.queryForObject("select * from users where id=?",
                new Object[]{id}, this.userRowMapper);
    }

    public List<User> getAll(){
        return this.jdbcTemplate.query("select * from users order by id",
                this.userRowMapper);
    }

    public void deleteAll(){
//        this.jdbcContext.excuteSql("delete from users");
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement("delete from users");
            }
        });
    }

    public int getCount() {
//        return this.jdbcTemplate.query(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                return connection.prepareStatement("select count(*) from users");
//            }
//        }, new ResultSetExtractor<Integer>() {
//            @Override
//            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//                resultSet.next();
//                return resultSet.getInt(1);
//            }
//        });
        return this.jdbcTemplate.queryForInt("select count(*) from users");
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update("update users set name=?,password=?, level=?, login=?,recommend=? where id=?",
                user.getName(),
                user.getPassword(),user.getLevel().intValue(),user.getLogin(),
                user.getRecommend(),user.getId());

    }
}
