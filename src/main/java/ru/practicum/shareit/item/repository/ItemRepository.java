package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int ownerId, Pageable pageable) throws UserNotFoundException;

    @Query(" SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(concat('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(concat('%', ?1, '%'))" +
            "AND i.available = TRUE")
    List<Item> searchAvailable(String text, Pageable pageable);
}
