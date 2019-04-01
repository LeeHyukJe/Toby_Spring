package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserService testUserService;
//    @Autowired
//    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;
    @Autowired
    UserLevelUpgradePolicy policy;
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;


    List<User> users;
    User user;

    @Test
    public void test() {
        assertThat(userDao, is(notNullValue()));
        assertThat(userService, is(notNullValue()));
        assertThat(policy, is(notNullValue()));
    }

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "lee", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "useradmin@leehyukje.org")
                , new User("joytouch", "kim", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "useradmin@leehyukje.org")
                , new User("erwins", "park", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "useradmin@leehyukje.org")
                , new User("madnite1", "choi", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "useradmin@leehyukje.org")
                , new User("green", "na", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "useradmin@leehyukje.org")
        );

        user = new User();

    }

    @Test()
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(), is(level.nextLevel()));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }


    @Test
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String exptectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(exptectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }


    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4); //레벨 설정 됨

        // 레벨이 설정되지 않은 객체
        User userWithoutLevel = users.get(0);
        userWithLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        /*
        TestUserServiceImpl testUserServiceImpl = new TestUserServiceImpl();
        testUserServiceImpl.setUserDao(this.userDao);
        testUserServiceImpl.setPolicy(policy);
        testUserServiceImpl.setMailSender(mailSender);
        */

        /*
        --Proxy 패턴 적용 전 트랜잭션 사용 코드--
        UserServiceTx txUserService=new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);
        txUserSerivce.upgradeLevels();
         */

        /* Advice 및 Advisor 적용하기 전 FactoryBean 설정
        TxProxyFactoryBean txProxyFactoryBean=
                context.getBean("&userService",TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService=(UserService) txProxyFactoryBean.getObject();
        */

        /*
        ProxyFactoryBean txProxyFactoryBean=
                context.getBean("&userService",ProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserServiceImpl);
        UserService txUserService=(UserService) txProxyFactoryBean.getObject();
        */


        userDao.deleteAll();
        for (User user : users)
            userDao.add(user);

        try {
            this.testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }

        checkLevelUpgrade(users.get(1), false);


    }


    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    //  어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드 될 것인가 아닌가를 지정한다.
    private void checkLevelUpgrade(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) { // 업그레이드가 일어났는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else { // 업그레이드가 일어나지 않았는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    static class TestUserServiceImpl extends UserServiceImpl {
        private String id="madnite1";

//        private TestUserServiceImpl(String id) {
//            this.id = id;
//        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }


    // UserServiceImpl 테스트 코드가 정상적으로 수행되도록 하는 MockClass
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            // 전소 요청을 받은 이메일 주소를 저장(간단하게 첫번째 수신자만 저장)
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {

        }
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }

}
