package com.example.controllers;

import com.example.controllers.requestPublicaciones.CreatePublicacionDTO;
import com.example.controllers.requestPublicaciones.UpdatePublicacionDTO;
import com.example.models.UserEntity;
import com.example.repositories.PostRepository;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.models.PostEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class PublicacionController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/getPublicacionesByIdUsuario/{idUsuario}")
    public ResponseEntity<List<PostEntity>> getPublicacionesByIdUsuario(@PathVariable Long idUsuario,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "6") int pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<PostEntity> pageResult = postRepository.findByIdUsuario(idUsuario, pageable);

        List<PostEntity> publicaciones = pageResult.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Last-Page", String.valueOf(pageResult.getTotalPages())); // Agregamos el encabezado personalizado

        return ResponseEntity.ok().headers(headers).body(publicaciones);
    }

    @GetMapping("/getPublicaciones")
    public ResponseEntity<List<PostEntity>> getPublicaciones(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<PostEntity> pageResult = postRepository.findAll(pageable);

        List<PostEntity> publicaciones = pageResult.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Last-Page", String.valueOf(pageResult.getTotalPages())); // Agregamos el encabezado personalizado

        return ResponseEntity.ok().headers(headers).body(publicaciones);
    }

    @PostMapping("/createPublicacion/{idUsuario}")
    public ResponseEntity<?> createPublicacion(@PathVariable Long idUsuario, @RequestBody CreatePublicacionDTO createPublicacionDTO){

            PostEntity postEntity = PostEntity.builder()
                    .fecha(createPublicacionDTO.getFecha())
                    .header(createPublicacionDTO.getHeader())
                    .ubicacion(createPublicacionDTO.getUbicacion())
                    .descripcion(createPublicacionDTO.getDescripcion())
                    .idUsuario(idUsuario)
                    .build();

            postRepository.save(postEntity);

            return ResponseEntity.ok(postEntity);
    }

    @PutMapping("/updatePublicacion/{id}")
    public ResponseEntity<?> updatePublicacion(@PathVariable Long id, @RequestBody UpdatePublicacionDTO updatePublicacionDTO) {

        Optional<PostEntity> postEntityOptional = postRepository.findById(id);

        if (postEntityOptional.isPresent()) {
            PostEntity postEntity = postEntityOptional.get();
            postEntity.setFecha(updatePublicacionDTO.getFecha());
            postEntity.setHeader(updatePublicacionDTO.getHeader());
            postEntity.setUbicacion(updatePublicacionDTO.getUbicacion());
            postEntity.setDescripcion(updatePublicacionDTO.getDescripcion());
            postRepository.save(postEntity);
            return ResponseEntity.ok(postEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePublicacion/{id}")
    public ResponseEntity<?> deletePublicacion(@PathVariable Long id) {

        Optional<PostEntity> postEntityOptional = postRepository.findById(id);

        if (postEntityOptional.isPresent()) {
            postRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
