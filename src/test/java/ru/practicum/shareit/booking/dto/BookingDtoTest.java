package ru.practicum.shareit.booking.dto;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoTest {
    private final String dtf = "yyyy-MM-dd'T'hh:mm:ss.SSSSSSSSS";
    private final EasyRandom generator = new EasyRandom();
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;


    @Test
    void bookingDtoTest() throws IOException {
        BookingDto bookingDto = generator.nextObject(BookingDto.class);
        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo(bookingDto.getBookerId());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(DateTimeFormatter.ofPattern(dtf).format(bookingDto.getStart()));
    }

    @Test
    void bookingDtoJsonTest() throws IOException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        String jsonBody = "{\n" +
                "    \"itemId\": 2,\n" +
                "    \"start\": \"" + DateTimeFormatter.ofPattern(dtf).format(startTime) + "\",\n" +
                "    \"end\": \"" + DateTimeFormatter.ofPattern(dtf).format(endTime) + "\"\n" +
                "}";

        BookingDto bookingDto = jacksonTester.parse(jsonBody).getObject();
        assertEquals(startTime, bookingDto.getStart());
        assertEquals(endTime, bookingDto.getEnd());
    }
}
