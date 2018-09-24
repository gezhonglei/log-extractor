package com.gezhonglei.common.log.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import com.gezhonglei.common.log.extractor.entity.DataSet;
import com.gezhonglei.common.log.extractor.entity.DataTable;
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
	
	public void writeResult(ParseResult result) throws IOException {
		File file = new File(this.config.getOutputPath());
		if(!file.exists()) {
			logger.info("not exist file:{}", file);
			throw new IOException("File or Directory does not exist while writing result");
		}
		
		long beginTime = System.currentTimeMillis();
		logger.debug("Data merge begin");
		DataSet dataSet = new OutputHandler(result, config).handle2();
		long endTime = System.currentTimeMillis();
		logger.debug("Data merge end, cost={}", endTime - beginTime);
		
		for (DataTable table : dataSet.getTables()) {
			if(file.isFile()) {
				this.writeDataTableToFile(table, file);
			} else if(file.isDirectory()) {
				File subFile = Paths.get(file.getCanonicalPath(), table.getName()).toFile();
				try {
					this.writeDataTableToFile(table, subFile);
				} catch (IOException e) {
					logger.error("write result error", e);
				}
			}
		}
		
	}
	
	private void writeDataTableToFile(DataTable table, File file) throws IOException {
		long beginTime = System.currentTimeMillis();
		logger.debug("begin wirte: {}, size={}", table.getName(), table.size());
		FileOutputStream fos = null;
		OutputStreamWriter osWriter = null;
		BufferedWriter bufWriter = null;
		try {
			fos = new FileOutputStream(file, false);
			osWriter = new OutputStreamWriter(fos, config.getEncode());
			bufWriter = new BufferedWriter(osWriter, config.getBufferSize() * 1024);
			final BufferedWriter writer = bufWriter;
			
			StringBuilder strBuilder = new StringBuilder();
			table.getFields().forEach(f-> {
				strBuilder.append(f.getName() + "\t");
			});
			strBuilder.append(System.lineSeparator());
			this.appendTo(writer, strBuilder.toString());
			
			int size = table.getFields().size();
			table.getValues().forEach(row -> {
				StringBuilder strBuffer = new StringBuilder();
				for (int i = 0; i < size; i++) {
					strBuffer.append(row.getValue(i) + "\t");
				}
				strBuffer.append(System.lineSeparator());
				this.appendTo(writer, strBuffer.toString());
			});
			writer.close();
			osWriter.close();
			fos.close();
		} 
		finally {
			if(bufWriter != null) {
				try {
					bufWriter.close();
				} catch (IOException e) {
				}
			}
			if(osWriter != null) {
				try {
					osWriter.close();
				} catch (IOException e) {
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
			long endTime = System.currentTimeMillis();
			logger.debug("end wirte: {}, cost={}", table.getName(), endTime - beginTime);
		}
	}
	
	private boolean appendTo(BufferedWriter writer, String str) {
		try {
			writer.append(str);
			return true;
		} catch (Exception e) {
			return false;
		}
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
			logger.info("parse files finished!");
			for (ExtracteTask task : tasks) {
				Result result = task.getResult();
				for (Entity entity : result.getValues()) {
					parseResult.addEntity(entity.getRuleName(), entity);
				}
			}
			tasks.clear();
		} catch (InterruptedException e) {
			logger.error("task await error", e);
		}
		return parseResult;
	}
	
	public void notify(ExtracteTask task) {
		if(countDown != null) {
			logger.info("progress: {}/{}", tasks.size() - countDown.getCount() + 1, tasks.size());
			countDown.countDown();
		}
	}
}
