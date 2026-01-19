package com.backend.domain.post.fileUpload.service;

import com.backend.domain.post.fileUpload.entity.GenFile;
import com.backend.domain.post.fileUpload.repository.GenFileRepository;
import com.backend.domain.post.post.entity.Post;
import com.backend.global.app.AppConfig;
import com.backend.global.dto.RsData;
import com.backend.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenFileService {
  private final GenFileRepository genFileRepository;

  private String getCurrentDirName(String relTypeCode) {
    return relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
  }

  public RsData<Map<String, GenFile>> saveFiles(Post post, Map<String, MultipartFile> fileMap) {
    String relTypeCode = "article";
    long relId = post.getId();

    Map<String, GenFile> genfileIds = new HashMap<>();

    for (String inputName : fileMap.keySet()) {
      MultipartFile multipartFile = fileMap.get(inputName);

      if(multipartFile.isEmpty()) continue;

      String[] inputNameBits = inputName.split("__"); // common__contentImg__1" : ["common", "contentImg", "1"]

      String typeCode = inputNameBits[0]; // 파일 타입
      String type2Code = inputNameBits[1]; // 세부 타입(write.html에 있는 name)
      String originFileName = multipartFile.getOriginalFilename(); // 원본 파일명
      String fileExt = Util.file.getExt(originFileName); // 파일 확장자
      String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt); // 확장 타입
      String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt);; // 확장 타입
      int fileNo = Integer.parseInt(inputNameBits[2]); // 파일 번호
      int fileSize = (int)multipartFile.getSize(); // 파일 크기
      String fileDir = getCurrentDirName(relTypeCode);

      GenFile genFile = GenFile.builder()
        .relTypeCode(relTypeCode)
        .relId(relId)
        .typeCode(typeCode)
        .type2Code(type2Code)
        .fileExtTypeCode(fileExtTypeCode)
        .fileExtType2Code(fileExtType2Code)
        .fileSize(fileSize)
        .fileNo(fileNo)
        .fileExt(fileExt)
        .fileDir(fileDir)
        .originFileName(originFileName)
        .build();

      genFile = save(genFile);

      String filePath = AppConfig.getGenFileDirPath() + "/" + fileDir + "/" + genFile.getFileName();

      File file = new File(filePath);
      file.getParentFile().mkdirs();

      try {
        multipartFile.transferTo(file); // 파일 저장
      } catch (Exception e) {
        e.printStackTrace();
      }

      genfileIds.put(inputName, genFile);
    }

    return new RsData<>("S-1", "파일을 업로드했습니다.", genfileIds);
  }

  private GenFile save(GenFile genFile) {
    Optional<GenFile> existGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(genFile.getRelTypeCode(), genFile.getRelId(), genFile.getTypeCode(), genFile.getType2Code(), genFile.getFileNo());

    if(existGenFile.isPresent()) {
      GenFile oldGenFile = existGenFile.get();
      deleteFileFromStorage(oldGenFile);

      oldGenFile.merge(genFile);

      genFileRepository.save(oldGenFile);
      return oldGenFile;
    }

    genFileRepository.save(genFile);

    return genFile;
  }

  private void deleteFileFromStorage(GenFile oldGenFile) {
    new File(oldGenFile.getFilePath()).delete(); // 기존에 존재하는 이미지 파일 삭제
  }

  public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
    String fileDir = getCurrentDirName(relTypeCode);

    String downFilePath = Util.file.downloadImg(url, AppConfig.getGenFileDirPath() + "/" + fileDir + "/" + UUID.randomUUID());

    File dowonloadedFile = new File(downFilePath);

    String originFileName = dowonloadedFile.getName();
    String fileExt = Util.file.getExt(originFileName); // 파일 확장자
    String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt); // 확장 타입
    String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt); // 확장 타입
    int fileSize = 0;

    try {
      fileSize = (int) Files.size(Paths.get(downFilePath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    GenFile genFile = GenFile.builder()
      .relTypeCode(relTypeCode)
      .relId(relId)
      .typeCode(typeCode)
      .type2Code(type2Code)
      .fileExtTypeCode(fileExtTypeCode)
      .fileExtType2Code(fileExtType2Code)
      .fileSize(fileSize)
      .fileNo(fileNo)
      .fileExt(fileExt)
      .fileDir(fileDir)
      .originFileName(originFileName)
      .build();

    genFile = save(genFile);

    String filePath = AppConfig.getGenFileDirPath() + "/" + fileDir + "/" + genFile.getFileName();

    File file = new File(filePath);

    file.getParentFile().mkdirs();

    dowonloadedFile.renameTo(file);
  }

  public Map<String, GenFile> getRelGenFileMap(Post post) {
    List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc("post", post.getId());

    // genFile 의 정렬상태를 LinkedHashMap 를 이용하여 순서를 보장하도록
    return genFiles
      .stream()
      .collect(Collectors.toMap(
        genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
        genFile -> genFile,
        (genFile1, genFile2) -> genFile1,
        LinkedHashMap::new
      ));
  }

  public void deleteFiles(Post post, Map<String, String> params) {
    List<String> deleteFilesArgs = params.keySet()
      .stream()
      .filter(key -> key.startsWith("delete___"))
      .map(key -> key.replace("delete___", ""))
      .collect(Collectors.toList());

    log.debug("deleteFilesArgs : {}", deleteFilesArgs);

    deleteFiles(post, deleteFilesArgs);
  }

  public void deleteFiles(Post post, List<String> params) {
    String relTypeCode = "article";
    Long relId = post.getId();

    params.stream()
      .forEach(key -> {
        String[] keyBits = key.split("__");

        String typeCode = keyBits[0];
        String type2Code = keyBits[1];
        int fileNo = Integer.parseInt(keyBits[2]);

        Optional<GenFile> opGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);

        if(opGenFile.isPresent()) {
          delete(opGenFile.get());
        }
      });
  }

  private void delete(GenFile genFile) {
    deleteFileFromStorage(genFile); // 로컬에서 이미지 제거
    genFileRepository.delete(genFile); // DB에서 이미지 제거
  }

  public void deleteAllFilesByPost(Post post) {
    String relTypeCode = "post";
    Long relId = post.getId();

    List<GenFile> genFiles =
      genFileRepository.findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc(
        relTypeCode, relId
      );

    log.debug("genFiles : {}", genFiles);

    for (GenFile genFile : genFiles) {
      delete(genFile);
    }
  }

  public List<GenFile> getRelFiles(String relTypeCode,
                                   Long relId,
                                   String typeCode,
                                   String type2Code) {
    return genFileRepository
      .findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoAsc(
        relTypeCode,
        relId,
        typeCode,
        type2Code
      );
  }

  public String getUrl(GenFile genFile) {
    // 예시: /gen/{fileDir}/{fileName} 형태로 정적 리소스 매핑했다고 가정
    return "/gen/" + genFile.getFileDir() + "/" + genFile.getFileName();
  }
}
