package com.ratingservice.controller;

import com.ratingservice.dto.RatingPatchRequest;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

  private final RatingService ratingService;
  private static final String ID = "/{id}";

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RatingResponse createRating(@RequestBody @Valid RatingRequest ratingRequest) {
    return ratingService.createRating(ratingRequest);
  }

  @GetMapping(ID)
  public RatingResponse getRating(@PathVariable Long id) {
    return ratingService.getRatingById(id);
  }

  @PatchMapping(ID)
  public RatingResponse patchRating(
          @PathVariable Long id, @RequestBody @Valid RatingPatchRequest ratingRequest) {
    return ratingService.patchRating(id, ratingRequest);
  }

  @DeleteMapping(ID)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRating(@PathVariable Long id) {
     ratingService.deleteRating(id);
  }
}