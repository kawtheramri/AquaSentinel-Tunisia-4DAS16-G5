
package controller;

import dto.MaintenanceRequestCreateDTO;
import dto.MaintenanceRequestResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import service.MaintenanceRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService service;

    @PostMapping
    public ResponseEntity<MaintenanceRequestResponseDTO> create(
            @Valid @RequestBody MaintenanceRequestCreateDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @GetMapping
    public List<MaintenanceRequestResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MaintenanceRequestResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MaintenanceRequestResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestCreateDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
