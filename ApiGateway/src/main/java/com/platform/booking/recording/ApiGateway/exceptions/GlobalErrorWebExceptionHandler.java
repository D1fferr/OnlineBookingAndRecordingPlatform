package com.platform.booking.recording.ApiGateway.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.Encoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
@Order(-2)
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

    private static final String TRACE_HEADER = "X-Trace-Id";
    private final Encoder<Object> encoder = new JacksonJsonEncoder();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = determineHttpStatus(ex);

        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }
        response.getHeaders().set(TRACE_HEADER, traceId);
        log.atError()
                .setCause(ex)
                .addKeyValue("exception", ex.getClass().getSimpleName())
                .addKeyValue("uri", exchange.getRequest().getURI().getPath())
                .addKeyValue("status", status.value())
                .addKeyValue("traceId", status.value())
                .log("Gateway exception: " + ex.getMessage());

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "exception", ex.getClass().getName(),
                "message", ex.getMessage() != null ? ex.getMessage() : "No message available",
                "traceId", traceId
        );

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(
                encoder.encode(
                        Mono.just(body),
                        response.bufferFactory(),
                        ResolvableType.forInstance(body),
                        MediaType.APPLICATION_JSON,
                        null
                )
        );
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            return HttpStatus.resolve(errorResponse.getStatusCode().value());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}