package com.quakearts.codegenerators.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.Test;

public class TestDifferenceAlgorithm {

	@Test
	public void test() {
		ByteArrayInputStream oldIs1 = new ByteArrayInputStream(("Many they are\r\n"
				+ "They that seek the power of the Eldor Warriors\r\n"
				+ "Many they are that try\r\n"
				+ "Few they are that succeed\r\n"
				+ "Those that do become legends, gods of men\r\n"
				+ "They are called Eldor Warriors").getBytes()),
				
				oldIs2 = new ByteArrayInputStream(("Many they are\r\n"
						+ "They that seek the power of the Eldor Warriors\r\n"
						+ "Many they are that try\r\n"
						+ "Few they are that succeed\r\n"
						+ "Few they are that achieve their dreams\r\n"
						+ "Fewer more live long enough to enjoy their fruits\r\n"
						+ "Those that do become legends, gods of men\r\n"
						+ "They are called Eldor Warriors").getBytes()),
				
				newIs1 = new ByteArrayInputStream(("Many they are\r\n"
						+ "They that seek the power of the Eldor Warriors\r\n"
						+ "Many they are that try\r\n"
						+ "Few they are that succeed\r\n"
						+ "Those that do become legends, gods of men\r\n"
						+ "They are called Eldor Warriors").getBytes()),
				
				newIs2 = new ByteArrayInputStream(("Many they are\r\n"
						+ "They that seek the power of the Eldor Warriors\r\n"
						+ "The power to shape their time as they saw fit\r\n"
						+ "The power to raze mountains and drive back the Cloud Seas\r\n"
						+ "Many they are that try\r\n"
						+ "Few they are that succeed\r\n"
						+ "Those that do become legends, gods of men\r\n"
						+ "They are called Eldor Warriors").getBytes()),
						
				newIs3 = new ByteArrayInputStream(("Many they are\r\n"
						+ "They that seek the power of the Ancients\r\n"
						+ "For the power of the Eldor Warriors is great\r\n"
						+ "Many they are that try\r\n"
						+ "Few they are that succeed\r\n"
						+ "Those that do become legends, gods of men\r\n"
						+ "They are called Eldor Warriors").getBytes()),
				
				newIs4 = new ByteArrayInputStream(("Many they are\r\n"
						+ "They that seek the power of the Eldor Warriors\r\n"
						+ "For the power of the Eldor Warriors is great\r\n"
						+ "Many they are that try\r\n"
						+ "Few they are that succeed\r\n"
						+ "Fewer more live long enough to enjoy their fruits\r\n"
						+ "They that seek the power of the Ancients\r\n"
						+ "They are indeed many\r\n"
						+ "Fewer more live long enough to enjoy their fruits\r\n"
						+ "Those that do become legends, gods of men\r\n"
						+ "They are called Eldor Warriors").getBytes());
		
		try {
			//Case 1, no difference
			print(consolidateDifferences(oldIs1, newIs1, true));
			//Case 2, lines added to the new
			oldIs1.reset();
			newIs1.reset();
			print(consolidateDifferences(oldIs1, newIs2, true));
			//Case 3, lines added and removed
			oldIs1.reset();
			newIs2.reset();
			print(consolidateDifferences(oldIs1, newIs3, true));
			//Case 4, lines added, some repeating
			oldIs1.reset();
			newIs3.reset();
			print(consolidateDifferences(oldIs1, newIs4, true));
			newIs4.reset();
			//Case 5, new lines added to the original
			print(consolidateDifferences(oldIs2, newIs1, true));
			oldIs2.reset();
			//Case 6, new lines added to the original, and new lines added to the new
			print(consolidateDifferences(oldIs2, newIs2, true));
			oldIs2.reset();
			//Case 7, new lines added to the original, and lines added and removed from the new
			print(consolidateDifferences(oldIs2, newIs3, true));
			oldIs2.reset();
			//Case 8, new lines added to the original, lines added, some repeating in the new			
			print(consolidateDifferences(oldIs2, newIs4, true));
		} catch (IOException e) {
		}

	}
	
	private void print(InputStream is) throws IOException{
		int read;
		while ((read=is.read())!=-1) {
			System.out.write(read);
		}
		System.out.println();
	}

	@SuppressWarnings("unchecked")
	private InputStream consolidateDifferences(InputStream oldIs, InputStream newIs, boolean markChanges) throws IOException {
		BufferedReader oldBuffer = new BufferedReader(new InputStreamReader(oldIs)),
				newBuffer = new BufferedReader(new InputStreamReader(newIs));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(oldIs.available()>newIs.available()?oldIs.available():newIs.available());
		
		ArrayList<String> newLines = new ArrayList<>();
		HashMap<String, Object> newLinesMap = new HashMap<>();
		String line, comparator;
		boolean ineditedportion = false;
		
		int lineCount = 0;
		while ((line = newBuffer.readLine())!=null) {
			newLines.add(line);
			if(!newLinesMap.containsKey(line)){
				newLinesMap.put(line, lineCount++);
			} else {
				Object obj = newLinesMap.get(line);
				LinkedList<Integer> lineCounts;
				if(obj instanceof Integer){
					lineCounts = new LinkedList<>();
					lineCounts.add((Integer) obj);
					newLinesMap.put(line, lineCounts);
				} else {
					lineCounts = (LinkedList<Integer>) obj;
				}
				
				lineCounts.add(lineCount++);
			}
		}
		
		lineCount = 0;
		int scantill;
		while ((line = oldBuffer.readLine())!=null && lineCount < newLines.size()) {
			comparator = newLines.get(lineCount);
			if(line.equals(comparator)){
				if(markChanges && ineditedportion){
					bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
					ineditedportion = false;
				}

				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
				lineCount++;
			} else {
				if(newLinesMap.containsKey(line)){
					if(markChanges && ineditedportion){
						bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
						ineditedportion = false;
					}

					Object obj = newLinesMap.get(line);
					if(obj instanceof Integer){
						scantill = (Integer) obj;
						newLinesMap.remove(line);
					} else {
						LinkedList<Integer> lineCounts = (LinkedList<Integer>) obj;
						scantill = lineCounts.removeFirst();
						if(lineCounts.isEmpty()){
							newLinesMap.remove(line);
						}
					}
					
					if(markChanges)
						bos.write("<<<Start: New Changes>>>\r\n".getBytes());
					for(;lineCount<scantill;lineCount++){
						line = newLines.get(lineCount);
						bos.write(line.getBytes());
						bos.write("\r\n".getBytes());
					}

					if(markChanges)
						bos.write("<<<End: New Changes>>>\r\n".getBytes());

					line = newLines.get(lineCount);
					bos.write(line.getBytes());
					bos.write("\r\n".getBytes());
					
					lineCount++;					

				} else {
					if(markChanges && !ineditedportion){
						bos.write("<<<Start: Edited Portion>>>\r\n".getBytes());
						ineditedportion = true;
					}
					
					bos.write(line.getBytes());
					bos.write("\r\n".getBytes());
					
				}
			}
		}

		if(markChanges && ineditedportion){
			bos.write("<<<End: Edited Portion>>>\r\n".getBytes());
			ineditedportion = false;
		}
		
		if(line!=null){
			if(markChanges)
				bos.write("<<<Start: New Changes>>>\r\n".getBytes());
			
			do {				
				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
			} while((line = oldBuffer.readLine())!=null);

			if(markChanges)
				bos.write("<<<End: New Changes>>>\r\n".getBytes());
		} else if(lineCount < newLines.size()){
			for(;lineCount<newLines.size();lineCount++){
				line = newLines.get(lineCount);
				bos.write(line.getBytes());
				bos.write("\r\n".getBytes());
			}
		}
				
		return new ByteArrayInputStream(bos.toByteArray());
	}
	
}
