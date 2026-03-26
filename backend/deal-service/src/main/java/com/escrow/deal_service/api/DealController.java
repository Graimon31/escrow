package com.escrow.deal_service.api;

import com.escrow.deal_service.api.DealDtos.*;
import com.escrow.deal_service.service.DealService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {
  private final DealService service;
  public DealController(DealService service) { this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DealResponse create(@RequestBody CreateDealRequest req) { return service.create(req); }

  @GetMapping
  public List<DealResponse> all() { return service.all(); }

  @PostMapping("/{id}/transition")
  public DealResponse transition(@PathVariable UUID id, @RequestBody TransitionRequest req) { return service.transition(id, req.state()); }
}
