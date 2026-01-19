package com.backend.global.util;

import org.apache.tika.Tika;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Util {
  public static class url {
    public static String encode(String str) {
      try {
        return URLEncoder.encode(str, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        return str;
      }
    }
  }

  public static class date {
    public static String getCurrentDateFormatted(String pattern) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
      return LocalDateTime.now().format(formatter);
    }
  }

  public static class file {

    public static String getExt(String fileName) {
      return Optional.ofNullable(fileName)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(f.lastIndexOf(".") + 1))
        .orElse("");
    }

    /**
     * 이미지를 다운로드해서 디스크에 저장하고, Tika로 MIME 타입(확장자)을 판별한 뒤
     * 실제 확장자를 붙인 최종 파일 경로를 반환하는 메서드.
     *
     * @param url      다운로드할 이미지 URL
     * @param filePath 확장자를 제외한 임시 저장 경로 (예: c:/spring-temp/app1/member/2025_08_21/asdasdas)
     * @return 실제 확장자를 붙인 최종 경로 (예: c:/spring-temp/app1/member/2025_08_21/asdasdas.jpg)
     */
    public static String downloadImg(String url, String filePath) {
      try {
        // 1) 디렉터리 생성 (parent 가 있을 때만)
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
          Files.createDirectories(parent);
        }

        // 2) 이미지 다운로드
        RestTemplate restTemplate = new RestTemplate();
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

        if (imageBytes == null || imageBytes.length == 0) {
          throw new IllegalStateException("이미지 데이터를 받지 못했습니다. url=" + url);
        }

        // 3) 임시 파일로 저장 (확장자 없이)
        Files.write(path, imageBytes);

        // 4) Tika로 MIME 타입 판별
        String mimeType;
        try {
          mimeType = new Tika().detect(path.toFile());
        } catch (IOException e) {
          throw new RuntimeException("Tika MIME 타입 판별 실패", e);
        }

        if (mimeType == null) {
          mimeType = "application/octet-stream";
        }

        // 5) MIME 타입에서 확장자 추출
        String ext = mimeType.replaceAll("^image/", "");
        ext = ext.replaceAll("jpeg", "jpg"); // jpeg → jpg 통일

        // 혹시라도 image/* 가 아니면 기본값
        if (!mimeType.startsWith("image/") || ext.isBlank()) {
          ext = "bin";
        }

        // 6) 최종 파일명으로 rename
        Path newPath = Paths.get(filePath + "." + ext);
        Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);

        return newPath.toString();

      } catch (Exception e) {
        // 어디에서 오류가 나는지 확인할 수 있도록 통으로 감싸서 던짐
        throw new RuntimeException("downloadImg 수행 중 오류가 발생했습니다. url=" + url + ", filePath=" + filePath, e);
      }
    }

    public static String getFileExtTypeCodeFromFileExt(String ext) {
      switch (ext) {
        case "jpeg":
        case "jpg":
        case "gif":
        case "png":
          return "img";
        case "mp4":
        case "avi":
        case "mov":
          return "video";
        case "mp3":
          return "audio";
      }
      return "etc";
    }

    public static String getFileExtType2CodeFromFileExt(String ext) {
      switch (ext) {
        case "jpeg":
        case "jpg":
          return "jpg";
        case "gif":
          return ext;
        case "png":
          return ext;
        case "mp4":
          return ext;
        case "mov":
          return ext;
        case "avi":
          return ext;
        case "mp3":
          return ext;
      }
      return "etc";
    }
  }
}
