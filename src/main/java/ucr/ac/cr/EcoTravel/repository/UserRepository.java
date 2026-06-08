package ucr.ac.cr.EcoTravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.EcoTravel.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // Long

    List<User> findByName(String name);

    User findByEmailAndPassword(String email, String password);

    List<User> findAllByOrderByNameAsc();

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> searchRol(@Param("role") String role);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    User verifyCredentials(@Param("email") String email,
                           @Param("password") String password);
}