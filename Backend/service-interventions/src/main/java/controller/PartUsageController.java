
package controller;

import dto.PartUsageRequestDTO;
import dto.PartUsageResponseDTO;
import service.PartUsageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests/{requestId}/parts")
@RequiredArgsConstructor
public class PartUsageController {

    private final PartUsageService service;

    @PostMapping
    public ResponseEntity<PartUsageResponseDTO> create(
            @PathVariable Long requestId,
            @Valid @RequestBody PartUsageRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(requestId, dto));
    }

    @GetMapping
    public List<PartUsageResponseDTO> getAll(@PathVariable Long requestId) {
        return service.getAll(requestId);
    }

    @GetMapping("/{partId}")
    public PartUsageResponseDTO getById(
            @PathVariable Long requestId,
            @PathVariable Long partId) {

        return service.getById(requestId, partId);
    }

    @PutMapping("/{partId}")
    public PartUsageResponseDTO update(
            @PathVariable Long requestId,
            @PathVariable Long partId,
            @Valid @RequestBody PartUsageRequestDTO dto) {

        return service.update(requestId, partId, dto);
    }

    @DeleteMapping("/{partId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long requestId,
            @PathVariable Long partId) {

        service.delete(requestId, partId);
    }
}
