package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Егор");
        user.setEmail("egor@mail.ru");

        em.persist(user);
        em.flush();

        userId = user.getId();
    }

    @Test
    public void mustCreateItemRequest() {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto("Ищу строительный пылесос", userId);

        ItemReqResponseDto itemReqResponseDto = itemRequestService.createItemRequest(userId, itemReqRequestDto);

        assertThat(itemReqResponseDto.getId()).isNotNull();
        assertThat(itemReqResponseDto.getDescription()).isEqualTo(itemReqResponseDto.getDescription());
        assertThat(itemReqResponseDto.getUserId()).isEqualTo(userId);
        assertThat(itemReqResponseDto.getCreated()).isNotNull();
        assertThat(itemReqResponseDto.getItems()).isEmpty();


        ItemRequest savedItemRequest = em.createQuery(
                        "SELECT ir FROM ItemRequest ir WHERE ir.id = :id", ItemRequest.class)
                .setParameter("id", itemReqResponseDto.getId())
                .getSingleResult();

        assertThat(savedItemRequest.getDescription()).isEqualTo("Ищу строительный пылесос");
        assertThat(savedItemRequest.getRequestor().getId()).isEqualTo(userId);
    }
}
