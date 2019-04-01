package springbook.user.dao;

public class DuplicateuserIdException extends RuntimeException {
    public DuplicateuserIdException(Throwable cause){
        super(cause);
    }
}
