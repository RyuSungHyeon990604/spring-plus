package org.example.expert.domain.user.repository;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.config.QueryDslConfig;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

@SpringBootTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String[] f = {
            "류", "김", "이", "박", "최", "정", "강", "조", "윤", "장",
            "임", "한", "오", "서", "신", "권", "황", "안", "송", "전",
            "홍", "고", "문", "양", "손", "배", "백", "허", "유", "남",
            "심", "노", "하", "곽", "성", "차", "주", "우", "민", "진",
            "구", "채", "원", "탁", "공", "엄", "길", "변", "반", "제"
    };

    private static final String[] m = {
            "성", "지", "민", "현", "준", "영", "우", "호", "혁", "도",
            "범", "석", "재", "찬", "수", "건", "승", "기", "형", "태",
            "훈", "진", "일", "명", "대", "범", "상", "환", "용", "시",
            "택", "연", "동", "규", "창", "태", "운", "영", "필", "건",
            "웅", "진", "강", "혁", "윤", "길", "찬", "한", "엽", "온"
    };

    private static final String[] l = {
            "현", "이", "은", "수", "민", "영", "우", "진", "혁", "호",
            "찬", "재", "범", "석", "환", "훈", "용", "태", "기", "상",
            "건", "승", "일", "형", "대", "명", "범", "성", "도", "안",
            "율", "환", "택", "성", "운", "원", "진", "규", "연", "혁",
            "윤", "강", "건", "웅", "찬", "필", "한", "길", "찬", "엽"
    };

    @Disabled
    @Test
    void 유저저장(){
        String sql = "INSERT INTO users (email, password, nick_name, user_role) VALUES (?, ?, ?, ?)";
        StopWatch stopWatch = new StopWatch();
        String encodedPassword = passwordEncoder.encode("123456");
        stopWatch.start();
        int batchSize = 1000;
        for (int i = 0; i < 100000000; i+=batchSize) {
            List<User> users = LongStream.range(i,  i + batchSize)
                    .mapToObj(o -> {
                        String name = randomName();
                        return new User(o + "@test.com", encodedPassword, name, UserRole.USER);
                    }).toList();
            jdbcTemplate.batchUpdate(
                    sql, new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            User user = users.get(i);
                            ps.setString(1, user.getEmail());
                            ps.setString(2, user.getPassword());
                            ps.setString(3,user.getNickName());
                            ps.setString(4,"USER");
                        }

                        @Override
                        public int getBatchSize() {
                            return users.size();
                        }
                    });
        }
        stopWatch.stop();
        System.out.println("total : "+stopWatch.getTotalTimeMillis());
    }

    @Disabled
    @Test
    void 유저저장_jparepository(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<User> users = LongStream.range(0,  10000)
                .mapToObj(o -> {
                    String name = randomName();
                    return new User(o + "@test.com", "password", name, UserRole.USER);
                }).toList();

        userRepository.saveAll(users);
        stopWatch.stop();
        System.out.println("total : "+stopWatch.getTotalTimeMillis());
    }

    @Test
    void 유저검색_where_nicName() {
        String search = "류성현";
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        userRepository.findUsersByNickName(search, PageRequest.of(0,10));
        stopWatch.stop();
        System.out.println("total : "+stopWatch.getTotalTimeMillis());
    }

    private String randomName(){
        Random random = new Random();
        String first = f[random.nextInt(f.length)];  // 성 랜덤 선택
        String middle = m[random.nextInt(m.length)]; // 중간 글자 랜덤 선택
        String last = l[random.nextInt(l.length)];   // 끝 글자 랜덤 선택
        return first + middle + last;
    }

}