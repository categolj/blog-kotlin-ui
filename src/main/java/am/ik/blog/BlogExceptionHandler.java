package am.ik.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import am.ik.blog.maintenance.MaintenanceException;

@ControllerAdvice
public class BlogExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(BlogExceptionHandler.class);

	@ExceptionHandler(MaintenanceException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	String handleMaintenanceException(MaintenanceException e) {
		return "error/maintenance";
	}

	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	String handleResourceAccessException(ResourceAccessException e) {
		log.error("Cannot access api", e);
		return "error/api-server-down";
	}

	@ExceptionHandler(HttpServerErrorException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String handleHttpServerErrorException(HttpServerErrorException e) {
		log.error("API Server Error", e);
		return "error/api-server-error";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String handleException(Exception e) {
		log.error("Unexpected Error", e);
		return "error/unexpected-error";
	}
}
