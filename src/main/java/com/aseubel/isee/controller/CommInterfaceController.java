package com.aseubel.isee.controller;



import com.aseubel.isee.common.Response;
import com.aseubel.isee.pojo.entity.CommInterface;
import com.aseubel.isee.service.ICommInterfaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地区接口
 * @author Aseubel
 * @date 2025/4/18 上午12:32
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/commInterface")
@RequiredArgsConstructor
public class CommInterfaceController {

    @Autowired
    private ICommInterfaceService commInterfaceService;

    /**
     * 获取所有地区
     */
    @GetMapping("")
    public Response<List<CommInterface>> getCommInterfaces() {
        List<CommInterface> list = commInterfaceService.list();
        return Response.success(list);
    }
}
