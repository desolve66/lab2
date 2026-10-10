package com.example.newsroom.service;

import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.ArticleStatus;
import com.example.newsroom.model.EditionIssue;
import com.example.newsroom.model.IssueStatus;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditionIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditionIssueService {
    private final EditionIssueRepository issueRepository;
    private final ArticleRepository articleRepository;

    public List<EditionIssue> findAll() {
        return issueRepository.findAll();
    }

    public EditionIssue findById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));
    }

    @Transactional
    public EditionIssue create(EditionIssue issue, List<Long> articleIds) {
        issue.setIssueDate(LocalDate.now()); // серверная дата
        if (articleIds != null && !articleIds.isEmpty()) {
            List<Article> articles = articleRepository.findAllById(articleIds);
            issue.setArticles(articles);
        }
        return issueRepository.save(issue);
    }

    @Transactional
    public EditionIssue patch(Long id, EditionIssue patch, List<Long> articleIds) {
        EditionIssue existing = findById(id);
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());

            // БИЗНЕС-ЛОГИКА: при публикации выпуска все статьи в нем становятся PUBLISHED
            if (patch.getStatus() == IssueStatus.PUBLISHED) {
                for (Article a : existing.getArticles()) {
                    a.setStatus(ArticleStatus.PUBLISHED);
                    articleRepository.save(a);
                }
            }
        }
        if (articleIds != null) {
            List<Article> articles = articleRepository.findAllById(articleIds);
            existing.setArticles(articles);
        }
        return issueRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        EditionIssue issue = findById(id);
        issue.getArticles().clear();
        issueRepository.delete(issue);
    }
}