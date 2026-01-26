package com.ratingservice.controller;

import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

  private final RatingService ratingService;

  @PostMapping
  public ResponseEntity<RatingResponse> createRating(@RequestBody @Valid RatingRequest ratingRequest) {
    RatingResponse ratingResponse = ratingService.createRating(ratingRequest);
    return new ResponseEntity<>(ratingResponse, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RatingResponse> getRating(@PathVariable Long id) {
    RatingResponse ratingResponse = ratingService.getRatingById(id);
    return new ResponseEntity<>(ratingResponse, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RatingResponse> updateRating(
          @PathVariable Long id, @RequestBody @Valid RatingRequest ratingRequest) {
    RatingResponse ratingResponse = ratingService.updateRating(id, ratingRequest);
    return new ResponseEntity<>(ratingResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<RatingResponse> deleteRating(@PathVariable Long id) {
    ratingService.deleteRating(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}