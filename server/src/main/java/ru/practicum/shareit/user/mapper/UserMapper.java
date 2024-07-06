package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        } else {
            return new UserDto(user.getId(), user.getName(), user.getEmail());
        }
    }

    public static User toUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        } else {
            return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        }
    }

    public static User toUser(UserDto userDto, Integer userId) {
        if (userDto == null) {
            return null;
        } else {
            return new User(userId, userDto.getName(), userDto.getEmail());
        }
    }
}
