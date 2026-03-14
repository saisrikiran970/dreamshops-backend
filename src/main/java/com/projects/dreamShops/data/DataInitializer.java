package com.projects.dreamShops.data;

import com.projects.dreamShops.model.Role;
import com.projects.dreamShops.model.User;
import com.projects.dreamShops.repository.RoleRepository;
import com.projects.dreamShops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN","ROLE_USER");
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultUserIfNotExists();
        createDefaultAdminIfNotExists();
    }

    private void createDefaultUserIfNotExists(){
        for(int i=1;i<=5;i++){
            String email = "user" + i + "@gmail.com";
            if(userRepository.existsByEmail(email)){
                continue;
            }
            Role userRole = roleRepository.findByName("ROLE_USER");
            User user = new User();
            user.setFirstName("The User");
            user.setLastName("User" + i);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println("Default user "+ i + " created successfully..!");
        }
    }

    private void createDefaultAdminIfNotExists(){
        for(int i=1;i<=5;i++){
            String email = "admin" + i + "@gmail.com";
            if(userRepository.existsByEmail(email)){
                continue;
            }
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin" + i);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("12345"));
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
            System.out.println("Default admin user "+ i + " created successfully..!");
        }
    }

    private void createDefaultRoleIfNotExists(Set<String> roles){
        roles.stream()
                .filter(role -> roleRepository.findByName(role) == null)
                .map(Role::new).forEach(roleRepository::save);
    }
}
