package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDto;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;
import static org.junit.jupiter.api.Assertions.*;

import br.com.sw2you.realmeet.api.facade.RoomApi;
import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateRoomDTO;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import domain.entity.Room;
import domain.repository.RoomRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

public class RoomApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private RoomApi roomApi;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(roomApi.getApiClient(), "/V1");
    }

    @Test
    public void testGetRoomSuccess() {
        Room room = newRoomBuilder().build();
        roomRepository.saveAndFlush(room);

        Assertions.assertNotNull(room.getId());
        Assertions.assertTrue(room.getActive());

        var dto = roomApi.getRoom(room.getId());
        assertEquals(room.getId(), dto.getId());
        assertEquals(room.getSeats(), dto.getSeats());
        assertEquals(room.getName(), dto.getName());
    }

    @Test
    public void testGetRoomInactive() {
        Room room = newRoomBuilder().active(false).build();
        roomRepository.saveAndFlush(room);

        assertFalse(room.getActive());
        assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(room.getId()));
    }

    @Test
    public void testRoomDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(DEFAULT_ROOM_ID));
    }

    @Test
    public void testCreateRoomSuccess() {
        var createRoomDTO = newCreateRoomDto();
        var roomDto = roomApi.createRoom(createRoomDTO);

        assertEquals(createRoomDTO.getName(), roomDto.getName());
        assertEquals(createRoomDTO.getSeats(), roomDto.getSeats());
        assertNotNull(roomDto.getId());

        var room = roomRepository.findById(roomDto.getId()).orElseThrow();
        assertEquals(roomDto.getName(), room.getName());
        assertEquals(roomDto.getSeats(), room.getSeats());
    }

    @Test
    public void testCreationRoomValidationError() {
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> roomApi.createRoom(newCreateRoomDto().name(null))
        );
    }

    @Test
    public void testDeleteRoomSuccess() {
        var roomId = roomRepository.saveAndFlush(Room.newBuilder().build()).getId();
        roomApi.deleteRoom(roomId);
        assertFalse(roomRepository.findById(roomId).orElseThrow().getActive());
    }

    @Test
    public void testDeleteRoomDoesNotExist() {
        var roomId = roomRepository.saveAndFlush(Room.newBuilder().build()).getId();
        assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.deleteRoom(1L));
    }

    @Test
    public void testUpdateRoomSuccess() {
        var room = roomRepository.saveAndFlush(Room.newBuilder().build());
        var updateRoomDto = (UpdateRoomDTO) new UpdateRoomDTO().name(room.getName() + "_").seats(room.getSeats() + 1);

        roomApi.updateRoom(room.getId(), updateRoomDto);

        var updatedRoom = roomRepository.findById(room.getId()).orElseThrow();
        assertEquals(updatedRoom.getName(), updatedRoom.getName());
        assertEquals(updatedRoom.getSeats(), updatedRoom.getSeats());
    }

    @Test
    public void testUpdateRoomDoesNotExist() {
        assertThrows(
            HttpClientErrorException.NotFound.class,
            () -> roomApi.updateRoom(1L, (UpdateRoomDTO) new UpdateRoomDTO().name("Room").seats(10))
        );
    }

    @Test
    public void testUpdateRoomValidationError() {
        var room = roomRepository.saveAndFlush(Room.newBuilder().build());
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> roomApi.updateRoom(room.getId(), (UpdateRoomDTO) new UpdateRoomDTO().name(null).seats(10))
        );
    }
}
