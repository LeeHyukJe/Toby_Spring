package springbook.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	
	@Bean
	public UserDaoJdbc userDao() {
		UserDaoJdbc userDaoJdbc =new UserDaoJdbc();
		userDaoJdbc.setDataSource(dataSource());
		return userDaoJdbc;
	}
	
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource=new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/springbook");
		dataSource.setUsername("user");
		dataSource.setPassword("1234");
		return dataSource;
		
	}
}
