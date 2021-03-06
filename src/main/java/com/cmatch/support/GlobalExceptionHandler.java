package com.cmatch.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cmatch.dto.ErrorDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 어플리케이션에서 발생하는 예외에 대한 최종 처리를 수행한다. 이 클래스가 계승하는
 * {@link ResponseEntityExceptionHandler}의 메서드를 오버라이딩 해서 예외를 처리하며 기타
 * {@link RuntimeException} 하위의 다른 예외들에 대해서는 이 클래스의
 * {@link #handleSystemException}에서 처리한다.
 * 
 * 이 클래스의 메서드에서 예외 전달 받은 예외에 대한 로깅을 함을 알 수 있다.
 * {@link log#error(String, Throwable)}와 같은 방법을 사용하고 있는데, 예외의 스택 트레이스가 콘솔에 출력되게
 * 된다.
 * 
 * 
 * @author leeseunghyun
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    // Instance Fields
    // ==========================================================================================================================

    private final MessageSource msgSource;
    
    // Constructors
    // ==========================================================================================================================

    public GlobalExceptionHandler(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    // Methods
    // ==========================================================================================================================

    /**
     * 
     * 컨트롤러 메서드에 전달되는 인자에 문제가 있을 때 던져지는 예외에 대한 예외 핸들러이다. 예를 들면
     * {@code @PathVariable}인자에 다른 타입의 값이 들어왔다거나 할 때 이 예외가 발생한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     * 
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info("An exception occurred associated with controller method parameter.", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.MethodArgumentNotValidException", null, request.getLocale()));

        ex.getBindingResult().getFieldErrors().stream()
                .forEach(e -> errDTO.addDetail(e.getField(), msgSource.getMessage(e, request.getLocale())));

        ex.getBindingResult().getGlobalErrors().stream()
                .forEach(e -> errDTO.addDetail(e.getObjectName(), msgSource.getMessage(e, request.getLocale())));

        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * 컨트롤러에 전달되는 오브젝트 인자에 대한 바인딩이 실패할 경우 이 메서드에서 해당 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.info("An Exception occurred while trying to bind object", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.BindException", null, request.getLocale()));

        ex.getBindingResult().getFieldErrors().stream()
                .forEach(e -> errDTO.addDetail(e.getField(), msgSource.getMessage(e, request.getLocale())));

        ex.getBindingResult().getGlobalErrors().stream()
                .forEach(e -> errDTO.addDetail(e.getObjectName(), msgSource.getMessage(e, request.getLocale())));

        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * {@link HttpServletRequest}에 원하는 파라미터가 들어있지 않을 경우 발생하는 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     *
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info("An exception occurred because request parameter doesn't exist.", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.MissingServletRequestParameterException",
                        new String[] { ex.getParameterName() }, request.getLocale()));

        return handleExceptionInternal(ex, errDTO, headers, status, request);

    }

    /**
     * 
     * 요구한 multi-part file이 전달되지 않은 경우 발생하는 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     * 
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info("An exception occurred because request multipart data doesn't exist.", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.MissingServletRequestPartException",
                        new String[] { ex.getRequestPartName() }, request.getLocale()));

        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * 컨트롤러의 {@link @RequestMapping} 메서드의 인자로 전달 될 경로 변수가 URL에서 추출 된 URI 변수에 존재하지 않을
     * 때 발생하는 예외를 처리한다. 이 예외는 일반적으로 URI 템플릿이 method 인자에 명시된 경로 변수 이름과 매치되지 않음을 의미한다.
     * 
     * @param status
     *            - 500 - Internal Server Error를 의미. URI 템플릿에서 지정한 경로 변수 명이나 메서드
     *            인자명을 바꾸거나 해야 한다.
     * 
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        log.info("An exception occurred accoicated with path variable setting.", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.MissingPathVariableException", null, request.getLocale()));

        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * 페이지를 찾지 못할 때 발생하는 예외에 대한 처리를 수행하는 메서드.
     * 
     * @param status
     *            - 404 - Not Found
     * 
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        log.info("An exception occurred associated with non existing end point call.", ex);

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.NoHandlerFoundException", new String[] { ex.getRequestURL() },
                        request.getLocale()));

        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * 지원되지 않는 HTTP method가 컨트롤러에 전달되었을 때 발생하는 예외를 처리하는 메서드.
     * 
     * @param status
     *            - 405 - Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        log.info("An exception occurred associated with unsupported type request.", ex);

        StringBuilder builder = new StringBuilder();
        ex.getSupportedHttpMethods().forEach(method -> builder.append(method.name() + ", "));

        int lastCommaIdx = builder.lastIndexOf(",");
        builder.replace(lastCommaIdx, lastCommaIdx + 1, "");

        ErrorDTO errDTO = new ErrorDTO(status.value(),
                msgSource.getMessage("response.exception.HttpRequestMethodNotSupportedException",
                        new String[] { ex.getMethod(), builder.toString() }, request.getLocale()));
        return handleExceptionInternal(ex, errDTO, headers, status, request);
    }

    /**
     * 
     * 이 클래스가 계승하는 {@link ResponseEntityExceptionHandler}에 정의된 메서드에서 처리할 수 있는 예외가 아닌
     * 그 외의 예외에 대한 처리를 수행하는 메서드이다.
     * 
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleSystemException(Exception ex, WebRequest request) {

        log.error("An system exception occurred.", ex);

        ErrorDTO errDTO = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                msgSource.getMessage("response.exception.SystemException", null, request.getLocale()));

        return handleExceptionInternal(ex, errDTO, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
