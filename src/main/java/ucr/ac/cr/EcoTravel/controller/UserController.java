package ucr.ac.cr.EcoTravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.EcoTravel.model.DTO.LoginDTO;
import ucr.ac.cr.EcoTravel.model.DTO.UserDTO;
import ucr.ac.cr.EcoTravel.model.User;
import ucr.ac.cr.EcoTravel.service.UserService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(this.userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) { // Long
        UserDTO dto = this.userService.findByIDUser(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER NOT FOUND");
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@Validated @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            UserDTO dto = this.userService.saveUser(user);
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("USER " + user.getId() + " HAS ALREADY REGISTER");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody User user) { // Long
        UserDTO dto = this.userService.editUser(id, user);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER NOT FOUND");
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) { // Long
        UserDTO dto = this.userService.findByIDUser(id);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER NOT FOUND");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok("USER DELETED");
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {
        if (this.userService.findByName(name).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NAME DOES NOT EXIST");
        }
        return ResponseEntity.ok(this.userService.findByName(name));
    }

    @GetMapping("/order")
    public ResponseEntity<?> findAllByOrderByNameAsc() {
        return ResponseEntity.ok(this.userService.findAllByOrderByNameAsc());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> searchRole(@PathVariable String role) {
        return ResponseEntity.ok(this.userService.searchRole(role));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dtoLogin) {
        User user = this.userService.login(dtoLogin.getEmail(), dtoLogin.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INCORRECT CREDENTIALS");
        }
        return ResponseEntity.ok("WELCOME " + user.getName());
    }
}