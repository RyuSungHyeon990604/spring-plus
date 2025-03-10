package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class TodoDynamicQueryRepositoryImpl implements TodoDynamicQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Todo> findAllByWeatherAndDateOrderByModifiedAtDesc(String weather, LocalDate from, LocalDate to, Pageable pageable) {

        List<Todo> todos = queryFactory.selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(
                        weatherEq(weather)
                        , dateBetween(from, to)
                ).orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(Wildcard.count)
                .from(todo)
                .where(
                        weatherEq(weather)
                        , dateBetween(from, to)
                ).fetchOne();

        return new PageImpl<>(todos, pageable, count == null ? 0 : count);
    }

    private BooleanExpression weatherEq(String weather) {
        return weather == null || weather.isEmpty() ? null : todo.weather.eq(weather);
    }

    private BooleanExpression dateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return null;
        }

        LocalDate f = from == null ? LocalDate.of(1970,1,1) : from;
        LocalDate e = to == null ? LocalDate.now() : to;

        return todo.modifiedAt.between(LocalDateTime.of(f, LocalTime.MIDNIGHT)
                , LocalDateTime.of(e, LocalTime.MAX));
    }
}
