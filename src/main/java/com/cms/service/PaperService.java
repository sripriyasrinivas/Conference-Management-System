package com.cms.service;

import com.cms.model.Paper;
import com.cms.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface PaperService {
    Paper submitPaper(Paper paper, MultipartFile file, User author);
    Paper resubmitPaper(Long paperId, Paper updatedPaper, MultipartFile file);
    Optional<Paper> findById(Long id);
    List<Paper> findByAuthor(User author);
    List<Paper> findAll();
    List<Paper> findByStatus(Paper.Status status);
    Paper updateStatus(Long paperId, Paper.Status status, String comments);
    void deletePaper(Long id);
    long countByStatus(Paper.Status status);
}
