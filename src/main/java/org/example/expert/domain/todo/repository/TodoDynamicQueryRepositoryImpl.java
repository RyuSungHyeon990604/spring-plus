package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.comment.entity.QComment.comment;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TodoDynamicQueryRepositoryImpl implements TodoDynamicQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Todo> findAllByWeatherAndDateOrderByModifiedAtDesc(String weather, LocalDate from, LocalDate to, Pageable pageable) {

        List<Todo> todos = queryFactory.selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(
                        weatherEq(weather)
                        , modifiedAtBetween(from, to)
                ).orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(Wildcard.count)
                .from(todo)
                .where(
                        weatherEq(weather)
                        , modifiedAtBetween(from, to)
                ).fetchOne();

        return new PageImpl<>(todos, pageable, count == null ? 0 : count);
    }

    @Override
    public Page<TodoSearchResponse> searchVer2(String title, LocalDate from, LocalDate to, Pageable pageable) {
        List<TodoSearchResponse> todos = queryFactory.select(Projections.constructor(TodoSearchResponse.class
                        , Projections.constructor(TodoResponse.class
                                , todo.id
                                , todo.title
                                , todo.contents
                                , todo.weather
                                , Projections.constructor(UserResponse.class, todo.user.id, todo.user.email)
                                , todo.createdAt
                                , todo.modifiedAt
                                )
                                , queryFactory.select(Wildcard.count).from(manager).where(manager.todo.eq(todo))
                                , queryFactory.select(Wildcard.count).from(comment).where(comment.todo.eq(todo)))
                        )
                .from(todo)
                .join(todo.user)
                .where(titleContains(title), createdAtBetween(from, to))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(titleContains(title), createdAtBetween(from, to))
                .fetchOne();

        return new PageImpl<>(todos, pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(queryFactory.select(todo)
                .from(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne());
    }

    @Override
    public Page<TodoSearchResponse> search(String title, LocalDate from, LocalDate to, Pageable pageable) {
        List<TodoSearchResponse> todos = queryFactory.select(Projections.constructor(TodoSearchResponse.class
                        , Projections.constructor(TodoResponse.class
                                , todo.id
                                , todo.title
                                , todo.contents
                                , todo.weather
                                , Projections.constructor(UserResponse.class, todo.user.id, todo.user.email)
                                , todo.createdAt
                                , todo.modifiedAt)
                        , manager.countDistinct()
                        , comment.countDistinct()))
                .from(todo)
                .join(todo.user)
                .leftJoin(manager).on(manager.todo.id.eq(todo.id))
                .leftJoin(comment).on(comment.todo.id.eq(todo.id))
                .where(titleContains(title), createdAtBetween(from, to))
                .groupBy(todo.id,todo.title, todo.contents, todo.weather, todo.user.id, todo.user.email, todo.createdAt, todo.modifiedAt)
                .orderBy(todo.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(titleContains(title), createdAtBetween(from, to))
                .fetchOne();

        return new PageImpl<>(todos, pageable, total == null ? 0 : total);
    }

    private BooleanExpression weatherEq(String weather) {
        return weather == null || weather.isEmpty() ? Expressions.TRUE : todo.weather.eq(weather);
    }

    private BooleanExpression titleContains(String title) {
        return title == null || title.isEmpty() ? Expressions.TRUE : todo.title.contains(title);
    }

    private BooleanExpression modifiedAtBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return Expressions.TRUE;
        }

        LocalDate f = from == null ? LocalDate.of(1970,1,1) : from;
        LocalDate e = to == null ? LocalDate.now() : to;

        return todo.modifiedAt.between(LocalDateTime.of(f, LocalTime.MIDNIGHT)
                , LocalDateTime.of(e, LocalTime.MAX));
    }

    private BooleanExpression createdAtBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return Expressions.TRUE;
        }

        LocalDate f = from == null ? LocalDate.of(1970,1,1) : from;
        LocalDate e = to == null ? LocalDate.now() : to;

        return todo.createdAt.between(LocalDateTime.of(f, LocalTime.MIDNIGHT)
                , LocalDateTime.of(e, LocalTime.MAX));
    }
}
