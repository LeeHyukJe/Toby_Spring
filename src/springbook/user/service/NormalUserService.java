package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class NormalUserService implements UserLevelUpgradePolicy {
    public static final int MIN_LOGCOUNT_FOR_SILVER=50;
    public static final int MIN_RECOMMEND_FOR_GOLD=30;

    UserDao userDao;

    public void setUserDao(UserDao userDao){
        this.userDao=userDao;
    }

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel=user.getLevel();
        switch (currentLevel){
            case BASIC:return (user.getLogin()>=MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:return (user.getRecommend()>=MIN_RECOMMEND_FOR_GOLD);
            case GOLD:return false;
            default:throw new IllegalArgumentException("Unknown Level:"+currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }


}
