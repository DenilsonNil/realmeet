package br.com.sw2you.realmeet.controller;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import br.com.sw2you.realmeet.api.facade.RoomsApi;
import br.com.sw2you.realmeet.api.model.RoomDTO;
import br.com.sw2you.realmeet.utils.ResponseEntityUtils;
import domain.service.RoomService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController implements RoomsApi {
    private final RoomService roomService;
    private final Executor controllersExecutor;

    public RoomController(RoomService roomService, Executor controllerExecutor) {
        this.roomService = roomService;
        this.controllersExecutor = controllerExecutor;
    }

    @Override
    public CompletableFuture<ResponseEntity<RoomDTO>> getRoom(@Valid Long id) {
        //        return supplyAsync(() -> ResponseEntity.ok(roomService.findById(id)));
        //        return supplyAsync(() -> roomService.findById(id), controllersExectutor).thenApply(r -> ResponseEntity.ok(r));
        return supplyAsync(() -> roomService.findById(id), controllersExecutor).thenApply(ResponseEntityUtils::ok);
    }
}
