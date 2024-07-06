package ru.practicum.shareit.item.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchAvailableTest() {
        User owner = generator.nextObject(User.class);
        owner.setId(null);
        User savedUser = testEntityManager.persist(owner);
        Item item = new Item(null, "name", "desc", true, owner, owner.getId(), null);
        Item savedItem = itemRepository.save(item);
        List<Item> foundItems = itemRepository.searchAvailable(
                "name", Pageable.unpaged()
        );
        assertEquals(savedItem.getId(), foundItems.get(0).getId());
    }
}
