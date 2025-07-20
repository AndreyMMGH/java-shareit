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
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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
}
