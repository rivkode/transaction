package com.example.transaction;

import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        int[] hexList = new int[8];
        int[] binaryList = new int[64];
        String[] binaryString = new String[16];

        StringBuilder sb = new StringBuilder();

        String lastFourChar = name.substring(name.length() - 4);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmssSSS");

        // LocalDateTime을 지정된 포맷으로 변환합니다.
        String formattedDateTime = createAt.format(formatter);

        // 마지막 4자리 숫자를 추출합니다.
        String lastFourDigits = formattedDateTime.substring(formattedDateTime.length() - 4);

        String mixedString = mixString(lastFourChar, lastFourDigits);

        for (int i = 0; i < 8; i++) {
            char c = mixedString.charAt(i);
            String hex = Integer.toHexString(c); // char를 16진수 문자열로 변환. ex) m = 6D
            sb.append(hex);
        }

        for (int i = 0; i < 16; i++) {
            char c = sb.charAt(i);
            int decimal = Integer.parseInt(String.valueOf(c));
            String binary = Integer.toBinaryString(decimal);
            binaryString[i] = binary;
        }



        int n = name.length();
        String nameLengthBinary = Integer.toBinaryString(n);

        for (int i = 0; i < 16; i++) {
            if (i == 15) {
                binaryString[15] = nameLengthBinary;
                break;
            }
            binaryString[i] = binaryString[i + 1];
        }








    }

    private String mixString(String lastFourChar, String lastFourDigits) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            result.append(lastFourChar.charAt(i));
            result.append(lastFourDigits.charAt(i));
        }
        return result.toString();
    }
}
