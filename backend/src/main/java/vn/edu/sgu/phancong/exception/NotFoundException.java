package vn.edu.sgu.phancong.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String entity, Object id) {
        super(HttpStatus.NOT_FOUND, entity + " không tồn tại: " + id);
    }
}
