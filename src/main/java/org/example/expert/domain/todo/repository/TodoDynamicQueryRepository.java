package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoDynamicQueryRepository {
    Page<Todo> findAllByWeatherAndDateOrderByModifiedAtDesc(String weather, LocalDate from, LocalDate to, Pageable pageable);
    Page<TodoSearchResponse> search(String title, LocalDate from, LocalDate to, Pageable pageable);
    Page<TodoSearchResponse> searchVer2(String title, LocalDate from, LocalDate to, Pageable pageable);
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
