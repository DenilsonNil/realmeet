package domain.service;

import static java.util.Objects.requireNonNull;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.RoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateRoomDTO;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.mapper.RoomMapper;
import br.com.sw2you.realmeet.validator.RoomValidator;
import domain.entity.Room;
import domain.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomValidator roomValidator;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, RoomValidator roomValidator) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.roomValidator = roomValidator;
    }

    public RoomDTO getRoom(Long id) {
        requireNonNull(id);
        Room room = getActiveRoomOrThrow(id);
        return roomMapper.fromEntityToDto(room);
    }

    public RoomDTO createRoom(CreateRoomDTO createRoomDTO) {
        roomValidator.validate(createRoomDTO);
        Room room = roomMapper.fromCreateRoomDtoToEntity(createRoomDTO);
        roomRepository.save(room);

        return roomMapper.fromEntityToDto(room);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        getActiveRoomOrThrow(roomId);
        roomRepository.deactivate(roomId);
    }

    private Room getActiveRoomOrThrow(Long id) {
        return roomRepository
            .findByIdAndActive(id, true)
            .orElseThrow(() -> new RoomNotFoundException("Room not found " + id));
    }

    @Transactional
    public void updateRoom(Long id, UpdateRoomDTO updateRoomDTO) {
        getActiveRoomOrThrow(id);
        roomValidator.validate(id, updateRoomDTO);
        roomRepository.updateRoom(id, updateRoomDTO.getName(), updateRoomDTO.getSeats());
    }
}
