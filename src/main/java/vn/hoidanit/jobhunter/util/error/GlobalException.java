package vn.hoidanit.jobhunter.util.error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.hoidanit.jobhunter.domain.response.RestRespone;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            InvalidException.class
    })
    public ResponseEntity<RestRespone<Object>> handleIdException(Exception exception) {

        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getMessage());
//        res.setMessage("username or password is wrong");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestRespone<Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());

        String errorMessage = ex.getMessage();
        if (errorMessage.contains("LevelEnum")) {
            res.setError("Invalid value for level");
            res.setMessage("Invalid value for level. Must be one of: SENIOR, JUNIOR, INTERN, FRESHER, MIDDLE.");
        } else if (errorMessage.contains("GenderEnum")) {
            res.setError("Invalid value for gender");
            res.setMessage("Invalid value for level. Must be one of: FEMALE, MALE, OTHER.");
        } else if (errorMessage.contains("ResumeStateEnum")) {
            res.setError("Invalid value for resume state");
            res.setMessage("Invalid value for level. Must be one of: PENDING, REVIEWING, APPROVED, REJECTED.");
        }else {
            res.setError("JSON parse error");
            res.setMessage("Invalid format or value in JSON input.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }



    @ExceptionHandler(value = {
            RuntimeException.class
    })
    public ResponseEntity<RestRespone<Object>> handleRuntimeException(RuntimeException ex) {

        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestRespone<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class,
    })
    public ResponseEntity<RestRespone<Object>> handleNotFoundException(Exception ex) {
        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(ex.getMessage());
        res.setError("404 not Found. URL may not exist...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(value = {
            FileException.class
    })
    public ResponseEntity<RestRespone<Object>> handleFileException(Exception exception) {

        RestRespone<Object> res = new RestRespone<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(exception.getMessage());
        res.setMessage("error when uploading a file");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }


}
