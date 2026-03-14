package com.driverservice.controller;

import com.driverservice.dto.DriverPatchRequest;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

  private static final String ID = "/{id}";
  private final DriverService driverService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DriverResponse createDriver(@Valid @RequestBody DriverRequest driver) {
    return driverService.createDriver(driver);
  }

  @GetMapping(ID)
  public DriverResponse getDriver(@PathVariable Long id) {
    return driverService.getDriverById(id);
  }

  @DeleteMapping(ID)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDriver(@PathVariable Long id) {
    driverService.deleteDriver(id);
  }

  @PatchMapping(ID)
  public DriverResponse patchDriver(@PathVariable Long id, @Valid @RequestBody DriverPatchRequest driver) {
    return driverService.patchDriver(id, driver);
  }
}