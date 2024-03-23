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
    private synchronized void hash(String name, LocalDateTime createAt) {
        String[] binaryString = new String[16];
        StringBuffer sb1 = new StringBuffer();

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

            // char를 16진수 문자열로 변환. ex) m = 6D
            // 하지만 c는 이미 문자임
            String hex = Integer.toHexString(c);
            // hex는 16진수이므로 여기서 바로 변환해서 넣으면 되지
            // hex 의 예시는 6d임 이거를 하나씩 분리해서 바로 2진수로 변환해서 넣으면 됨

            // 2진수 추출
            String binary1 = Integer.toBinaryString(hex.charAt(0));
            binary1 = String.format("%4s", binary1).replace(' ', '0');
            String binary2 = Integer.toBinaryString(hex.charAt(1));
            binary2 = String.format("%4s", binary2).replace(' ', '0');

            // 2진수 추가
            sb.append(binary1);
            sb.append(binary2);

            log.info("hex : " + hex);

            log.info("StringBuffer : " + sb.charAt(j));
            log.info("StringBuffer : " + sb.charAt(j + 1));
            j +=2 ;
        }
        log.info("end StringBuffer-----------------------------------------");

        for (int i = 0; i < 16; i++) {
            int value;

            char ch = sb.charAt(i); // 변환할 타켓

            // 어차피 처음부터 문자 16진수 이므로 바로 변환
            String binary = Integer.toBinaryString(ch);

            // 결과를 4자리로 맞추기 위해 0 추가
            binary = String.format("%4s", binary).replace(' ', '0');
            log.info("final binary : " + binary);

            sb1.append(binary);
        }

        log.info("end binary transfer -----------------------------------------");


        int n = name.length();
        String nameLengthBinary = String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');

//        // 4bit 만큼 left shift 함
//        for (int i = 0; i < 15; i++) {
//            binaryString[i] = binaryString[i + 1];
//        }
//        // 오른쪽 끝에 이름 길이 정보 대체
//        binaryString[15] = nameLengthBinary;
//
//        sb1.append(nameLengthBinary);

        String sbString = sb.toString();
        sbString = sbString.substring(4);

        sb.append(nameLengthBinary);

        String sbString1 = sbString.substring(1);
        sbString1 += "0";


        String token = String.join("", binaryString);
        log.info("token : " + token);

        // 1bit left shift
        token += "0";
        String finalToken = token.substring(1);
        log.info("finalToken : " + finalToken);

        String a = sb1.toString();
        String b = a.substring(1);
        b += "0";


        String[] hexToken = new String[8];
        StringBuffer sbCollect = new StringBuffer();

        // 변환 과정

        for (int i = 0; i < 8; i++) {
            // 토큰에서 2진수를 총 8bit만큼 추출 -> 16진수로 만들기 위해
            String binary = token.substring(i, i + 8);

            // 토큰에서 가져온 2진수 8bit를 16진수로 변환
            String hex = Integer.toString(Integer.parseInt(binary, 2), 16);
//            hexToken[i] = hex;

            // 16진수를 다시 아스키코드로 변환
            char asciiChar = (char) Integer.parseInt(hex, 16);

            // StringBuffer에 추가
            sbCollect.append(asciiChar);
        }

        //if !7F안에 범위 내에 존재 하는가 ?
        // check()
        // check() 안에서는 범위안에 존재하지 않으므로 보수법을 통해 변환해준다

        //if 문자, 숫자 범위 내에 존재하는가?
        //그러면 그대로 출력
        //else
        //hashset에 넣은 뒤 결과를 바탕으로 출력

        String midHex = "80";

        for (int i = 0; i < 8; i++) {
            int isOver = hexToken[i].compareTo(midHex);
            if (isOver >= 0) {
                // 크므로
                // 보수법을 취한다
//                complement()
            } else {
                // 그렇지 않은 경우에는 아스키코드 범위(127)안에 있으므로 그대로 진행
            }
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

    private void complement(String hex) {
        int value;


        for (int i = 0; i < 2; i++) {
            char ch = hex.charAt(0); // 변환할 타켓
            log.info("ch : " + ch);
            boolean isDigit = Character.isDigit(ch); // 문자가 숫자인지 확인

            if (isDigit) {
                value = Character.digit(ch, 10); // 숫자인 경우 10진수로 변환
                log.info("value : " + value);
                value = ~value;
                log.info("value : " + value);
            } else {
                value = Character.digit(ch, 16); // 숫자가 아닌 경우 16진수로 변환
                log.info("value : " + value);
            }

            String binary = Integer.toBinaryString(value); // 2진수로 변환
//            binary = ~binary;
        }

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
