package com.ecommerce.project.service.impl;

import com.ecommerce.project.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //getting original file name
        String originalFileName= file.getOriginalFilename();

        //generating unique file name using UUID
        String randomId = UUID.randomUUID().toString();
//        mat.jpg-->.jpg-->1234.jpg
        String newFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));

        //creating new file path
        String newFilePath = path + File.separator +newFileName;

        //check if folder exists and create
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        //Upload in server
        Files.copy(file.getInputStream(), Paths.get(newFilePath));
        //return file name
        return newFileName;
    }
}
