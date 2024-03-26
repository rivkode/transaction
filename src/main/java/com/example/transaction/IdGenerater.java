package com.example.transaction;

import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

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
    private synchronized void hash(String name, LocalDateTime createAt) {
        // Single Thread 에서는 StringBuilder 사용
        // 만약 Multi Thread 환경이라면 StringBuffer 사용 고려
        StringBuffer hexSb = new StringBuffer();

        StringBuffer binarySb = new StringBuffer();




        // number
        HashMap<Integer, Integer> firstAsciiMap = firstMapping();
        HashMap<Integer, Integer> secondAsciiMap = secondMapping();
        HashMap<Integer, Integer> thirdAsciiMap = thirdMapping();
        HashMap<Integer, Integer> fourthAsciiMap = fourthMapping();
        // hashMap 생성 완료


        HashMap<Integer, Integer> asciiMap = fourthMapping();
        asciiMap.putAll(firstAsciiMap);
        asciiMap.putAll(secondAsciiMap);
        asciiMap.putAll(thirdAsciiMap);
        asciiMap.putAll(fourthAsciiMap);



        String lastFourChar = name.substring(name.length() - 4);
        // sample
//        lastFourChar = "abcd";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmssSSSS");

        // LocalDateTime을 지정된 포맷으로 변환합니다.
        String formattedDateTime = createAt.format(formatter);

        // 마지막 4자리 숫자를 추출합니다.
        String lastFourDigits = formattedDateTime.substring(formattedDateTime.length() - 4);

        // sample
//        lastFourDigits = "1234";
        log.info(lastFourDigits);

        String mixedString = mixString(lastFourChar, lastFourDigits);
        log.info(mixedString);


        // 아래 for문은 입력받은 문자를 StringBuffer에 16진수의 형태로 입력하기 위함
        int j = 0;
        for (int i = 0; i < 8; i++) {
            char c = mixedString.charAt(i);
            log.info("c : " + c);

            // char를 16진수 문자열로 변환. ex) m = 6D
            // 하지만 c는 이미 문자임
            // 아님 .. 16진수를 10진수로 변환하기 위해 숫자인지 문자인지 판단 필요

            String hex = Integer.toHexString(c);
            hexSb.append(hex.charAt(0));

            boolean isDigit = Character.isDigit(hex.charAt(1));

            if (isDigit) {
                hexSb.append(hex.charAt(1));
            } else {
                char charAt = hex.charAt(1);
                hexSb.append(Character.toUpperCase(charAt));
            }

            log.info("hex : " + hex);

            log.info("StringBuffer : " + hexSb.charAt(j));
            log.info("StringBuffer : " + hexSb.charAt(j + 1));
            j +=2 ;
        }
        log.info("end StringBuffer-----------------------------------------");

        // 16진수 -> 10진수 -> 2진수 변환 과정
        for (int i = 0; i < 16; i++) {
            int value;

            char ch = hexSb.charAt(i); // 변환할 타켓
            log.info("ch : " + ch);
            boolean isDigit = Character.isDigit(ch); // 문자가 숫자인지 확인

            if (isDigit) {
                value = Character.digit(ch, 10); // 숫자인 경우 10진수로 변환
                log.info("value : " + value);
            } else {
                value = Character.digit(ch, 16); // 숫자가 아닌 경우 16진수로 변환
                log.info("value : " + value);
            }


            String binary = Integer.toBinaryString(value);

            // 결과를 4자리로 맞추기 위해 0 추가
            binary = String.format("%4s", binary).replace(' ', '0');
            log.info("final binary : " + binary);

            binarySb.append(binary);
        }

        log.info("end binary transfer -----------------------------------------");

        // 이름 길이 정보 추가
        int n = name.length();
        String nameLengthBinary = String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
        binarySb.append(nameLengthBinary);

        // 4bit 만큼 left shift
        String binarySbString = binarySb.toString();
        binarySbString = binarySbString.substring(4);


        // 1bit만큼 left shift
        String transferedBinary = binarySbString.substring(1);
        transferedBinary += "0";

        log.info("leftShiftOneBit : " + transferedBinary);


        String[] hexToken = new String[8];
        StringBuffer transferedToChar = new StringBuffer();



        // 변환 과정
        for (int i = 0; i < 8; i++) {
            boolean isASCII;
            boolean isBelongNumberOrChar;
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

            // 숫자, 글자 범위안에 속하는지 체크
            int first = Integer.parseInt(binary.substring(0, 4), 2);
            int last = Integer.parseInt(binary.substring(4, 8), 2);
            int decimalNumber = (first * (int)Math.pow(2, 4)) + last;
            log.info("first : " + first);
            log.info("last : " + last);

            log.info("decimalNumber : " + decimalNumber);

            // 속하지 않는다면 hashMap을 통해 decimalNumber을 매핑
            // 이렇게 매핑하면 decimalNumber은 숫자 혹은 글자인 아스키코드 십진수를 가지게 된다.
            if (!((47 < decimalNumber && decimalNumber < 58) || (64 < decimalNumber && decimalNumber < 91) || (96 < decimalNumber && decimalNumber < 123))) {
                // hashset 과 매칭
                decimalNumber = asciiMap.get(decimalNumber);
            }





            // 10진수를 알파벳으로 변환
            char asciiChar = (char) decimalNumber;

            // StringBuffer에 추가
            transferedToChar.append(asciiChar);
        }

        String midHex = "80";


        String fin = transferedToChar.toString();
        log.info("transferedToChar : " + fin);
    }

    private HashMap<Integer, Integer> firstMapping() {
        HashMap<Integer, Integer> firstAsciiMap = new HashMap<>();


        int v = 48;
        for (int i = 0; i < 9; i++) {
            firstAsciiMap.put(i, v);
            v++;
        }
        // 52까지


        v = 65;
        for (int i = 10; i < 36; i++) {
            firstAsciiMap.put(i, v);
            v++;
        }
        // 90까지

        v = 97;
        for (int i = 36; i < 48; i++) {
            firstAsciiMap.put(i, v);
        }
        log.info("firstAsciiMap");

        return firstAsciiMap;
        // 108까지
    }

    private HashMap<Integer, Integer> secondMapping() {
        HashMap<Integer, Integer> secondAsciiMap = new HashMap<>();

        int v = 109;
        for (int i = 58; i < 64; i++) {
            secondAsciiMap.put(i, v);
            v++;
        }
        // 115까지

        log.info("secondAsciiMap");

        return secondAsciiMap;
    }

    private HashMap<Integer, Integer> thirdMapping() {
        HashMap<Integer, Integer> thirdAsciiMap = new HashMap<>();

        int v = 115;
        for (int i = 91; i < 97; i++) {
            thirdAsciiMap.put(i, v);
            v++;
        }
        // 121까지

        log.info("thirdAsciiMap");

        return thirdAsciiMap;
    }

    private HashMap<Integer, Integer> fourthMapping() {
        HashMap<Integer, Integer> fourthAsciiMap = new HashMap<>();

        int v = 121;
        for (int i = 123; i < 128; i++) {
            fourthAsciiMap.put(i, v);
            v++;
        }

        log.info("fourthAsciiMap");
        return fourthAsciiMap;
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