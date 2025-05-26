package com.example.demo.exception;

import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.example.demo.controller")
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, Exception e){
        log.error("异常信息："+e);
        return ResultFactory.error("系统异常");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result customError(HttpServletRequest request,CustomException e){
        return ResultFactory.error(e.getMsg());
    }

}
