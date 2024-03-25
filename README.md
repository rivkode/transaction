# Transaction 토이 프로젝트

요구사항

핵심

- 필요한 기능들을 직접 구현
  - ex JWT, 검색 등
- 구현하며 공부한 내용 직접 정리

DB 설치시 참고 블로그

- [MongoDB](https://blog.naver.com/sudoku1/223050233311)
- [MySQL](https://hongong.hanbit.co.kr/mysql-%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C-%EB%B0%8F-%EC%84%A4%EC%B9%98%ED%95%98%EA%B8%B0mysql-community-8-0/)

## UUID 구현

## 날짜 데이터를 Instant로 해야하는 이유

날짜 데이터 다룰때에 참고 블로그

- [Java의 날짜, 시간에 대한 기본적인 정책](https://dev.gmarket.com/49)

## 거래 데이터를 NoSQL로 정한 이유

Transaction의 경우 데이터 저장소 구조는 NoSQL이 적합하다고 판단하였습니다.

그 이유는

1. 데이터 구조가 단순하다.
   1. id, send_user_id, receive_user_id, amount, time 만 존재
2. 데이터 저장이 조회보다 빈번하게 발생한다.
   1. 송금 데이터의 경우 생성이 더 빈번하게 발생
3. 다른 Entity간 Join에 대한 요구사항이 없다.
   1. 데이터 송금의 경우 다른사람과의 Join이 발생할 확률이 낮기 때문에 관계형 데이터베이스를 사용할 이유가 크게 없음

user_entity_id 정보를 저장하기 위해 2가지 방법이 존재

1. DBRef 사용
   1. 객체 간 관계를 유지할 수 있음
   2. 예를 들어 UserEntity를 로드하면 Transaction도 함께 로드될 수 있으며
   3. 반대로 UserEntity가 삭제될 경우 해당 Transaction 도 함께 삭제될 수 있음
   4. 의존성 증가
2. 직접 user_entity_id 저장
   1. 단순히 외래키를 저장하는것과 유사하여 유연성을 제공
   2. 데이터 일관성과 무결성을 직접 관리

`일관성, 무결성 관리 vs NoSQL의 유연성` 사이의 트레이드 오프

나는 NoSQL의 장점을 살리고자 직접 외래키를 저장하는 방법 채택

