package com.anigame.api.config;


import com.anigame.api.persistence.entity.UserEntity;
import com.anigame.api.persistence.entity.enumerate.Gender;
import com.anigame.api.exception.ResourceNotFoundException;
import com.anigame.api.persistence.entity.RoleEntity;
import com.anigame.api.persistence.repository.RoleRepository;
import com.anigame.api.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(RoleEntity.Values.ADMIN.name());
        var userAdmin = userRepository.findByUsername("admin");

        if (roleAdmin.isEmpty()) {
            throw new ResourceNotFoundException("Role not found.");
        }

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin already exists.");
                },
                () -> {
                    var user = new UserEntity();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setFirstName("admin");
                    user.setLastName("admin");
                    user.setCpf("99999999999");
                    user.setEmail("admin@email.com");
                    user.setGender(Gender.MALE);
                    user.setDateOfBirth(LocalDate.of(1,1,1));
                    user.setRoles(Set.of(roleAdmin.get()));
                    userRepository.save(user);
                }
        );
    }
}
