package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:postgresql://localhost:5439/test",
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private Long userId;

    @BeforeEach
    void setUo() {
        User user = new User();
        user.setName("Егор");
        user.setEmail("egor@mail.ru");

        em.persist(user);
        em.flush();

        userId = user.getId();
    }

    @Test
    public void mustCreateItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Ищу строительный пылесос", userId);

        ItemResponseDto itemResponseDto = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertThat(itemResponseDto.getId()).isNotNull();
        assertThat(itemResponseDto.getDescription()).isEqualTo(itemResponseDto.getDescription());
        assertThat(itemResponseDto.getUserId()).isEqualTo(userId);
        assertThat(itemResponseDto.getCreated()).isNotNull();
        assertThat(itemResponseDto.getItems()).isEmpty();


        ItemRequest savedItemRequest = em.createQuery(
                        "SELECT ir FROM ItemRequest ir WHERE ir.id = :id", ItemRequest.class)
                .setParameter("id", itemResponseDto.getId())
                .getSingleResult();

        assertThat(savedItemRequest.getDescription()).isEqualTo("Ищу строительный пылесос");
        assertThat(savedItemRequest.getRequestor().getId()).isEqualTo(userId);
    }
}
