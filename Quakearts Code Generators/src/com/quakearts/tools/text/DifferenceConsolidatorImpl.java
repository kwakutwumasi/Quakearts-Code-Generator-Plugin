package com.quakearts.tools.text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DifferenceConsolidatorImpl implements DifferenceConsolidator {


	private static final String NEWLINE = "\r\n";
	private static final String NEW_MATCH_PREFIX = "--";
	private static final String OLD_MATCH_PREFIX = "++";

	/* (non-Javadoc)
	 * @see com.quakearts.tools.utils.DifferenceConsolidator#consolidate(java.io.InputStream, java.io.InputStream)
	 */
	@Override
	public InputStream consolidate(InputStream oldIs, InputStream newIs) throws IOException {
		return new ByteArrayInputStream(consolidate(new BufferedReader(new InputStreamReader(oldIs)), 
				new BufferedReader(new InputStreamReader(newIs)), 
				oldIs.available()>newIs.available()?oldIs.available():newIs.available()));
	}

	@Override
	public byte[] consolidate(BufferedReader oldBuffer, BufferedReader newBuffer, int minOutPutSize)
			throws IOException {
		return consolidate(new EnhancedBufferedReader(oldBuffer), new EnhancedBufferedReader(newBuffer), minOutPutSize);
	}
//	Algorithm:
//	1. Scan through oldBuffer and newBuffer simultaneously, comparing each line
//	1.1 If the buffers are not empty goto 1.2.2. else if the line matches, write it
//	1.2 if the line does not match, 
//		1.2.1 buffer line from oldBuffer into oldBufferLines, buffer line from newBuffer into newBufferLines, move to the next pair
//		1.2.2 compare the line from oldBuffer to each item in newBufferLines, and compare line from newBuffer to each item in oldBuffer
//			1.2.2.1 if a match is found in newBufferLines, write all the contents of oldBufferLines, appending ++, and empty it. 
//					Write all the items in newBufferLines preceding the match, appending -- and remove it. Write the matching line and 
//					remove from the newBufferLines. Buffer line from newBuffer into newBufferLines
//			1.2.2.2 if a match is found in oldBufferLines, write all the contents of newBufferLines, appending --, and empty it.
//					Write all the items in newBufferLines preceding the match, appending -- and remove it. Write the matching line and 
//					remove from the oldBufferLines. Buffer line from oldBuffer into oldBufferLines
//			1.2.2.3 if no match is found, goto 1.2.1
//	2. Repeat until one of the two lists are empty.
//	3. If there are still items in oldBuffer
//		3.1 If there are still items in newBufferLines, compare each line from oldBuffer to the Items in newBufferLines. 
//			3.1.1 If a match is found, write all the preceding lines to the match in the buffer, appending -- and remove them. Write all the items in oldBufferLines, 
//					appending ++ and remove them. Write the matching line and remove from newBufferLines
//			3.1.2 If no match is found, append line to oldBufferLines
//			3.2 Repeat until oldBuffer is empty. Write the remaining items in oldBufferLines.
//		3.2 If there are no items in newBufferLines, write the rest of the items in oldBuffer, appending ++
//	4. If there are still items in newBuffer
//		4.1 If there are still items in oldBufferLines, compare each line from newBuffer to the Items in oldBufferLines. 
//			4.1.1 If a match is found, write all the preceding lines to the match in the buffer, appending ++ and remove them. Write all the items in newBufferLines, 
//					appending -- and remove them. Write the matching line and remove from oldBufferLines
//			4.1.2 If no match is found, append line to newBufferLines
//			4.1.3 Repeat until newBuffer is empty. Write the remaining items in newBufferLines.
//		4.2 If there are no items in oldBufferLines, write the rest of the items in newBuffer, appending --
//	5. Write all remaining items in oldBufferLines, appending ++, if any. Write all remaining items in newBufferLines, appending --, if any.
	/* (non-Javadoc)
	 * @see com.quakearts.tools.utils.DifferenceConsolidator#consolidate(java.io.BufferedReader, java.io.BufferedReader, int)
	 */
	private byte[] consolidate(EnhancedBufferedReader oldBuffer, EnhancedBufferedReader newBuffer, int minOutPutSize) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(minOutPutSize);
		String oldBufferLine, newBufferLine;
		List<String> oldBufferLines = new ArrayList<>(), newBufferLines = new ArrayList<>();
		
		oldBufferLine = oldBuffer.readLine();
		newBufferLine = newBuffer.readLine();
		
		if(oldBufferLine != null && newBufferLine != null)
			do {
				matchLines(oldBufferLine, oldBufferLines, newBufferLine, newBufferLines, bos);
				oldBufferLine = oldBuffer.readLine();
				newBufferLine = newBuffer.readLine();
			} while(oldBufferLine != null && newBufferLine != null);
		
		processUnmatchedLines(oldBufferLines, newBufferLine, newBufferLines, bos, NEW_MATCH_PREFIX, OLD_MATCH_PREFIX, newBuffer);
		processUnmatchedLines(newBufferLines, oldBufferLine, oldBufferLines, bos, OLD_MATCH_PREFIX, NEW_MATCH_PREFIX, oldBuffer);
	
		writeAndClearBufferLines(bos, oldBufferLines, OLD_MATCH_PREFIX);
		writeAndClearBufferLines(bos, newBufferLines, NEW_MATCH_PREFIX);
				
		return bos.toByteArray();
	}

	private void matchLines(String oldBufferLine, List<String> oldBufferLines, String newBufferLine,
			List<String> newBufferLines, ByteArrayOutputStream bos) throws IOException {
		if(!newBufferLines.isEmpty()||!oldBufferLines.isEmpty()) {
			if(testBuffer(newBufferLines, oldBufferLine, oldBufferLines, bos,OLD_MATCH_PREFIX,NEW_MATCH_PREFIX)){
				newBufferLines.add(newBufferLine);						
			} else if(testBuffer(oldBufferLines, newBufferLine, newBufferLines, bos,NEW_MATCH_PREFIX,OLD_MATCH_PREFIX)) {
				oldBufferLines.add(oldBufferLine);
			} else if(isAMatch(oldBufferLine, newBufferLine)){
				writeAndClearBufferLines(bos, newBufferLines, NEW_MATCH_PREFIX);
				writeAndClearBufferLines(bos, oldBufferLines, OLD_MATCH_PREFIX);
				writeToStream(bos, oldBufferLine, "");
			} else {
				oldBufferLines.add(oldBufferLine);
				newBufferLines.add(newBufferLine);
			}					
		} else {
			if(isAMatch(oldBufferLine, newBufferLine)) {
				writeToStream(bos, oldBufferLine, "");
			} else {
				oldBufferLines.add(oldBufferLine);
				newBufferLines.add(newBufferLine);
			}
		}
	}

	private boolean isAMatch(String line, String comparedLine) {
		return line.trim().equals(comparedLine.trim());
	}

	private void writeAndClearBufferLines(OutputStream bos, List<String> bufferLines, String matchPrefix) throws IOException {
		for(String line:bufferLines) {
			writeToStream(bos, line, matchPrefix);			
		}
		
		bufferLines.clear();
	}

	private void processUnmatchedLines(List<String> unMatchedLines, String bufferLine,
			List<String> previousLines, OutputStream bos, String unMatchedLinesPrefix, String previousLinesPrefix,
			EnhancedBufferedReader oldBuffer) throws IOException {
		if(bufferLine != null) {
			if(!unMatchedLines.isEmpty()) {
				do {
					if(!testBuffer(unMatchedLines, bufferLine, previousLines, bos, unMatchedLinesPrefix, previousLinesPrefix)){
						previousLines.add(bufferLine);
					}
				} while((bufferLine = oldBuffer.readLine())!=null);
			} else {
				for(String line:previousLines) {
					writeToStream(bos, line, unMatchedLinesPrefix);			
				}		
				
				previousLines.clear();
				
				writeToStream(bos, bufferLine, unMatchedLinesPrefix);				
			}
		}
	}
	
	private void writeToStream(OutputStream bos, String line, String matchPrefix) throws IOException {
		if(!matchPrefix.isEmpty()) {
			if(line.contains(NEWLINE))
				line = line.replace(NEWLINE, NEWLINE+matchPrefix);
			bos.write(matchPrefix.getBytes());
		}
		
		bos.write(line.getBytes());
		bos.write(NEWLINE.getBytes());
	}
	
	private boolean testBuffer(List<String> unMatchedLines, String lineToTest, List<String> previousLines, OutputStream os, String unMatchedLinesPrefix, String previousLinesPrefix) throws IOException {
		boolean match = false;
		for(int index=0; index < unMatchedLines.size(); index++) {
			String lineToCompare = unMatchedLines.get(index);
			if(isAMatch(lineToCompare, lineToTest)) {
				match = true;
				writeAndClearBufferLines(os,previousLines,unMatchedLinesPrefix);

				for(int innerIndex = 0; innerIndex < index; innerIndex++) {
					String line = unMatchedLines.get(innerIndex);
					writeToStream(os, line, previousLinesPrefix);
				}
				List<String> temp = new ArrayList<>();
				for(int innerIndex = index+1; innerIndex < unMatchedLines.size(); innerIndex++) {
					temp.add(unMatchedLines.get(innerIndex));
				}
				unMatchedLines.clear();
				unMatchedLines.addAll(temp);
				writeToStream(os, lineToTest, "");
				break;
			}
		}
		
		return match;
	}
	
	private static Set<String> delimiterCharacters = new HashSet<>();
	
	static {
		delimiterCharacters.add("{");
		delimiterCharacters.add("}");
		delimiterCharacters.add(";");
		delimiterCharacters.add(",");
		delimiterCharacters.add(".");
		delimiterCharacters.add("\\\\");
		delimiterCharacters.add("/*");
		delimiterCharacters.add("*/");
		delimiterCharacters.add("<!--");
		delimiterCharacters.add("-->");
		delimiterCharacters.add("<br />");
		delimiterCharacters.add("<br/>");
		delimiterCharacters.add("[");
		delimiterCharacters.add("]");
		delimiterCharacters.add("(");
		delimiterCharacters.add(")");
		delimiterCharacters.add("#");
		delimiterCharacters.add("##");
	}
	
	private class EnhancedBufferedReader{
		BufferedReader bufferedReader;
		Stack<String> internalStack = new Stack<>();
		
		public EnhancedBufferedReader(BufferedReader bufferedReader) {
			this.bufferedReader = bufferedReader;
		}
		
		public String readLine() throws IOException {
			String line;
			if(!internalStack.isEmpty()) {
				line = internalStack.pop();
			} else {
				line = bufferedReader.readLine();
			}
			
			if(line != null) {
				String nextLine = bufferedReader.readLine();
				if(nextLine != null) {
					if(isDelimiterCharacter(nextLine)) {
						line+=NEWLINE+nextLine;
					} else {
						internalStack.push(nextLine);
					}
				}
			}

			return line;
		}
		
		private boolean isDelimiterCharacter(String line) {
			return delimiterCharacters.contains(line.trim());
		}

	}
	
	public static Set<String> getDelimiterCharactersSet() {
		return delimiterCharacters;
	}
}
