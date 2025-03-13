package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final ImageResponse image;

    public UserResponse(Long id, String email) {
        this.id = id;
        this.email = email;
        this.image = null;
    }

    public UserResponse(Long id, String email, ImageResponse image) {
        this.id = id;
        this.email = email;
        this.image = image;
    }
}
