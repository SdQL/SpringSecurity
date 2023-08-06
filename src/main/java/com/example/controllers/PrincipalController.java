package com.example.controllers;

import com.example.controllers.request.CreateUserDTO;
import com.example.controllers.request.UpdateUserDTO;
import com.example.models.ERole;
import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUsuarios")
    public ResponseEntity<List<UserEntity>> getUsuarios(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<UserEntity> pageResult = userRepository.findAll(pageable);

        List<UserEntity> usuarios = pageResult.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Last-Page", String.valueOf(pageResult.getTotalPages())); // Agregamos el encabezado personalizado

        return ResponseEntity.ok().headers(headers).body(usuarios);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

        if(userEntityOptional.isPresent()){
            return ResponseEntity.ok(userEntityOptional.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findEmail/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        if(userEntityOptional.isPresent()){
            return ResponseEntity.ok(userEntityOptional.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(Long.parseLong(id));

        if(userEntityOptional.isPresent()){
            return ResponseEntity.ok(userEntityOptional.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {

        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                        .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .email(createUserDTO.getEmail())
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .nombres(createUserDTO.getNombres())
                .apellidos(createUserDTO.getApellidos())
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            userEntity.setEmail(updateUserDTO.getEmail());
            userEntity.setUsername(updateUserDTO.getUsername());
            userEntity.setNombres(updateUserDTO.getNombres());
            userEntity.setApellidos(updateUserDTO.getApellidos());

            userRepository.save(userEntity);

            return ResponseEntity.ok(userEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "El usuario con id " + id + " ha sido eliminado";
    }
}