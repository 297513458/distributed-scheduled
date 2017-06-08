package com.htxx.scheduling.common;
import java.io.PrintWriter;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
	@ResponseBody
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		try {
			PrintWriter print = response.getWriter();
			if (ex instanceof LoginException) {
				response.setStatus(HttpStatus.SC_NOT_ACCEPTABLE);
				print.write(ResultJson.toJSONString(HttpStatus.SC_NOT_ACCEPTABLE,"用户禁止访问,可能登录超时" ));
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				print.write(ResultJson.toJSONString(HttpStatus.SC_BAD_REQUEST,"Tengine/2.5.0"));
			}
			print.flush();
			print.close();
		} catch (Exception e) {
		} finally {
			CurrentUser.remove();
		}
		return null;
	}
}