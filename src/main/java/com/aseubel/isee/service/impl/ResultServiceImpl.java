package com.aseubel.isee.service.impl;



import com.aseubel.isee.dao.ResultMapper;
import com.aseubel.isee.pojo.entity.Result;
import com.aseubel.isee.service.ResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Aseubel
 * @date 2025/4/16 下午9:09
 */
@Service
@Slf4j
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result> implements ResultService {
}
