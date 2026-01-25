package com.driverservice.controller;

import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Driver;
import com.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

  private final DriverService driverService;

  @PostMapping
  public ResponseEntity<DriverResponse> createDriver(@RequestBody DriverRequest driver) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(driverService.createDriver(driver));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DriverResponse> getDriver(@PathVariable Long id) {
    return ResponseEntity.ok(driverService.getDriverById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Driver> deleteDriver(@PathVariable Long id) throws Exception {
    driverService.deleteDriver(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<DriverResponse> updateDriver(@PathVariable Long id, @RequestBody DriverRequest driver) throws Exception {
    return ResponseEntity.ok(driverService.updateDriver(id,driver));
  }
}
