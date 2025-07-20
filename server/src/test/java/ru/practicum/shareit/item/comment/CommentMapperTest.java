package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentMapperTest {
    @Test
    void mustMapCommentRequestDtoToComment() {

        CommentRequestDto requestDto = new CommentRequestDto("Отличная вещь!");
        User author = new User(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );

        Item item = new Item(
                2L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                author,
                null
        );

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Comment comment = CommentMapper.toComment(requestDto, author, item);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(comment.getId()).isNull();
        assertThat(comment.getText()).isEqualTo("Отличная вещь!");
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getCreated()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
    }
}
