package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TodoSearchResponse {
    private final TodoResponse todo;
    private final Long managerCount;
    private final Long commentCount;
}
