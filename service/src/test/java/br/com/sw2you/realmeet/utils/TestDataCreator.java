package br.com.sw2you.realmeet.utils;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_SEATS;
import static domain.entity.Room.newBuilder;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import domain.entity.Room;

public final class TestDataCreator {

    private TestDataCreator() {}

    public static Room.Builder newRoomBuilder() {
        return newBuilder().name(DEFAULT_ROOM_NAME).seats(DEFAULT_ROOM_SEATS);
    }

    public static CreateRoomDTO newCreateRoomDto() {
        return new CreateRoomDTO().name(DEFAULT_ROOM_NAME).seats(DEFAULT_ROOM_SEATS);
    }
}
