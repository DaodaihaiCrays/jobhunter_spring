package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public ResponseEntity<String> getHelloWorld() {
//        RestRespone<String> response = new RestRespone<>();
//        response.setStatusCode(HttpStatus.OK.value());
//        response.setMessage("Hello");

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }
}
