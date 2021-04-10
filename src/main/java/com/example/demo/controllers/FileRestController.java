package com.example.demo.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvException;

@RestController
public class FileRestController {
	
	private static String UPLOAD_DIR = "uploads";
	
	@RequestMapping(value = "uploadFile", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		try {
			if(file.getContentType().equalsIgnoreCase("text/csv")) {
				String fileName = "Properties.csv";
				String path = request.getServletContext().getRealPath("/") + UPLOAD_DIR + File.separator + fileName;
				String fileStatus = saveFile(file.getInputStream(), path);
				if(fileStatus.equalsIgnoreCase("File is corrupted")) {
					File f = new File(path);
					f.delete();
				}
				return fileName +" "+ fileStatus;
			}
			else {
				return "Please select a CSV file to upload";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@RequestMapping(value = "/getPropertyById/{pk}", method = RequestMethod.POST)
	public Map<String,String> readValuesfromCSV(@PathVariable String pk, HttpServletRequest request) throws IOException, CsvException {
		String path = request.getServletContext().getRealPath("/") + UPLOAD_DIR + File.separator + "Properties.csv";
		Reader reader = Files.newBufferedReader(Paths.get(path));
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
		List<String[]> records = csvReader.readAll();
		Map<String, String> result = new HashMap<String, String>();
		for (String[] record : records) {
	        if(record[0].equals(pk)) {
	        	result.put("PRIMARY_KEY", record[0]);
	        	result.put("PROPERTY_NAME", record[1]);
	        	result.put("DESCRIPTION", record[2]);
	        	result.put("TIMESTAMP", record[3]);
	        }
	    }
		csvReader.close();
		reader.close();
		return result;
	}
	
	@RequestMapping(value = "/deletePropertyById/{pk}", method = RequestMethod.POST)
	public String deleteValuesfromCSV(@PathVariable String pk, HttpServletRequest request) throws IOException, CsvException {
		String path = request.getServletContext().getRealPath("/") + UPLOAD_DIR + File.separator + "Properties.csv";
		Reader reader = Files.newBufferedReader(Paths.get(path));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();
		List<String[]> records = csvReader.readAll();
		csvReader.close();
		reader.close();
		Writer writer = Files.newBufferedWriter(Paths.get(path));
		ICSVWriter csvWriter = new CSVWriterBuilder(writer).build();
		for (String[] record : records) {
	        if(!record[0].equals(pk)) {
	        	csvWriter.writeNext(record);
	        }
	    }
		csvWriter.close();
	    writer.close();
		return "Primary Key Deleted";
	}
	
	private String saveFile(InputStream inputStream, String path) {
		String fileStatus = "";
		try {
			OutputStream outputStream = new FileOutputStream(new File(path));
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = inputStream.read(bytes)) !=-1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.flush();
			outputStream.close();
			fileStatus = validateFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileStatus;
	}
	
	public String validateFile(String path) throws IOException, CsvException {
		Reader reader = Files.newBufferedReader(Paths.get(path));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();
		List<String[]> records = csvReader.readAll();
		String fileStatus ="";
		for (String[] record : records) {
			if(record.length!=4) {
				fileStatus = "File is corrupted";
			}
	    }
		return fileStatus;
	}
}
