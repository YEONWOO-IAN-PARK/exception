package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 응답으로 404 전달
    @ExceptionHandler(IllegalArgumentException.class)
    // 예외가 발생하면 스프링의 ExceptionHandlerExceptionResolver가 해당 컨트롤러에서 @ExceptionHanlder 어노테이션을 검색하고 반환되는 타입에 맞는 객체형식(생성해준 에러결과 객체)으로 예외처리를 해준다. (Default 200)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exception Handler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler // (UserException.class) 요청핸들러메서드의 파라미터로 커스텀 Exception을 넣으면 어노테이션에서 지정해주지 않아도 인식한다.
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHanlder] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력값");
        }
        if(id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
