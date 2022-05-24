package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newRoomBuilder;

import br.com.sw2you.realmeet.api.facade.RoomApi;
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
        Assertions.assertEquals(room.getId(), dto.getId());
        Assertions.assertEquals(room.getSeats(), dto.getSeats());
        Assertions.assertEquals(room.getName(), dto.getName());
    }

    @Test
    public void testGetRoomInactive() {
        Room room = newRoomBuilder().active(false).build();
        roomRepository.saveAndFlush(room);

        Assertions.assertFalse(room.getActive());
        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(room.getId()));
    }

    @Test
    public void testRoomDoesNotExist() {
        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(DEFAULT_ROOM_ID));
    }
}
