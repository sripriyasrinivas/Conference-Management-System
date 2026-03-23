package com.cms.service.impl;

import com.cms.model.Abstract;
import com.cms.model.User;
import com.cms.repository.AbstractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AbstractServiceImpl {

    private final AbstractRepository abstractRepository;

    public Abstract submit(Abstract abs, User author) {
        abs.setAuthor(author);
        abs.setStatus(Abstract.AbstractStatus.SUBMITTED);
        return abstractRepository.save(abs);
    }

    public Abstract review(Long id, Abstract.AbstractStatus status, String comments) {
        Abstract abs = abstractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abstract not found"));
        abs.setStatus(status);
        abs.setReviewComments(comments);
        abs.setUpdatedAt(LocalDateTime.now());
        return abstractRepository.save(abs);
    }

    public Abstract revise(Long id, Abstract updated) {
        Abstract abs = abstractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abstract not found"));
        abs.setTitle(updated.getTitle());
        abs.setContent(updated.getContent());
        abs.setKeywords(updated.getKeywords());
        abs.setVersion(abs.getVersion() + 1);
        abs.setStatus(Abstract.AbstractStatus.SUBMITTED);
        abs.setUpdatedAt(LocalDateTime.now());
        return abstractRepository.save(abs);
    }

    @Transactional(readOnly = true)
    public List<Abstract> findByAuthor(User author) {
        return abstractRepository.findByAuthor(author);
    }

    @Transactional(readOnly = true)
    public List<Abstract> findAll() {
        return abstractRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Abstract> findByStatus(Abstract.AbstractStatus status) {
        return abstractRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Optional<Abstract> findById(Long id) {
        return abstractRepository.findById(id);
    }
}
