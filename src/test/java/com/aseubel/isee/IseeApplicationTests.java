package com.aseubel.isee;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class IseeApplicationTests {

//	@Test
	void testModel() {
		HttpUtil.createPost("http://127.0.0.1:8000/predict").timeout(3000).execute();
	}

}
