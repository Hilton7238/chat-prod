package com.h.userservice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class UploadFile {
    public void upload(String name, String path, MultipartFile file) throws IOException {
        File temp = new File(path);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        File localFile = new File(path + name);
        file.transferTo(localFile); //把上传的文件保存至本地
    }
}
