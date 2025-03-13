package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImage extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String url;
    private String fileName;


    public UserImage(User user, String url, String fileName) {
        this.user = user;
        this.url = url;
        this.fileName = fileName;
    }

    public void update(String url, String fileName){
        this.url = url;
        this.fileName = fileName;
    }
}
