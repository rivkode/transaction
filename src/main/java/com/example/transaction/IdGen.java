package com.example.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Component
public class IdGen {
    private static final int ASCII_LOWER_CASE_START = 97;
    private static final int ASCII_LOWER_CASE_END = 122;
    private static final int ASCII_UPPER_CASE_START = 65;
    private static final int ASCII_UPPER_CASE_END = 90;
    private static final int ASCII_DIGIT_START = 48;
    private static final int ASCII_DIGIT_END = 57;

    private final HashMap<Integer, Integer> asciiMap = new HashMap<>();

    public IdGen() {
        initializeAsciiMap();
    }

    public String generateId(String s, LocalDateTime createAt) {
        return hash(s, createAt);
    }

    /**
     * hash 함수 동작 원리
     * ---- String -> 2진수 변환 시작 ----
     * 1. name, createAt을 입력
     * 2. 이름의 끝 4자리 + 날짜 끝 4자리를 사용
     * 3. 이름날짜이름날짜... 순으로 서로를 번갈아가며 1개의 조합을 만들고 String에 저장
     * 4. 해당 String을 char단위로 나누어 저장
     * 5. char들을 char -> 16진수 -> 10진수 -> 2진수 순으로 변환
     * 5. 변환한 값을 4bit 만큼 left shift 수행
     * 6. 왼쪽으로 shift하고 남은 자리인 4bit에 대해 입력받은 name의 길이정보를 2진수로 변환하여 넣음
     * 7. leftshit를 1bit만큼 다시 수행
     * 8. ---- 2진수로 변환 완료 ----
     *
     *  ---- 2진수 -> String 변환 시작 ----
     * 9. 2진수에 대해 숫자, 알파벳으로 변환하기 위해 ASCII 범위(0-127) 내에 속하는지 체크 (8bit는 256만큼 표현이 가능하므로)
     * 10. 속하지 않는다면 1의 보수법으로 변환
     * 11. 숫자, 글자범위에 속하는지 체크
     * 12. 속하지 않는다면 hashMap으로 매핑
     * 13. 2진수 -> 10진수 -> String 순으로 변환
     *  ---- String으로 변환 완료 ----
     *
     *
     * @param name
     * @param createAt
     */
    private String hash(String name, LocalDateTime createAt) {
        StringBuilder hexSb = new StringBuilder();
        StringBuilder binarySb = new StringBuilder();

        String lastFourChar = name.substring(name.length() - 4);
        String lastFourDigits = String.format("%04d", createAt.getSecond());

        String mixedString = mixString(lastFourChar, lastFourDigits);

        int j = 0;
        for (int i = 0; i < 8; i++) {
            char c = mixedString.charAt(i);
            log.info("c : " + c);
            String hex = Integer.toHexString(c);
            hexSb.append(hex.charAt(0));
            hexSb.append(Character.isDigit(hex.charAt(1)) ? hex.charAt(1) : Character.toUpperCase(hex.charAt(1)));
            log.info("hex : " + hex);

            log.info("StringBuffer : " + hexSb.charAt(j));
            log.info("StringBuffer : " + hexSb.charAt(j + 1));
            j +=2;
        }
        log.info("end StringBuffer-----------------------------------------");

        for (int i = 0; i < 16; i++) {
            int value;
            char ch = hexSb.charAt(i);
            log.info("ch : " + ch);
            if (Character.isDigit(ch)) {
                value = Character.digit(ch, 16);
            } else {
                value = Character.digit(ch, 16);
            }
            log.info("value : " + value);
            String binary = String.format("%4s", Integer.toBinaryString(value)).replace(' ', '0');
            log.info("final binary : " + binary);
            binarySb.append(binary);
        }
        log.info("end binary transfer -----------------------------------------");

        int n = name.length();
        String nameLengthBinary = String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
        binarySb.append(nameLengthBinary);

        String binarySbString = binarySb.substring(4);
        String transferedBinary = binarySbString.substring(1) + "0";
        log.info("leftShiftOneBit : " + transferedBinary);

        StringBuilder transferedToChar = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            boolean isASCII;
            // 토큰에서 2진수를 총 8bit만큼 추출 -> 16진수로 만들기 위해
            String binary = transferedBinary.substring(i, i + 8);
//            String binary = transferedBinary.substring(i * 8, (i + 1) * 8);
            // 아스키 범위안에 속하는지 체크
            if (binary.charAt(0) == '0') {
                isASCII = true;
            } else {
                isASCII = false;
            }

            // 범위가 아니라면 1의 보수를 통해 아스키범위로 전환
            if (!isASCII) {
                binary = onesComplement(binary);
            }

            log.info("isASCII : " + isASCII);
            log.info("binary ASCII : " + binary);

            int first = Integer.parseInt(binary.substring(0, 4), 2);
            int last = Integer.parseInt(binary.substring(4, 8), 2);
            int decimalNumber = (first * (int)Math.pow(2, 4)) + last;
            log.info("first : " + first);
            log.info("last : " + last);

            log.info("decimalNumber : " + decimalNumber);

//            int decimalNumber = Integer.parseInt(binary, 2);
            if (!isValidAscii(decimalNumber)) {
                decimalNumber = asciiMap.get(decimalNumber);
            }

            // 10진수를 알파벳으로 변환
            char asciiChar = (char) decimalNumber;

            // StringBuffer에 추가
            transferedToChar.append(asciiChar);
        }

        return transferedToChar.toString();
    }

    private boolean isValidAscii(int decimalNumber) {
        return (decimalNumber >= ASCII_DIGIT_START && decimalNumber <= ASCII_DIGIT_END) ||
                (decimalNumber >= ASCII_UPPER_CASE_START && decimalNumber <= ASCII_UPPER_CASE_END) ||
                (decimalNumber >= ASCII_LOWER_CASE_START && decimalNumber <= ASCII_LOWER_CASE_END);
    }

    private String mixString(String lastFourChar, String lastFourDigits) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            result.append(lastFourChar.charAt(i));
            result.append(lastFourDigits.charAt(i));
        }
        return result.toString();
    }

    // 1의 보수 계산 함수
    public static String onesComplement(String binary) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binary.length(); i++) {
            char bit = binary.charAt(i);
            if (bit == '0') {
                result.append('1');
            } else if (bit == '1') {
                result.append('0');
            } else {
                // 예외 처리: 0 또는 1이 아닌 문자가 있을 경우
                throw new IllegalArgumentException("Invalid binary input");
            }
        }
        return result.toString();
    }

    private void initializeAsciiMap() {
        int v = 48;
        for (int i = 0; i < 10; i++) {
            asciiMap.put(i, v);
            v++;
        }

        v = 65;
        for (int i = 10; i < 36; i++) {
            asciiMap.put(i, v);
            v++;
        }

        v = 97;
        for (int i = 36; i < 48; i++) {
            asciiMap.put(i, v);
            v++;
        }

        v = 109;
        for (int i = 58; i < 65; i++) {
            asciiMap.put(i, v);
            v++;
        }

        v = 115;
        for (int i = 91; i < 97; i++) {
            asciiMap.put(i, v);
            v++;
        }

        v = 48;
        for (int i = 123; i < 128; i++) {
            asciiMap.put(i, v);
            v++;
        }
    }
}