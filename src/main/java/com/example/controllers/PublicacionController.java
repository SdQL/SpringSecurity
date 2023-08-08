package com.example.controllers;

import com.example.models.UserEntity;
import com.example.repositories.PostRepository;
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

    @GetMapping("/getPublicacionesByIdUsuario")
    public ResponseEntity<List<PostEntity>> getPublicacionesByIdUsuario(@RequestParam Long idUsuario) {

        Optional<List<PostEntity>> postEntityOptional = postRepository.findByIdUsuario(idUsuario);

        if (postEntityOptional.isPresent()) {
            return ResponseEntity.ok(postEntityOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> createPublicacion(@PathVariable Long idUsuario
                                                , @RequestParam String header
                                                , @RequestParam String descripcion
                                                , @RequestParam Date fecha) {

        PostEntity postEntity = PostEntity.builder()
                .idUsuario(idUsuario)
                .header(header)
                .descripcion(descripcion)
                .fecha(fecha)
                .build();

        postRepository.save(postEntity);

        return ResponseEntity.ok(postEntity);
    }

    @PutMapping("/updatePublicacion/{idPublicacion}")
    public ResponseEntity<?> updatePublicacion(@PathVariable Long idPublicacion
                                                , @RequestParam String header
                                                , @RequestParam String descripcion
                                                , @RequestParam Date fecha) {

        Optional<PostEntity> postEntityOptional = postRepository.findById(idPublicacion);

        if (postEntityOptional.isPresent()) {
            PostEntity postEntity = postEntityOptional.get();
            postEntity.setHeader(header);
            postEntity.setDescripcion(descripcion);
            postEntity.setFecha(fecha);
            postRepository.save(postEntity);
            return ResponseEntity.ok(postEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePublicacion/{idPublicacion}")
    public ResponseEntity<?> deletePublicacion(@PathVariable Long idPublicacion) {

        Optional<PostEntity> postEntityOptional = postRepository.findById(idPublicacion);

        if (postEntityOptional.isPresent()) {
            postRepository.deleteById(idPublicacion);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
