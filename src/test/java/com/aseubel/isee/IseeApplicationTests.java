package com.aseubel.isee;

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
		try {
			// 1. 指定.exe文件路径（绝对路径更可靠）
			String exePath = "D:\\BaiduNetdiskDownload\\yolov8\\run_headless\\run_headless.exe";

			// 2. 创建ProcessBuilder实例
			ProcessBuilder pb = new ProcessBuilder(exePath);

			// 3. 设置工作目录（可选，默认项目根目录）
//			pb.directory(new File("C:/parent/folder/of/exe"));

			// 4. 启动进程
			Process process = pb.start();

			// 5. 捕获输出流（防止进程阻塞）
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream())
			);
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("[EXE Output] " + line);
			}

			// 6. 等待程序执行完成
			int exitCode = process.waitFor();
			System.out.println("Exe执行完成，退出码: " + exitCode);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
