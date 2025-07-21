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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    void mustReturnListOfYourQueriesWithAnswers() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен фотоаппарат");
        itemRequest.setRequestor(em.find(User.class, userId));
        em.persist(itemRequest);
        em.flush();

        List<ItemReqResponseDto> results = itemRequestService.getRequests(userId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Нужен фотоаппарат");
        assertThat(results.get(0).getItems()).isEmpty();
    }

    @Test
    void mustReturnListOfRequestsFromOtherUsers() {
        User requestor = new User();
        requestor.setName("Катя Петрова");
        requestor.setEmail("Katya@mail.ru");
        em.persist(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен фотоаппарат");
        itemRequest.setRequestor(requestor);
        em.persist(itemRequest);
        em.flush();

        List<ItemReqResponseDto> results = itemRequestService.getRequestsOtherUsers(userId, 0, 10);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Нужен фотоаппарат");
    }

    @Test
    void mustReturnSingleRequestWithAnswers() {
        User requestor = new User();
        requestor.setName("Катя Петрова");
        requestor.setEmail("Katya@mail.ru");
        em.persist(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Ищу гитару");
        itemRequest.setRequestor(requestor);
        em.persist(itemRequest);

        Item item = new Item();
        item.setName("Yamaha PACIFICA 012 BL");
        item.setDescription("Электрогитара");
        item.setAvailable(true);
        item.setOwner(em.find(User.class, userId));
        item.setItemRequest(itemRequest);
        em.persist(item);

        em.flush();

        ItemReqResponseDto result = itemRequestService.getRequestWithAnswers(userId, itemRequest.getId());

        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Yamaha PACIFICA 012 BL");
    }

    @Test
    void mustThrowWhenUserNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequests(999L))
                .isInstanceOf(ru.practicum.shareit.exception.NotFoundException.class);
    }

    @Test
    void mustThrowWhenRequestNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequestWithAnswers(userId, 999L))
                .isInstanceOf(ru.practicum.shareit.exception.NotFoundException.class);
    }

    @Test
    void mustReturnEmptyListWhenNoRequests() {
        var result = itemRequestService.getRequests(userId);
        assertThat(result).isEmpty();
    }
}
