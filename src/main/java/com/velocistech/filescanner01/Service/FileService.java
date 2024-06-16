package com.velocistech.filescanner01.Service;

import com.velocistech.filescanner01.Entity.File;
import com.velocistech.filescanner01.Repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service()
@AllArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    @Transactional
    public String createFile(File file){
        System.out.println("..........................................."+file);
        try {
            if (!fileRepository.existsByTaskid(file.getTaskid())){
                file.setId(null == fileRepository.findMaxId()? 0 : fileRepository.findMaxId() + 1);
                fileRepository.save(file);
                System.out.println("File status record created successfully.");
                System.out.println(this.toString());
                return "alluploads";
            }else {
                System.out.println("File status already exists in the database.");
                return "alluploads";
            }
        }catch (Exception e){
            throw e;
        }
    }

    public List<File> readFiles(String email){

        return fileRepository.findByEmail(email);
    }
    @Transactional
    public String updateFile(File file){
        System.out.println("..........................................."+file);
        if (fileRepository.existsByTaskid(file.getTaskid())){
            try {
                List<File> files = fileRepository.findByTaskid(file.getTaskid());
                files.stream().forEach(f -> {
                    File fileToBeUpdated = fileRepository.findById(f.getId()).get();
                    fileToBeUpdated.setQuery_status(file.getQuery_status());
                    fileToBeUpdated.setClamav_results(file.getClamav_results());
                    fileToBeUpdated.setUnpack_results(file.getUnpack_results());
                    fileToBeUpdated.setStatic_results(file.getStatic_results());

                    fileRepository.save(fileToBeUpdated);
                });
                System.out.println("file status record updated." +fileRepository+"file"+file);
                return "uploader";
            }catch (Exception e){
                throw e;
            }
        }else {
            System.out.println("File task id does not exists in the database.");
            return "uploader";
        }
    }

}
