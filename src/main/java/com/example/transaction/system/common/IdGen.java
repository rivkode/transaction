package com.example.transaction.system.common;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.random.RandomGenerator;

@Slf4j
@Component
public class IdGen {
    private static final int ASCII_LOWER_CASE_START = 97;
    private static final int ASCII_LOWER_CASE_END = 122;
    private static final int ASCII_UPPER_CASE_START = 65;
    private static final int ASCII_UPPER_CASE_END = 90;
    private static final int ASCII_DIGIT_START = 48;
    private static final int ASCII_DIGIT_END = 57;
    private static final int TIME_NUMBER_START = 0;
    private static final int TIME_UPPER_CASE_START = 10;
    private static final int TIME_LOWER_CASE_START = 36;


    private final HashMap<Integer, Integer> asciiMap = new HashMap<>();
    private final HashMap<Integer, Character> timeMap = new HashMap<>();

    public IdGen() {
        initializeAsciiMap();
        initializeTimeMap();
    }

    public String generateId(String s, Instant createAt, IdPrefix idPrefix) {
        RandomGenerator generator = RandomGenerator.of("L128X256MixRandom");
        System.out.printf("random number = %d", generator.nextInt(10000));
        Integer pseudo = generator.nextInt(10000);

        String generatedId = hash(s, pseudo);
        String timeBit = calculateInstant(createAt);
        generatedId = idPrefix.getValue() + "-" + timeBit + "-" + generatedId;
        log.info("time : "+ timeMap.get(0));
        log.info("time : "+ timeMap.get(20));
        log.info("time : "+ timeMap.get(7));
        return generatedId;
    }

    /**
     * Instant에서 년, 월, 일, 시, 분을 추출
     * 년도는 숫자 그대로 나타내며
     * 월, 일, 시, 분에 대해 timeMap에서 값을 가져와 String 생성
     * timeMap은 0-9 / A-Z / a-z 순으로 이루어진 hashMap
     * 이렇게 함으로써 시간순으로 정렬이 가능하도록 됨
     * @param createAt
     * @return
     */
    private String calculateInstant(Instant createAt) {

        int year = createAt.atZone(java.time.ZoneOffset.UTC).getYear();
        int yearFirstDigit = year / 100;
        int yearSecondDigit = year % 100;
        int month = createAt.atZone(java.time.ZoneOffset.UTC).getMonthValue();
        int day = createAt.atZone(java.time.ZoneOffset.UTC).getDayOfMonth();
        int hour = createAt.atZone(java.time.ZoneOffset.UTC).getHour();
        int minute = createAt.atZone(java.time.ZoneOffset.UTC).getMinute();

        String total = String.valueOf(yearSecondDigit);

        Character CMonth = timeMap.getOrDefault(month, '0');
        Character CDay = timeMap.getOrDefault(day, '0');
        Character CHour = timeMap.getOrDefault(hour, '0');
        Character CMinute = timeMap.getOrDefault(minute, '0');

        total = total + CMonth + CDay + CHour + CMinute;
        log.info(total);

        return total;
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
     * @param pseudo
     */
    private String hash(String name, Integer pseudo) {
        StringBuffer hexSb = new StringBuffer();
        StringBuffer binarySb = new StringBuffer();
//        log.info("createAt : " + createAt.toString());

        String lastFourChar = name.substring(name.length() - 4);
        String SPseudo = String.format("%4s", pseudo).replace(' ', '0');
//        String firstFourDigits = String.valueOf(createAt.getNano());
//        firstFourDigits = firstFourDigits.substring(0, 4);
//        log.info("firstFourDigits : " + firstFourDigits);
//
        String mixedString = mixString(lastFourChar, SPseudo);
        log.info("mixedString : " + mixedString);

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

    private void initializeTimeMap() {
        char v = '0';
        for (int i = TIME_NUMBER_START; i < 10; i++) {
            timeMap.put(i, v);
            log.info("v : " + v);
            v++;

        }

        v = 'A';

        for (int i = TIME_UPPER_CASE_START; i < 36; i++) {
            timeMap.put(i, v);
            log.info("v : " + v);
            v++;

        }

        v = 'a';

        for (int i = TIME_LOWER_CASE_START; i < 62; i++) {
            timeMap.put(i, v);
            log.info("v : " + v);
            v++;
        }
    }


    private void initializeAsciiMap() {
        // ASCII_DIGIT_START - 48
        int v = ASCII_DIGIT_START;
        for (int i = 0; i < 10; i++) {
            asciiMap.put(i, v);
            v++;
        }

        // ASCII_UPPER_CASE_START - 65
        v = ASCII_UPPER_CASE_START;
        for (int i = 10; i < 36; i++) {
            asciiMap.put(i, v);
            v++;
        }

        // ASCII_LOWER_CASE_START - 97
        v = ASCII_LOWER_CASE_START;
        for (int i = 36; i < 48; i++) {
            asciiMap.put(i, v);
            v++;
        }

        // 중간값 계산
        v = 109;
        for (int i = 58; i < 65; i++) {
            asciiMap.put(i, v);
            v++;
        }

        // 중간값 계산
        v = 115;
        for (int i = 91; i < 97; i++) {
            asciiMap.put(i, v);
            v++;
        }

        // ASCII_DIGIT_START - 48
        v = ASCII_DIGIT_START;
        for (int i = 123; i < 128; i++) {
            asciiMap.put(i, v);
            v++;
        }
    }
}