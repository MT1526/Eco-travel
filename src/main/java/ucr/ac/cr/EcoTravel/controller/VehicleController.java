package ucr.ac.cr.EcoTravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.EcoTravel.model.Vehicle;
import ucr.ac.cr.EcoTravel.service.VehicleService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add")
    public ResponseEntity<?> saveVehicle(@Validated @RequestBody Vehicle vehicle, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Vehicle saved = this.vehicleService.saveVehicle(vehicle);
            if (saved == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("VEHICLE " + vehicle.getId() + " ALREADY EXISTS");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.vehicleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) { // Long
        Vehicle vehicle = this.vehicleService.findById(id);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("VEHICLE NOT FOUND");
        }
        return ResponseEntity.ok(vehicle);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) { // Long
        Vehicle updated = this.vehicleService.editVehicle(id, vehicle);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("VEHICLE NOT FOUND");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        Vehicle vehicle = this.vehicleService.findById(id);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "VEHICLE NOT FOUND"));
        }
        this.vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(Map.of("message", "VEHICLE DELETED"));
    }
}