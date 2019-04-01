package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.domain.User;

// 프록시 역할 클래스
public class UserServiceTx implements UserService {
    UserService userService; //타깃 오브젝트
    PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService){
        this.userService=userService;
    }
    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager=transactionManager;
    }

    @Override
    // 메소드 구현과 위임
    public void add(User user) {
        userService.add(user);
    }

    @Override
    // 메소드 구현
    public void upgradeLevels() {
        // 부가기능 수행
        TransactionStatus status=this.transactionManager
                .getTransaction(new DefaultTransactionDefinition());
        try{
            userService.upgradeLevels(); // 위임
            this.transactionManager.commit(status); // 부가기능 수행
        }catch (RuntimeException e){
            this.transactionManager.rollback(status); // 부가기능 수행
            throw e;
        }
    }
}
