/**
 * 
 */
package com.pppenger.microblog.exception;

import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 */

@ControllerAdvice
public class WebExceptionHandler {



	/**
	 * 获取批量异常信息
	 * @param e
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(value=Exception.class)
	public Result<String> exceptionHandler(Exception e){
		e.printStackTrace();
		if(e instanceof GlobalException) {
			GlobalException ex = (GlobalException)e;
			return Result.error(ex.getCm());
		}else if(e instanceof ConstraintViolationException) {
			List<String> msgList = new ArrayList<>();
			for (ConstraintViolation<?> constraintViolation : ((ConstraintViolationException) e).getConstraintViolations()) {
				msgList.add(constraintViolation.getMessage());
			}
			return Result.success(msgList.toString());
		}else if(e instanceof BindException) {
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else if(e instanceof AccessDeniedException) {
			System.out.println("检测到权限异常");
			return null;
			//AccessDeniedException ex = (AccessDeniedException)e;
			//return Result.success(ex.getMessage());
		}else {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	}


//	@ResponseBody
//	@ExceptionHandler(ConstraintViolationException.class)
//	public static String getMessage(ConstraintViolationException e) {
//		e.printStackTrace();
//		//网上查的，不怎么理解的
//		// String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
//		List<String> msgList = new ArrayList<>();
//		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
//			msgList.add(constraintViolation.getMessage());
//        }
//		String messages = StringUtils.join(msgList.toArray(), ";");
//		return messages;
//	}

}
