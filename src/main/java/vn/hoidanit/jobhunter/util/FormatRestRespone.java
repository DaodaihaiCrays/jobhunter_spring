package vn.hoidanit.jobhunter.util;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.hoidanit.jobhunter.domain.response.RestRespone;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;


@RestControllerAdvice
public class FormatRestRespone implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {

        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        if (body instanceof String) {
            return body;
        }

        int status = servletResponse.getStatus();
        RestRespone<Object> res = new RestRespone<>();
        res.setStatusCode(status);

        if (status >= 400) {
            return body;
        } else {
            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "success");
        }

        return res;
    }

}
