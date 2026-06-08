package ucr.ac.cr.EcoTravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.EcoTravel.model.DTO.ComparisonResultDTO;
import ucr.ac.cr.EcoTravel.model.DTO.FactorUpdateRequest;
import ucr.ac.cr.EcoTravel.model.DTO.StatsDTO;
import ucr.ac.cr.EcoTravel.model.DTO.TripComparisonRequest;
import ucr.ac.cr.EcoTravel.service.ImpactService;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/impact")
public class ImpactController {

    @Autowired
    private ImpactService impactService;

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getStats(@PathVariable Long userId) { // Long
        StatsDTO stats = this.impactService.getStatsByUserId(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/factors")
    public ResponseEntity<?> getAllFactors() {
        return ResponseEntity.ok(this.impactService.getAllFactors());
    }

    @PostMapping("/factors/edit")
    public ResponseEntity<?> updateFactor(@RequestBody FactorUpdateRequest request) {
        try {
            return ResponseEntity.ok(this.impactService.updateFactor(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR UPDATING FACTOR: " + e.getMessage());
        }
    }

    @PostMapping("/compare")
    public ResponseEntity<?> compare(@RequestBody TripComparisonRequest request) {
        try {
            ComparisonResultDTO result = this.impactService.compare(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) { // capturar cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
}