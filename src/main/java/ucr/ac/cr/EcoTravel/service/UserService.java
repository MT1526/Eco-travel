package ucr.ac.cr.EcoTravel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.EcoTravel.model.DTO.UserDTO;
import ucr.ac.cr.EcoTravel.model.User;
import ucr.ac.cr.EcoTravel.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO saveUser(User user) {
        Optional<User> opt = this.userRepository.findById(user.getId());
        if (opt.isPresent()) {
            return null;
        }

        String password = user.getPassword();
        String email = user.getEmail();

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (password == null || password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[@#$%^&+=!?.*_\\-].*") ||
                email == null || !Pattern.matches(emailRegex, email)) {

            throw new IllegalArgumentException(
                    "INVALID DATA: PASSWORD MUST HAVE 8 CHARACTERS, " +
                            "UPPERCASE, LOWERCASE, NUMBER AND SYMBOL. " +
                            "EMAIL MUST BE VALID (example: user@domain.com)"
            );
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        return this.convertUserDTO(this.userRepository.save(user));
    }

    public List<UserDTO> findAll() {
        return this.convertListDTO(this.userRepository.findAll());
    }

    public UserDTO findByIDUser(Long id) { // Long
        Optional<User> optional = this.userRepository.findById(id);
        return optional.map(this::convertUserDTO).orElse(null);
    }

    public void deleteUser(Long id) { // Long
        this.userRepository.deleteById(id);
    }

    public UserDTO editUser(Long id, User userEdit) { // Long
        Optional<User> userOp = this.userRepository.findById(id);
        if (userOp.isPresent()) {
            User user = userOp.get();
            user.setName(userEdit.getName());
            user.setEmail(userEdit.getEmail());
            user.setPassword(userEdit.getPassword());
            user.setRole(userEdit.getRole());
            return this.convertUserDTO(this.userRepository.save(user));
        }
        return null;
    }

    public List<UserDTO> findByName(String name) {
        return this.convertListDTO(this.userRepository.findByName(name));
    }

    public List<User> findAllByOrderByNameAsc() {
        return this.userRepository.findAllByOrderByNameAsc();
    }

    public List<User> searchRole(String role) {
        return this.userRepository.searchRol(role);
    }

    public User login(String email, String password) {
        return this.userRepository.verifyCredentials(email, password);
    }

    private UserDTO convertUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId()); // Long, pero UserDTO tiene Long? Debe cambiarse también
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }

    private List<UserDTO> convertListDTO(List<User> userList) {
        List<UserDTO> listDTO = new ArrayList<>();
        for (User user : userList) {
            listDTO.add(this.convertUserDTO(user));
        }
        return listDTO;
    }
}