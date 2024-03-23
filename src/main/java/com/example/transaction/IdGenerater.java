package com.example.transaction;

import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class IdGenerater {
    Integer x;
    Integer y;
    Integer z;

    public String generateId(String s, LocalDateTime createAt) {
        hash(s, createAt);
        return "";
    }


    /**
     * hash 함수 동작 원리
     *
     * 1. name, createAt을 입력받는다
     * 2. 이름의 끝 4자리 + 날짜 끝 4자리를 사용한다
     * 3. 이름날짜이름날짜... 순으로 서로를 번갈아가며 1개의 조합을 만든다
     * 4. 8개로 나눈 뒤 각 문자에 대해 8bit로 표현하여 총 64bit를 만든다
     * 5. 64bit로 표현한 값을 4만큼 left shift 수행한다 '<<4'
     * 6. 왼쪽으로 shift하고 남은 자리인 4bit에 대해 name의 총 길이정보를 넣는다
     * 7. 처리가 끝난 hash 값에 대해 8bit로 각각 나누어 다시 8자리 문자로 표현한다.
     *
     * @param name
     * @param createAt
     */
    private void hash(String name, LocalDateTime createAt) {
        String[] binaryString = new String[16];

        // Single Thread 에서는 StringBuilder 사용
        // 만약 Multi Thread 환경이라면 StringBuffer 사용 고려
        StringBuffer sb = new StringBuffer();

        String lastFourChar = name.substring(name.length() - 4);
        // sample
        lastFourChar = "abcd";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmssSSSS");

        // LocalDateTime을 지정된 포맷으로 변환합니다.
        String formattedDateTime = createAt.format(formatter);

        // 마지막 4자리 숫자를 추출합니다.
        String lastFourDigits = formattedDateTime.substring(formattedDateTime.length() - 4);

        // sample
        lastFourDigits = "1234";
        log.info(lastFourDigits);

        String mixedString = mixString(lastFourChar, lastFourDigits);
        log.info(mixedString);

        int j = 0;
        for (int i = 0; i < 8; i++) {
            char c = mixedString.charAt(i);
            log.info("c : " + c);
            String hex = Integer.toHexString(c); // char를 16진수 문자열로 변환. ex) m = 6D

            sb.append(hex.charAt(0));

            boolean isDigit = Character.isDigit(hex.charAt(1));

            if (isDigit) {
                sb.append(hex.charAt(1));
            } else {
                char charAt = hex.charAt(1);
                sb.append(Character.toUpperCase(charAt));
            }

            log.info("hex : " + hex);

            log.info("StringBuilder : " + sb.charAt(j));
            log.info("StringBuilder : " + sb.charAt(j + 1));
            j +=2 ;
        }

        for (int i = 0; i < 16; i++) {
            int value;

            char ch = sb.charAt(i); // 변환할 타켓
            log.info("ch : " + ch);
            boolean isDigit = Character.isDigit(ch); // 문자가 숫자인지 확인

            if (isDigit) {
                value = Character.digit(ch, 10); // 숫자인 경우 10진수로 변환
                log.info("value : " + value);
            } else {
                value = Character.digit(ch, 16); // 숫자가 아닌 경우 16진수로 변환
                log.info("value : " + value);
            }

            String binary = Integer.toBinaryString(value); // 2진수로 변환

            // 결과를 4자리로 맞추기 위해 0 추가
            binary = String.format("%4s", binary).replace(' ', '0');
            log.info("final binary : " + binary);

            // 6일경우 0110 으로 저장되어야 함
            binaryString[i] = binary;
        }



        int n = name.length();
        String nameLengthBinary = String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');

        for (int i = 0; i < 15; i++) {
            binaryString[i] = binaryString[i + 1];
        }
        binaryString[15] = nameLengthBinary;

        String token = String.join("", binaryString);
        log.info("token : " + token);

        token += "0";
        String finalToken = token.substring(1);
        log.info("finalToken : " + finalToken);

        String[] hexToken = new String[8];
        StringBuffer sbCollect = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            String binary = token.substring(i, i + 8);
            String hex = Integer.toString(Integer.parseInt(binary, 2), 16);
            hexToken[i] = hex;
            char asciiChar = (char) Integer.parseInt(hex, 16);
            sbCollect.append(asciiChar);
        }

        String fin = sbCollect.toString();
        log.info("fin : " + fin);


        log.info(binaryString[0] + " " +
                binaryString[1] + " " +
                binaryString[2] + " " +
                binaryString[3] + " " +
                binaryString[4] + " " +
                binaryString[5] + " " +
                binaryString[6] + " " +
                binaryString[7] + " " +
                binaryString[8] + " " +
                binaryString[9] + " " +
                binaryString[10] + " " +
                binaryString[11] + " " +
                binaryString[12] + " " +
                binaryString[13] + " " +
                binaryString[14] + " " +
                binaryString[15]
                );
    }

    private String mixString(String lastFourChar, String lastFourDigits) {
        StringBuilder result = new StringBuilder();
        int j = 0;
        for (int i = 0; i < 4; i++) {
            result.append(lastFourChar.charAt(i));
            result.append(lastFourDigits.charAt(i));
            log.info("mix StringBuilder : " + result.charAt(j));
            log.info("mix StringBuilder : " + result.charAt(j+1));
            j += 2;
        }

        return result.toString();
    }
}
