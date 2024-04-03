package com.example.transaction.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class MessageService {
    @Value("${discord.webhook.alert.url}")
    String webHookUrl;

    public boolean send(String message) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json; utf-8");
            HttpEntity<Message> messageHttpEntity = new HttpEntity<>(new Message(message), httpHeaders);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    webHookUrl,
                    HttpMethod.POST,
                    messageHttpEntity,
                    String.class
            );

            if (response.getStatusCode().value() != HttpStatus.NO_CONTENT.value()) {
                log.error("메세지 전송 이후 에러");
                return false;
            }
        } catch (Exception e) {
            log.error("에러 발생 : " + e.toString());
            return false;
        }
        return true;
    }


}
