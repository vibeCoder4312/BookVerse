package com.bookverse.repository;

import com.bookverse.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Extending JpaRepository<User, Long> instantly gives us methods like
// save(), findById(), findAll(), deleteById() - no implementation needed,
// Spring Data JPA generates the actual SQL behind the scenes at runtime.
//
// Long here is the type of User's @Id field.
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA reads this method NAME and auto-generates the
    // correct SQL query from it - "findByEmail" becomes
    // "SELECT * FROM users WHERE email = ?". No SQL written by us at all.
    Optional<User> findByEmail(String email);

    // Used by the Admin User Management page's search box - matches
    // partial, case-insensitive text against either name OR email.
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable
    );
}
