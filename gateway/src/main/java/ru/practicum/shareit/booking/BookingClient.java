package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String BOOKINGS_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BOOKINGS_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> addBooking(int userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(int userId, int bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(int userId, int bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getAllBookingsOfUserByState(int userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state.name(), "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsOfUserItems(int userId, State state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state.name(), "from", from, "size", size);
        return get("/owner?state={state}&from={from}&size={size}", (long) userId, parameters);
    }
}
