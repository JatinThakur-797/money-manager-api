package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
private final DashboardService dashboardService;

@GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(){
    Map<String, Object> response = dashboardService.getDashboardData();
    return ResponseEntity.ok(response);
}
}
