package springbook.user.dao;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class CountingDaoFactory {

	@Bean
	public UserDaoJdbc userDao() {
		UserDaoJdbc userDaoJdbc =new UserDaoJdbc();
		userDaoJdbc.setDataSource(dataSource());
		return userDaoJdbc;
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realConnectionMaker());
	}
	
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource=new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://10.211.55.4/springbook");
		dataSource.setUsername("user");
		dataSource.setPassword("1234");
		return dataSource;
	}
	
	@Bean
	public ConnectionMaker realConnectionMaker() {
		return new DConnectionMaker();
	}
}
