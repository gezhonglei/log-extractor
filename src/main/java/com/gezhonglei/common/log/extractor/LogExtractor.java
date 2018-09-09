package com.gezhonglei.common.log.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.ParseResult;
import com.gezhonglei.common.log.extractor.entity.Result;

public class LogExtractor {

	private static Logger logger = LoggerFactory.getLogger(LogExtractor.class);
	
	int coreSize = Runtime.getRuntime().availableProcessors();
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, coreSize, 
			0,  TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
	private ExtracteConfig config;
	private List<ExtracteTask> tasks = new LinkedList<>();
	CountDownLatch countDown = null;
	
	public LogExtractor(ExtracteConfig config) {
		this.config = config;
	}
	
	public ParseResult parse() throws IOException {
		File file = new File(this.config.getPath());
		if(!file.exists()) {
			logger.info("not exist file:{}", file);
			throw new IOException("File or Directory does not exist");
		}
		if(file.isFile()){
			createTask(file);
		} else if(file.isDirectory()) {
			walkDirectory(file.getCanonicalPath(), this.config.getFilter());
		}
		return getResult();
	}
	
	private void createTask(File file) {
		if(checkNotInList(file)) {
			this.tasks.add(new ExtracteTask(file, config, this));
		}
	}

	private boolean checkNotInList(File file) {
		for (ExtracteTask task : tasks) {
			if(task.getFile().equals(file)) {
				return false;
			}
		}
		return true;
	}

	private void walkDirectory(String path, String pattern) throws IOException {
		String glob = "glob:**/" + pattern;
		
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
			{
				if (pathMatcher.matches(path))
				{
					createTask(path.toFile());
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

	private ParseResult getResult() {
		countDown = new CountDownLatch(tasks.size());
		for (ExtracteTask task : tasks) {
			executor.submit(task);
		}
		ParseResult parseResult = new ParseResult();
		try {
			countDown.await();
			for (ExtracteTask task : tasks) {
				Result result = task.getResult();
				for (Entity entity : result.getValues()) {
					parseResult.addEntity(entity.getRuleName(), entity);
				}
			}
			tasks.clear();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return parseResult;
	}
	
	public void notify(ExtracteTask task) {
		if(countDown != null) {
			countDown.countDown();
		}
	}
}
