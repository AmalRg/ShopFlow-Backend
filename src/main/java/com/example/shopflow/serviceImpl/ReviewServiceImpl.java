package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.ReviewRequest;
import com.example.shopflow.dto.response.ReviewResponse;
import com.example.shopflow.entity.Product;
import com.example.shopflow.entity.Review;
import com.example.shopflow.entity.User;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.ReviewMapper;
import com.example.shopflow.repository.OrderRepository;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.repository.ReviewRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository  reviewRepository;
    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;
    private final ReviewMapper      reviewMapper;


    @Override
    public ReviewResponse create(String email, ReviewRequest req) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé"));

        if (!orderRepository.customerHasPurchasedProduct(
                customer.getId(), req.getProductId()))
            throw new BusinessException(
                    "Vous devez avoir acheté ce produit pour laisser un avis");

        if (reviewRepository.existsByCustomerIdAndProductId(
                customer.getId(), req.getProductId()))
            throw new BusinessException(
                    "Vous avez déjà laissé un avis sur ce produit");

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé"));

        Review review = new Review();
        review.setCustomer(customer);
        review.setProduct(product);
        review.setNote(req.getNote());
        review.setCommentaire(req.getCommentaire());
        review.setApprouve(false);

        return reviewMapper.toDto(reviewRepository.save(review));
    }
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getByProduct(Long productId) {
        return reviewRepository
                .findByProductIdAndApprouveTrue(productId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getAllPending() {
        return reviewRepository.findByApprouveFalse()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }
    @Override
    public ReviewResponse approve(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avis non trouvé : " + id));
        review.setApprouve(true);
        return reviewMapper.toDto(reviewRepository.save(review));
    }
    @Override
    public void delete(Long id) {
        if (!reviewRepository.existsById(id))
            throw new ResourceNotFoundException("Avis non trouvé : " + id);
        reviewRepository.deleteById(id);
    }
}