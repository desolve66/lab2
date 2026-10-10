package com.example.newsroom.service;

import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditionIssueRepository;
import com.example.newsroom.repository.EditorialSectionRepository;
import com.example.newsroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final EditorialSectionRepository sectionRepository;
    private final EditionIssueRepository issueRepository;

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article with id " + id + " not found"));
    }

    @Transactional
    public Article create(Article article, Long sectionId, Long authorId) {
        if (sectionId != null) {
            article.setSection(sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section not found: " + sectionId)));
        }
        if (authorId != null) {
            article.setAuthor(userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("User not found: " + authorId)));
        }
        return articleRepository.save(article);
    }

    @Transactional
    public Article patch(Long id, Article patch, Long sectionId, Long authorId) {
        Article existing = findById(id);
        if (patch.getTitle() != null) existing.setTitle(patch.getTitle());
        if (patch.getText() != null) existing.setText(patch.getText());
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (sectionId != null) {
            existing.setSection(sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section not found: " + sectionId)));
        }
        if (authorId != null) {
            existing.setAuthor(userRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("User not found: " + authorId)));
        }
        return articleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Article article = findById(id);
        List<EditionIssue> issues = issueRepository.findAll();
        for (EditionIssue issue : issues) {
            if (issue.getArticles().remove(article)) {
                issueRepository.save(issue);
            }
        }
        articleRepository.delete(article);
    }
}