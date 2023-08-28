package com.example.repositories;

import com.example.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<PostEntity, Long> {

    Page<PostEntity> findByIdUsuario(Long idUsuario, Pageable pageable);

    Page<PostEntity> findAll(Pageable pageable);
}
