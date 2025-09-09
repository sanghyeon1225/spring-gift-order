package gift.controller;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<OptionResponseDto>> getOptions(@PathVariable Long productId) {
        return ResponseEntity.ok(optionService.getOptions(productId));
    }

    @PostMapping
    public ResponseEntity<OptionResponseDto> addOption(@PathVariable Long productId, @Valid @RequestBody OptionRequestDto optionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.addOption(productId, optionRequestDto));
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<OptionResponseDto> updateOption(
            @PathVariable Long productId,
            @PathVariable Long optionId,
            @Valid @RequestBody OptionRequestDto optionRequestDto
    ) {
        return ResponseEntity.ok(optionService.updateOption(productId, optionId, optionRequestDto));
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long productId, @PathVariable Long optionId) {
        optionService.deleteOption(productId, optionId);
        return ResponseEntity.ok().build();
    }
}
