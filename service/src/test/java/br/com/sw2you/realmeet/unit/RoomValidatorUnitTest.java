package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDto;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import domain.entity.Room;
import domain.repository.RoomRepository;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

class RoomValidatorUnitTest extends BaseUnitTest {
    private RoomValidator victim;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void SetupEach() {
        victim = new RoomValidator(roomRepository);
    }

    @Test
    void testValidateWhenRoomIsValid() {
        victim.validate(newCreateRoomDto());
    }

    @Test
    void testValidateWhenRoomNameIsMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDto().name(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomNameExceedsLength() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDto().name(StringUtils.rightPad("x", ROOM_NAME_MAX_LENGTH + 1, "x")))
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + EXCEEDED), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomSeatsAreMissing() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDto().seats(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomSeatsAreLessThanMinValue() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDto().seats(ROOM_SEATS_MIN_VALUE - 1))
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + BELOW_MIN_VALUE),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomSeatsAreGreaterThanMaxValue() {
        var exception = Assertions.assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDto().seats(ROOM_SEATS_MAX_VALUE + 1))
        );
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + EXCEEDS_MAX_VALUE),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    public void testValidateWhenRoomNameIsDuplicated() {
        given(roomRepository.findByNameAndActive(DEFAULT_ROOM_NAME, true))
            .willReturn(Optional.of(Room.newBuilder().build()));

        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate(newCreateRoomDto()));
        assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
        assertEquals(
            new ValidationError(ROOM_NAME, ROOM_NAME + DUPLICATED),
            exception.getValidationErrors().getError(0)
        );
    }
}
