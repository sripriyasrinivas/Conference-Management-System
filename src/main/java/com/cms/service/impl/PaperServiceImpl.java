package com.cms.service.impl;

import com.cms.model.Paper;
import com.cms.model.User;
import com.cms.repository.PaperRepository;
import com.cms.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public Paper submitPaper(Paper paper, MultipartFile file, User author) {
        paper.setAuthor(author);
        paper.setStatus(Paper.Status.SUBMITTED);

        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            paper.setFileName(file.getOriginalFilename());
            paper.setFilePath(fileName);
        }

        return paperRepository.save(paper);
    }

    @Override
    public Paper resubmitPaper(Long paperId, Paper updatedPaper, MultipartFile file) {
        Paper existing = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found: " + paperId));

        existing.setTitle(updatedPaper.getTitle());
        existing.setAbstractText(updatedPaper.getAbstractText());
        existing.setKeywords(updatedPaper.getKeywords());
        existing.setStatus(Paper.Status.REVISED);
        existing.setVersion(existing.getVersion() + 1);
        existing.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            existing.setFileName(file.getOriginalFilename());
            existing.setFilePath(fileName);
        }

        return paperRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paper> findById(Long id) {
        return paperRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paper> findByAuthor(User author) {
        return paperRepository.findByAuthorOrderBySubmittedAtDesc(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paper> findAll() {
        return paperRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paper> findByStatus(Paper.Status status) {
        return paperRepository.findByStatus(status);
    }

    @Override
    public Paper updateStatus(Long paperId, Paper.Status status, String comments) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found: " + paperId));
        paper.setStatus(status);
        if (comments != null && !comments.isBlank()) {
            paper.setRevisionComments(comments);
        }
        paper.setUpdatedAt(LocalDateTime.now());
        return paperRepository.save(paper);
    }

    @Override
    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(Paper.Status status) {
        return paperRepository.countByStatus(status);
    }

    private String storeFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = uploadPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
}
