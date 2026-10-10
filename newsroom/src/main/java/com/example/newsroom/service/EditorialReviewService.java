package com.example.newsroom.service;

import com.example.newsroom.exception.NotFoundException;
import com.example.newsroom.model.Article;
import com.example.newsroom.model.ArticleStatus;
import com.example.newsroom.model.EditorialReview;
import com.example.newsroom.repository.ArticleRepository;
import com.example.newsroom.repository.EditorialReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorialReviewService {
    private final EditorialReviewRepository reviewRepository;
    private final ArticleRepository articleRepository;

    public List<EditorialReview> findAll() {
        return reviewRepository.findAll();
    }

    public EditorialReview findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review with id " + id + " not found"));
    }

    @Transactional
    public EditorialReview create(EditorialReview review, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("Article not found: " + articleId));
        review.setArticle(article);
        review.setReviewTime(LocalDateTime.now()); // серверное время

        // БИЗНЕС-ЛОГИКА: если одобрено, статья становится APPROVED
        if ("APPROVED".equalsIgnoreCase(review.getDecision())) {
            article.setStatus(ArticleStatus.APPROVED);
            articleRepository.save(article);
        }

        return reviewRepository.save(review);
    }

    @Transactional
    public EditorialReview patch(Long id, EditorialReview patch) {
        EditorialReview existing = findById(id);
        if (patch.getDecision() != null) existing.setDecision(patch.getDecision());
        if (patch.getComments() != null) existing.setComments(patch.getComments());
        existing.setReviewTime(LocalDateTime.now());
        return reviewRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        EditorialReview review = findById(id);
        reviewRepository.delete(review);
    }
}