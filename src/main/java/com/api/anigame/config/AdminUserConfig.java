package com.api.anigame.config;


import com.api.anigame.exception.ResourceNotFoundException;
import com.api.anigame.persistence.entity.RoleEntity;
import com.api.anigame.persistence.entity.UserEntity;
import com.api.anigame.persistence.repository.RoleRepository;
import com.api.anigame.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                    user.setRoles(Set.of(roleAdmin.get()));
                    userRepository.save(user);
                }
        );
    }
}
