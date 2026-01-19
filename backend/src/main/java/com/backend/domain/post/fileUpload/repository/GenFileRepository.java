package com.backend.domain.post.fileUpload.repository;

import com.backend.domain.post.fileUpload.entity.GenFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenFileRepository extends JpaRepository<GenFile, Long> {
  List<GenFile> findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc(
    String relTypeCode,
    Long relId
  );

  List<GenFile> findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoAsc(
    String relTypeCode,
    Long relId,
    String typeCode,
    String type2Code
  );
}