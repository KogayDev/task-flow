package com.kogay.taskflow.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kogay.taskflow.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;


/**
 * Utility class for sending JSON responses to clients.
 */
@UtilityClass
public class JsonResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a JSON response to the client.
     *
     * @param response      HTTP response.
     * @param errorResponse JSON response containing an error.
     */
    @SneakyThrows
    public static void send(HttpServletResponse response, final ErrorResponse errorResponse) {
        response.setStatus(errorResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, errorResponse);
        outputStream.flush();
    }
}

