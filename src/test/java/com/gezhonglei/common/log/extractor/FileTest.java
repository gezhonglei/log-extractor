package com.gezhonglei.common.log.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;

import com.gezhonglei.common.util.FileUtil;

public class FileTest {
	// 以File作为输入源File->FileReader
	public static void test1(File source) throws Exception {
		FileReader m = new FileReader(source);
		BufferedReader reader = new BufferedReader(m);

		while (true) {
			String nextline = reader.readLine();
			if (nextline == null)
				break;
			System.out.println("got:" + nextline);
		}
		reader.close();

	}

	// 以InputStream作为输入源 InputStream->InputStreamReader
	public static void test2(InputStream source) throws Exception {
		InputStreamReader m = new InputStreamReader(source, "GBK");
		BufferedReader reader = new BufferedReader(m);

		while (true) {
			String nextline = reader.readLine();
			if (nextline == null)
				break;
			System.out.println("got:" + nextline);
		}
		reader.close();
	}

	public static void test3() throws Exception {
		InputStreamReader m = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(m);
		while (true) {
			System.out.print(">");
			String nextline = reader.readLine();
			if (nextline == null)
				break;
			if ("exit".equals(nextline)) {
				System.out.println("Good Bye");
				break;
			}
			// 处理用户输入
			System.out.println("handle command:" + nextline);
		}
		reader.close();
	}
	
	@Test
	public void test001() throws IOException {
		String url = FileTest.class.getClassLoader().getResource("").getFile();
		String location = new File(url).getCanonicalPath();
		String glob = "glob:**/*.log";
		
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
		Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>()
		{
 
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
			{
				//如果匹配上了指定的路径,处理查询的数据
				if (pathMatcher.matches(path))
				{
					System.out.println(path);
				}
				return FileVisitResult.CONTINUE;
			}
 
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
			{
				return FileVisitResult.CONTINUE;
			}
		});
	}
	

	@Test
	public void testFilePath() throws Exception {
		String file = ClassLoader.getSystemResource("config.json").getFile();
		System.out.println(file);
		System.out.println(new File(file).exists());
		
		String content = FileUtil.getAllContent(file, "UTF-8");
		System.out.println(content);
		
		//ParserConfig config = JsonUtil.fromJson(content, ParserConfig.class);
	}
	
	@Test 
	public void testJson() {
		
	}
}
