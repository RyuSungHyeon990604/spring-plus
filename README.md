# SPRING PLUS

[코드 개선 퀴즈 - @transactional의 이해](https://github.com/RyuSungHyeon990604/spring-plus/pull/5/commits/0bf5af011e0f4a8901a2535452de0ecb17546961)

[코드 추가 퀴즈 - JWT의 이해](https://github.com/RyuSungHyeon990604/spring-plus/pull/5/commits/6265011ca7aca17910fd8b6ff5c977e4f659c770)

[코드 개선 퀴즈 - JPA의 이해](https://github.com/RyuSungHyeon990604/spring-plus/pull/5/commits/bbc7f0f430fffe99a9dd484fa29861fc0b945cf8)

[테스트 코드 퀴즈 - 컨트롤러 테스트의 이해](https://github.com/RyuSungHyeon990604/spring-plus/pull/5/commits/77992293ce0bf5ac7e49e6f0e10a916248743d5f)

[코드 개선 퀴즈 - AOP의 이해](https://github.com/RyuSungHyeon990604/spring-plus/pull/5/commits/f811d94be4357359e1b921f0f10e495cea52c146)


[JPA Cascade](https://github.com/RyuSungHyeon990604/spring-plus/pull/6/commits/4433a2cbbeb93c1be432a2879314f05656e5911d)

[N+1](https://github.com/RyuSungHyeon990604/spring-plus/pull/6/commits/f08ed1cddc5b54dcde8e9eed79a705347a8c0309)

[QueryDSL](https://github.com/RyuSungHyeon990604/spring-plus/commit/b5fae493d3ff7135c5b13c8a1eef46a683ce1b74)

[Spring Security](https://github.com/RyuSungHyeon990604/spring-plus/pull/7/commits/474675af5f7cd7a7574230c41ab8d0239a0d0e8e#diff-b8ee6a5b5c94de03b947df92ca54eb44a716ab6e53684ef7c595478099cf5814)

[QueryDSL 을 사용하여 검색 기능 만들기](https://github.com/RyuSungHyeon990604/spring-plus/pull/7/commits/2cf79f4ab5ef3d534d119473c71c0fdc90c70304)

[Transaction 심화](https://github.com/RyuSungHyeon990604/spring-plus/pull/7/commits/ae8d88bef4a2e71bd9b5d153fc3067718b443da1)

AWS 활용
![image](https://github.com/user-attachments/assets/80929096-c387-443a-9939-416a0baf6839)
![image](https://github.com/user-attachments/assets/abec55ad-bb98-4664-9c72-dfabd985b093)
[대용량 데이터 처리](https://github.com/RyuSungHyeon990604/spring-plus/pull/9/commits/bfe3b417ce01cbeb48d3752c03be7f4fc3bdafa3)



## 🚀 **JPA 대량 데이터 INSERT & 검색 성능 최적화 (트러블슈팅)**

### 🛠️ **문제 상황**

Spring Boot + JPA 환경에서 **대량 데이터(100만 건 이상) INSERT 시 속도 저하 & 메모리 문제** 발생

- `saveAll()` 사용 시 **1만 건 저장에 11초 소요**
- **100만 건 실행 시 프로그램 멈춤 (메모리 부족)**

---

## 🎯 **1️⃣ JPA의 `saveAll()`을 이용한 INSERT (비효율적)**

### **🔍 코드**

```java
List<User> users = LongStream.range(0,10000)  
                .mapToObj(o -> {  
                    String name = UUID.randomUUID().toString();  
                    User user = new User(name + "@test.com", "password", name, UserRole.USER);  
                    return user;  
                }).toList();  
stopWatch.start();  
userRepository.saveAll(users);  
stopWatch.stop();  
System.out.println("total : "+stopWatch.getTotalTimeMillis());  
```


### **⏱️ 실행 결과**

![JPA SaveAll 실행 결과](https://i.imgur.com/7jz9HxL.png)  
📌 **1만 건 저장 → `11,161ms` (약 11초)**  
📌 **100만 건 실행 시 프로그램 멈춤 (OutOfMemoryError 발생)**

### **❌ 원인 분석**

- JPA는 `saveAll()`을 실행하면 **엔티티를 영속성 컨텍스트에 저장** 후 **트랜잭션 종료 시 `flush()`**
- **모든 데이터를 메모리에 유지**하려고 하기 때문에 **대량 데이터 입력 시 OOM(OutOfMemoryError) 발생**
- `hibernate.jdbc.batch_size` 옵션을 추가하면 성능이 개선될 수도 있지만, **여전히 비효율적**

---

## 🎯 **2️⃣ JDBC Template을 활용한 Bulk Insert (속도 개선)**

### **✅ `rewriteBatchedStatements=true` 설정 추가**

MySQL의 경우 `rewriteBatchedStatements=true`를 추가하면 **INSERT 성능이 크게 개선됨**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/testdb?rewriteBatchedStatements=true
```



### **🔍 코드 (JDBC Template Batch Insert)**

```java
String sql = "INSERT INTO users (email, password, nick_name, user_role) VALUES (?, ?, ?, ?)";  
StopWatch stopWatch = new StopWatch();  
  
List<User> users = LongStream.range(0,10000)  
        .mapToObj(o -> {  
            String name = UUID.randomUUID().toString();  
            return new User(name + "@test.com", "password", name, UserRole.USER);  
        }).toList();  
stopWatch.start();  
jdbcTemplate.batchUpdate(  
        sql, new BatchPreparedStatementSetter() {  
            @Override  
            public void setValues(PreparedStatement ps, int i) throws SQLException {  
                User user = users.get(i);  
                ps.setString(1, user.getEmail());  
                ps.setString(2, user.getPassword());  
                ps.setString(3, user.getNickName());  
                ps.setString(4, "USER");  
            }  

            @Override  
            public int getBatchSize() {  
                return users.size();  
            }  
        });  
stopWatch.stop();  
System.out.println("total : "+stopWatch.getTotalTimeMillis());
```

### **⏱️ 실행 결과**

![JDBC Template 실행 결과](https://i.imgur.com/CiA80WX.png)  
📌 **1만 건 저장 → `111ms` (약 0.1초, JPA 대비 100배 빠름!)**

### **❌ 문제 발생**

📌 **100만 건 실행 시 메모리 부족 오류 발생 (OOM 에러)**  
📌 **100만 개의 객체를 한 번에 생성 & 저장 → 메모리 부족 문제 발생**

---

## 🎯 **3️⃣ 1000개씩 나누어 처리하여 메모리 절약 (Batch Insert)**

### **🔍 개선된 코드**

```java
String sql = "INSERT INTO users (email, password, nick_name, user_role) VALUES (?, ?, ?, ?)";  
StopWatch stopWatch = new StopWatch();  
stopWatch.start();  
int batchSize = 1000;  
for (int i = 0; i < 1000000; i+=batchSize) {  
    List<User> users = LongStream.range(i, i + batchSize)  
            .mapToObj(o -> {  
                String name = randomName();  
                return new User(o + "@test.com", "password", name, UserRole.USER);  
            }).toList();  
    jdbcTemplate.batchUpdate(  
            sql, new BatchPreparedStatementSetter() {  
                @Override  
                public void setValues(PreparedStatement ps, int i) throws SQLException {  
                    User user = users.get(i);  
                    ps.setString(1, user.getEmail());  
                    ps.setString(2, user.getPassword());  
                    ps.setString(3, user.getNickName());  
                    ps.setString(4, "USER");  
                }  

                @Override  
                public int getBatchSize() {  
                    return users.size();  
                }  
            });  
}  
stopWatch.stop();  
System.out.println("total : "+stopWatch.getTotalTimeMillis());
```
### **⏱️ 실행 결과**

![Batch Insert 실행 결과](https://i.imgur.com/7Af957a.png)  
📌 **100만 건 저장 → 약 `11초` (JPA `saveAll()`과 동일한 1만 건 기준 속도)**  
📌 **OOM 발생 없이 실행 가능**

### **❗ 추가 테스트: 1억 건 데이터 입력**
![1억 건 실행 결과](https://i.imgur.com/M5W4GmZ.png)  
📌 **1억 건 저장 → `32분` 소요 (대량 데이터 입력 성공)**

---

## 🎯 **4️⃣ 검색 성능 개선 (인덱스 추가)**

### **🔍 JPA 검색 코드**

```java
@Test  
void 유저검색_where_nicName() {  
    String search = "류성현";  
    StopWatch stopWatch = new StopWatch();  
    stopWatch.start();  
    List<User> byNickname = userRepository.findByNickName(search);  
    stopWatch.stop();  
    System.out.println("total : "+stopWatch.getTotalTimeMillis());  
}
```
### **⏱️ 실행 결과**

![기본 검색 속도](https://i.imgur.com/H8ZdgZQ.png)  
📌 **닉네임 검색 → `800ms` (느림)**

### **✅ 해결책: `INDEX` 추가**

```sql
CREATE INDEX idx_nick_name ON users(nick_name);
```
### **⏱️ 실행 결과 (인덱스 추가 후)**

![인덱스 추가 후 속도](https://i.imgur.com/K5d2RSv.png)  
📌 **닉네임 검색 → `300ms` (절반 이하로 줄어듦)**

### **🔍 SQL 쿼리 실행 비교 (DBeaver)**

#### **인덱스 적용 전**

![인덱스 전 실행 시간](https://i.imgur.com/JC4bddc.png)  
📌 **실행 시간: `0.5초`**

#### **인덱스 적용 후**

![인덱스 후 실행 시간](https://i.imgur.com/BVKgvGB.png)  
📌 **실행 시간: `0.002초` (250배 빨라짐)** 🚀

---

## 🔥 **트러블슈팅 요약**

|문제|해결책|결과|
|---|---|---|
|**JPA `saveAll()` 속도 저하**|**JDBC Template `batchUpdate()` 사용**|11초 → 0.1초 (100배 개선)|
|**100만 건 저장 시 OOM 발생**|**1000개씩 나누어 Batch Insert 실행**|메모리 문제 해결|
|**닉네임 검색 속도 느림**|**닉네임 컬럼에 인덱스 추가**|0.8초 → 0.3초 (2배 개선)|




