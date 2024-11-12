package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.domain.response.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;

import java.io.IOException;
import java.lang.module.FindException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/files")
public class FileController {

    private final FileService fileService;

    @Value("${upload-file.base-uri}")
    private String baseUri;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping()
    public ResponseEntity<ResUploadFileDTO> Upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, FindException {

        if(file == null || file.isEmpty()) {
            throw new FindException("file is not empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if(!isValid) {
            throw new FindException("file only allow pdf, jpg, jpeg, png, doc, docx");
        }


        // Create a folder
        this.fileService.createUploadFolder(baseUri + folder);

        // Save file into folder
        String uploadFile = this.fileService.store(file, folder, baseUri);

        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(resUploadFileDTO);
    }
}
