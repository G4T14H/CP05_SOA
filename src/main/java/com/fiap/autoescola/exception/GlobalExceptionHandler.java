package com.fiap.autoescola.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErro> handleValidation(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(new ApiErro(400, "Dados inválidos", mensagens));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErro> handleBusiness(BusinessException ex) {
        return ResponseEntity.unprocessableEntity().body(new ApiErro(422, "Regra de negócio violada", List.of(ex.getMessage())));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErro> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErro(404, "Recurso não encontrado", List.of(ex.getMessage())));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErro> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErro(409, "Conflito", List.of(ex.getMessage())));
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiErro> handleBadCredentials(org.springframework.security.core.AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErro(401, "Credenciais inválidas", List.of("Usuário ou senha incorretos")));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErro> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErro(403, "Acesso negado", List.of("Você não tem permissão para executar esta ação")));
    }
    @ExceptionHandler(ApiExternaException.class)
    public ResponseEntity<ApiErro> externo(ApiExternaException ex){return ResponseEntity.status(502).body(new ApiErro(502,"Serviço externo indisponível",List.of(ex.getMessage())));}
    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,IllegalArgumentException.class,org.springframework.data.mapping.PropertyReferenceException.class})
    public ResponseEntity<ApiErro> formato(Exception ex){return ResponseEntity.badRequest().body(new ApiErro(400,"Formato inválido",List.of("Confira JSON, enumerações, identificador, CEP e campos de ordenação")));}
    @ExceptionHandler({org.springframework.dao.DataIntegrityViolationException.class,org.springframework.dao.PessimisticLockingFailureException.class,org.springframework.dao.OptimisticLockingFailureException.class})
    public ResponseEntity<ApiErro> integridade(Exception ex){return ResponseEntity.status(409).body(new ApiErro(409,"Conflito de dados",List.of("Registro duplicado ou em alteração. Atualize os dados e tente novamente")));}
}
