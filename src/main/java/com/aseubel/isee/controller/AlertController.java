package com.aseubel.isee.controller;

import com.aseubel.isee.common.Response;
import com.aseubel.isee.common.annotation.RequirePermission;
import com.aseubel.isee.pojo.entity.AlertNotification;
import com.aseubel.isee.service.IAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {

    private final IAlertService alertService;

    @GetMapping("/list")
    @RequirePermission(minAdminLevel = 0, checkArea = true)
    public Response<List<AlertNotification>> getAlerts(@RequestParam(required = false) Integer areaId) {
        return Response.success(alertService.getAlertsByArea(areaId));
    }

    @PutMapping("/{alertId}/status")
    @RequirePermission(minAdminLevel = 1, checkArea = true)
    public Response<Void> updateAlertStatus(
            @PathVariable Integer alertId,
            @RequestParam Integer status) {
        alertService.updateAlertStatus(alertId, status);
        return Response.success();
    }
}