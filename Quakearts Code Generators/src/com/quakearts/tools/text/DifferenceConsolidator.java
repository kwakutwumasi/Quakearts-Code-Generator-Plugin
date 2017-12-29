package com.quakearts.tools.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public interface DifferenceConsolidator {

	InputStream consolidate(InputStream oldIs, InputStream newIs) throws IOException;

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
	byte[] consolidate(BufferedReader oldBuffer, BufferedReader newBuffer, int minOutPutSize) throws IOException;

}