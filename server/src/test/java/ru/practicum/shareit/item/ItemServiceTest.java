package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void mustReturnUserItems() {
        User owner = new User();
        owner.setName("Владимир Петухов");
        owner.setEmail("Vladimir@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Петр Васильев");
        booker.setEmail("Petr@mail.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Canon 500d");
        item.setDescription("Зеркальный фотоаппарат");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(4));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(RentalStatus.APPROVED);
        em.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(4));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(RentalStatus.APPROVED);
        em.persist(futureBooking);

        Comment comment = new Comment();
        comment.setText("Все понравилось. Рекомендую!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now().minusHours(2));
        em.persist(comment);

        em.flush();
        em.clear();

        List<ItemBookingDto> result = itemService.findUserItems(owner.getId());

        assertThat(result).hasSize(1);

        ItemBookingDto itemBookingDto = result.get(0);
        assertThat(itemBookingDto.getName()).isEqualTo("Canon 500d");
        assertThat(itemBookingDto.getDescription()).isEqualTo("Зеркальный фотоаппарат");
        assertThat(itemBookingDto.getAvailable()).isEqualTo(true);
        assertThat(itemBookingDto.getLastBooking()).isNotNull();
        assertThat(itemBookingDto.getNextBooking()).isNotNull();
        assertThat(itemBookingDto.getComments()).hasSize(1);
        assertThat(itemBookingDto.getComments().get(0).getText()).isEqualTo("Все понравилось. Рекомендую!");
    }

    @Test
    void mustCreateItemWithRequestId() {
        User user = new User();
        user.setName("Андрей Алексеев");
        user.setEmail("Andrey@mail.ru");
        em.persist(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен строительный пылесос");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);
        em.flush();

        ItemDto itemDto = new ItemDto(null, "Karcher WD 2 Plus", "Строительный пылесос", true, itemRequest.getId());

        ItemDto result = itemService.createItem(user.getId(), itemDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void mustCreateItemWithoutRequestId() {
        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.ru");
        em.persist(user);
        em.flush();

        ItemDto itemDto = new ItemDto(null, "Dykemann", "Удобное кресло", true, null);

        ItemDto result = itemService.createItem(user.getId(), itemDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void mustUpdateItemFields() {
        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.ru");
        em.persist(user);

        Item item = new Item();
        item.setName("Стол для учебы");
        item.setDescription("Старый");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);
        em.flush();

        ItemDto updateItemDto = new ItemDto(null, "Новый стол для учебы", "Современный", false, null);

        ItemDto result = itemService.updateItem(item.getId(), user.getId(), updateItemDto);

        assertThat(result.getName()).isEqualTo("Новый стол для учебы");
        assertThat(result.getDescription()).isEqualTo("Современный");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void mustReturnItemWithBookingsForOwner() {
        User owner = new User();
        owner.setName("Макс Иванов");
        owner.setEmail("Max@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Андрей Алексеев");
        booker.setEmail("Andrey@mail.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Yamaha PACIFICA 012 BL");
        item.setDescription("Электрогитара");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking.setStatus(RentalStatus.APPROVED);
        em.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(3));
        futureBooking.setStatus(RentalStatus.APPROVED);
        em.persist(futureBooking);

        Comment comment = new Comment();
        comment.setText("Отличный инструмент!");
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now().minusHours(1));
        em.persist(comment);

        em.flush();
        em.clear();

        ItemBookingDto result = itemService.findItemById(owner.getId(), item.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void mustReturnItemWithoutBookingsForOtherUser() {
        User owner = new User();
        owner.setName("Макс Иванов");
        owner.setEmail("Max@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Андрей Алексеев");
        booker.setEmail("Andrey@mail.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Судьба человека");
        item.setDescription("Классическое произведение");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        em.flush();

        ItemBookingDto result = itemService.findItemById(booker.getId(), item.getId());

        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void mustSearchOnlyAvailableItems() {
        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.ru");
        em.persist(user);

        Item available = new Item();
        available.setName("Телевизор Samsung");
        available.setDescription("Smart TV");
        available.setAvailable(true);
        available.setOwner(user);
        em.persist(available);

        Item unavailable = new Item();
        unavailable.setName("Телевизор Rolsen");
        unavailable.setDescription("Аналоговый");
        unavailable.setAvailable(false);
        unavailable.setOwner(user);
        em.persist(unavailable);

        em.flush();

        List<ItemDto> result = itemService.searchItem("Телевизор");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAvailable()).isTrue();
    }

    @Test
    void mustCreateCommentAfterBooking() {
        User owner = new User();
        owner.setName("Макс Иванов");
        owner.setEmail("Max@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Андрей Алексеев");
        booker.setEmail("Andrey@mail.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Atom");
        item.setDescription("Велосипед горный");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setStatus(RentalStatus.APPROVED);
        em.persist(booking);

        em.flush();

        CommentRequestDto dto = new CommentRequestDto("Крутой велосипед! Спасибо!");
        CommentResponseDto result = itemService.createComment(booker.getId(), item.getId(), dto);

        assertThat(result.getText()).isEqualTo("Крутой велосипед! Спасибо!");
    }

    @Test
    void mustThrowWhenCommentWithoutBooking() {
        User owner = new User();
        owner.setName("Макс Иванов");
        owner.setEmail("Max@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Андрей Алексеев");
        booker.setEmail("Andrey@mail.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Canon 500d");
        item.setDescription("Зеркальный фотоаппарат");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        em.flush();

        CommentRequestDto dto = new CommentRequestDto("Хочу оставить отзыв");

        assertThatThrownBy(() -> itemService.createComment(booker.getId(), item.getId(), dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь может оставить комментарий после аренды");
    }
}
