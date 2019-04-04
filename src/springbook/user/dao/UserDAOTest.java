package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDAOTest {

	@Autowired
	private UserDaoJdbc dao;
	@Autowired
	private DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setup(){
//		this.dao=context.getBean("userDao",UserDaoJdbc.class);

		this.user1=new User("1","dlfkjsldf","1111", Level.BASIC,1,0,"useradmin@leehyukje.org");
		this.user2=new User("2","dkfjlesf","2222",Level.SILVER,55,10,"useradmin@leehyukje.org");
		this.user3=new User("3","dlkfjslkdjf","3333", Level.GOLD,100,40,"useradmin@leehyukje.org");

		//dao=new DaoFactory().userDao();
	}
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException{
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount() ,is(2));   
		
		
		User userget1=dao.get(user1.getId());
		assertThat(userget1.getName(),is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));
		checkSameUser(userget1,user1);
		
		User userget2=dao.get(user2.getId());
		assertThat(user2.getName(), is(user2.getName()));
		assertThat(user2.getPassword(), is(user2.getPassword()));
		checkSameUser(userget2,user2);
	}
	
	@Test
	public void count() throws SQLException, ClassNotFoundException{

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}

	@Test
	public void getAll() throws SQLException, ClassNotFoundException{
		dao.deleteAll();

//		dao.add(user1);
//		List<User> users1=dao.getAll();
//		assertThat(users1.size(),is(1));
//		checkSameUser(user1,users1.get(0));
//
//		dao.add(user2);
//		List<User> users2=dao.getAll();
//		assertThat(users2.size(),is(2));
//		checkSameUser(user1,users2.get(0));
//		checkSameUser(user2,users2.get(1));
//
//		dao.add(user3);
//		List<User> users3=dao.getAll();
//		assertThat(users3.size(),is(3));
//		checkSameUser(user3,users3.get(0));
//		checkSameUser(user1,users3.get(1));
//		checkSameUser(user2,users3.get(2));

		List<User> users0=dao.getAll();
		assertThat(users0.size(),is(0));

	}

	@Test
	public void update(){
		dao.deleteAll();

		dao.add(user1);
		dao.add(user2);

		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level .GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);

		User user1update =dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same=dao.get(user2.getId());
		checkSameUser(user2,user2same);
	}

	private void checkSameUser(User user1, User user2){
		assertThat(user1.getId(),is(user2.getId()));
		assertThat(user1.getName(),is(user2.getName()));
		assertThat(user1.getPassword(),is(user2.getPassword()));
		assertThat(user1.getLevel(),is(user2.getLevel()));
		assertThat(user1.getLogin(),is(user2.getLogin()));
		assertThat(user1.getRecommend(),is(user2.getRecommend()));
	}

	@Test(expected=DuplicateuserIdException.class)
	public void duplicateKey(){
		dao.deleteAll();

		dao.add(user1);
		dao.add(user1);
	}

	@Test
	public void sqlExceptionTranslate(){
		dao.deleteAll();

		try{
			dao.add(user1);
			dao.add(user1);
		}catch (DuplicateuserIdException ex){
			SQLException sqlEx=(SQLException)ex.getCause();
			SQLExceptionTranslator set=new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			assertThat(set.translate(null,null,sqlEx),is(DuplicateKeyException.class));
		}
	}
	
	
	public static void main(String[] args) {

		JUnitCore.main("springbook.user.dao.UserDAOTest");
	}
	
//	@Test(expected=EmptyResultDataAccessException.class)
//	public void getUserFaliure() throws SQLException{
//
//	}
}