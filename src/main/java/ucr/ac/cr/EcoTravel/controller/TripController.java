package ucr.ac.cr.EcoTravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.EcoTravel.model.Trip;
import ucr.ac.cr.EcoTravel.service.TripService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/add")
    public ResponseEntity<?> saveTrip(@Validated @RequestBody Trip trip, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Trip saved = this.tripService.saveTrip(trip);
            if (saved == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("TRIP " + trip.getId() + " ALREADY EXISTS");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.tripService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) { // Long
        Trip trip = this.tripService.findById(id);
        if (trip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TRIP NOT FOUND");
        }
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editTrip(@PathVariable Long id, @RequestBody Trip trip) { // Long
        Trip updated = this.tripService.editTrip(id, trip);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TRIP NOT FOUND");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id) {
        Trip trip = this.tripService.findById(id);
        if (trip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "TRIP NOT FOUND"));
        }
        this.tripService.deleteTrip(id);
        return ResponseEntity.ok(Map.of("message", "TRIP DELETED"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable Long userId) { // Long
        return ResponseEntity.ok(this.tripService.findByUserId(userId));
    }
}