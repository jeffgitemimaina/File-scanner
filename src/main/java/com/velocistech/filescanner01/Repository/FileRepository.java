package com.velocistech.filescanner01.Repository;

import com.velocistech.filescanner01.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface FileRepository extends JpaRepository<File, Long> {

    boolean existsByTaskid(String TaskId);

     List<File> findByTaskid(String TaskId);
     List<File> findByEmail(String email);
     @Query("select max(f.id) from File f")
     Long findMaxId();

}