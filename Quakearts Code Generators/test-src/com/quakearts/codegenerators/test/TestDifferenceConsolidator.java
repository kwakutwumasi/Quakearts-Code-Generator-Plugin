package com.quakearts.codegenerators.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.quakearts.tools.text.DifferenceConsolidatorFactory;

public class TestDifferenceConsolidator {
	
	String testOriginalGenerated = "123456\r\n" + 
			"234567\r\n" + 
			"345678\r\n" + 
			"456789\r\n" + 
			"567890\r\n",
		   testOriginalGeneratedMultiple = "Lorem ipsum dolor sit amet, \r\n" + 
			"consectetur adipiscing elit, \r\n" + 
			"sed do eiusmod tempor incididunt \r\n" + 
			"ut labore et dolore magna \r\n" + 
			"aliqua. Ut enim ad minim veniam, \r\n" + 
			"quis nostrud exercitation \r\n" + 
			"ullamco laboris nisi ut aliquip \r\n" + 
			"ex ea commodo consequat. \r\n" + 
			"Duis aute irure dolor in \r\n" + 
			"reprehenderit in voluptate velit \r\n" + 
			"esse cillum dolore eu fugiat \r\n" + 
			"nulla pariatur. Excepteur sint \r\n" + 
			"occaecat cupidatat non proident, \r\n" + 
			"sunt in culpa qui officia \r\n" + 
			"deserunt mollit anim id \r\n" + 
			"est laborum\r\n";
	
	private BufferedReader toBufferedReader(String string) {
		return new BufferedReader(new StringReader(string));
	};
	
	@Test
	public void testInput() throws IOException {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"012345\r\n" + 
				"345678\r\n" + 
				"456789\r\n" + 
				"567890\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"++012345\r\n" + 
				"345678\r\n" + 
				"456789\r\n" + 
				"567890\r\n"));
	}

	@Test
	public void testRemovedLines() throws Exception {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"456789\r\n" + 
				"567890\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"--345678\r\n" + 
				"456789\r\n" + 
				"567890\r\n"));
	}
	
	@Test
	public void testInputWithNewGeneratedContent() throws Exception {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"012345\r\n" + 
				"345678\r\n" + 
				"456789\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"++012345\r\n" + 
				"345678\r\n" + 
				"456789\r\n" + 
				"--567890\r\n"));		
	}
	
	@Test
	public void testRemovedLinesWithNewGeneratedContent() throws Exception {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"456789\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"--345678\r\n" + 
				"456789\r\n" + 
				"--567890\r\n"));
	}
	
	@Test
	public void testAddedAndRemovedLines() throws Exception {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"012345\r\n" + 
				"345678\r\n" + 
				"567890\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"++012345\r\n" + 
				"345678\r\n" + 
				"--456789\r\n" + 
				"567890\r\n"));
	}
	
	@Test
	public void testAddedAndRemovedLinesWithNewGeneratedContent() throws Exception {
		String testInput = "123456\r\n" + 
				"234567\r\n" + 
				"012345\r\n" + 
				"345678\r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGenerated), testInput.length()));
		assertThat(result, is("123456\r\n" + 
				"234567\r\n" + 
				"++012345\r\n" + 
				"345678\r\n" + 
				"--456789\r\n" + 
				"--567890\r\n"));
	}
	
	@Test
	public void testMultipleAddedLines() throws Exception {
		String testInput = "Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"sed do eiusmod tempor incididunt \r\n" + 
				"Sed ut perspiciatis unde omnis iste \r\n" + 
				"natus error sit voluptatem accusantium \r\n" + 
				"doloremque laudantium, totam rem \r\n" + 
				"ut labore et dolore magna \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"quis nostrud exercitation \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"aperiam, eaque ipsa quae ab illo \r\n" + 
				"inventore veritatis et quasi architecto \r\n" + 
				"ex ea commodo consequat. \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"reprehenderit in voluptate velit \r\n" + 
				"esse cillum dolore eu fugiat \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"beatae vitae dicta sunt explicabo. Nemo \r\n" + 
				"occaecat cupidatat non proident, \r\n" + 
				"sunt in culpa qui officia \r\n" + 
				"enim ipsam voluptatem quia voluptas sit \r\n" + 
				"deserunt mollit anim id \r\n" + 
				"est laborum\r\n" + 
				"aspernatur aut odit aut fugit, sed quia \r\n" + 
				"consequuntur magni dolores eos qui \r\n" + 
				"ratione voluptatem sequi nesciunt";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGeneratedMultiple), testInput.length()));
		assertThat(result, is("Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"sed do eiusmod tempor incididunt \r\n" + 
				"++Sed ut perspiciatis unde omnis iste \r\n" + 
				"++natus error sit voluptatem accusantium \r\n" + 
				"++doloremque laudantium, totam rem \r\n" + 
				"ut labore et dolore magna \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"quis nostrud exercitation \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"++aperiam, eaque ipsa quae ab illo \r\n" + 
				"++inventore veritatis et quasi architecto \r\n" + 
				"ex ea commodo consequat. \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"reprehenderit in voluptate velit \r\n" + 
				"esse cillum dolore eu fugiat \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"++beatae vitae dicta sunt explicabo. Nemo \r\n" + 
				"occaecat cupidatat non proident, \r\n" + 
				"sunt in culpa qui officia \r\n" + 
				"++enim ipsam voluptatem quia voluptas sit \r\n" + 
				"deserunt mollit anim id \r\n" + 
				"est laborum\r\n" + 
				"++aspernatur aut odit aut fugit, sed quia \r\n" + 
				"++consequuntur magni dolores eos qui \r\n" + 
				"++ratione voluptatem sequi nesciunt\r\n"));
	}
	
	@Test
	public void testMultipleRemovedLines() throws Exception {
		String testInput = "Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"quis nostrud exercitation \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"reprehenderit in voluptate velit \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"occaecat cupidatat non proident, \r\n";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGeneratedMultiple), testInput.length()));
		assertThat(result, is("Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"--sed do eiusmod tempor incididunt \r\n" + 
				"--ut labore et dolore magna \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"quis nostrud exercitation \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"--ex ea commodo consequat. \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"reprehenderit in voluptate velit \r\n" + 
				"--esse cillum dolore eu fugiat \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"occaecat cupidatat non proident, \r\n" + 
				"--sunt in culpa qui officia \r\n" + 
				"--deserunt mollit anim id \r\n" + 
				"--est laborum\r\n"));		
	}
		
	@Test
	public void testMultipleAddedAndRemovedLines() throws Exception {
		String testInput = "Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"sed do eiusmod tempor incididunt \r\n" + 
				"ut labore et dolore magna \r\n" + 
				"Sed ut perspiciatis unde omnis \r\n" + 
				"iste natus error sit voluptatem \r\n" + 
				"accusantium doloremque laudantium, \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"ex ea commodo consequat. \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"totam rem aperiam, eaque ipsa quae \r\n" + 
				"ab illo inventore veritatis et \r\n" + 
				"quasi architecto beatae vitae \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"occaecat cupidatat non proident, \r\n" + 
				"deserunt mollit anim id \r\n" + 
				"est laborum";
				
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput), toBufferedReader(testOriginalGeneratedMultiple), testInput.length()));
		
		assertThat(result, is("Lorem ipsum dolor sit amet, \r\n" + 
				"consectetur adipiscing elit, \r\n" + 
				"sed do eiusmod tempor incididunt \r\n" + 
				"ut labore et dolore magna \r\n" + 
				"++Sed ut perspiciatis unde omnis \r\n" + 
				"++iste natus error sit voluptatem \r\n" + 
				"++accusantium doloremque laudantium, \r\n" + 
				"aliqua. Ut enim ad minim veniam, \r\n" + 
				"--quis nostrud exercitation \r\n" + 
				"ullamco laboris nisi ut aliquip \r\n" + 
				"ex ea commodo consequat. \r\n" + 
				"Duis aute irure dolor in \r\n" + 
				"++totam rem aperiam, eaque ipsa quae \r\n" + 
				"++ab illo inventore veritatis et \r\n" + 
				"++quasi architecto beatae vitae \r\n" + 
				"--reprehenderit in voluptate velit \r\n" + 
				"--esse cillum dolore eu fugiat \r\n" + 
				"nulla pariatur. Excepteur sint \r\n" + 
				"occaecat cupidatat non proident, \r\n" + 
				"--sunt in culpa qui officia \r\n" + 
				"deserunt mollit anim id \r\n" + 
				"est laborum\r\n" ));				
	}
	
	@Test
	public void testNoMatch() throws Exception {
		String testInput1="12345\r\n" + 
				"23456\r\n" + 
				"34567\r\n" + 
				"45678",
			   testInput2="56789\r\n" + 
				"67890\r\n" + 
				"78901\r\n" + 
				"89012";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput1), toBufferedReader(testInput2), testInput1.length()));
		
		assertThat(result, is("++12345\r\n" + 
				"++23456\r\n" + 
				"++34567\r\n" + 
				"++45678\r\n" + 
				"--56789\r\n" + 
				"--67890\r\n" + 
				"--78901\r\n" + 
				"--89012\r\n"));
	}
	
	@Test
	public void testRepeatedLines() throws Exception {
		String testInput1="package com.quakearts.test.hibernate;\r\n" + 
		   		"\r\n" + 
		   		"import java.io.Serializable;\r\n" + 
		   		"\r\n" + 
		   		"import javax.persistence.Id;\r\n" + 
		   		"import javax.persistence.ManyToOne;\r\n" + 
		   		"\r\n" + 
		   		"public class TestBean3 implements Serializable {\r\n" + 
		   		"	\r\n" + 
		   		"	/**\r\n" + 
		   		"	 * \r\n" + 
		   		"	 */\r\n" + 
		   		"	private static final long serialVersionUID = -2165482054340952213L;\r\n" + 
		   		"	@Id\r\n" + 
		   		"	private int id1;\r\n" + 
		   		"	private int id2;\r\n" + 
		   		"	@ManyToOne\r\n" + 
		   		"	private TestBean5 testBean5;\r\n" + 
		   		"	private int addedVariable;\r\n" + 
		   		"	\r\n" + 
		   		"	public int getId1() {\r\n" + 
		   		"		return id1;\r\n" + 
		   		"	}\r\n" + 
		   		"	public void setId1(int id1) {\r\n" + 
		   		"		this.id1 = id1;\r\n" + 
		   		"	}\r\n" + 
		   		"	public int getId2() {\r\n" + 
		   		"		return id2;\r\n" + 
		   		"	}\r\n" + 
		   		"	public void setId2(int id2) {\r\n" + 
		   		"		this.id2 = id2;\r\n" + 
		   		"	}\r\n" + 
		   		"	public TestBean5 getTestBean5() {\r\n" + 
		   		"		return testBean5;\r\n" + 
		   		"	}\r\n" + 
		   		"	public void setTestBean5(TestBean5 testBean5) {\r\n" + 
		   		"		this.testBean5 = testBean5;\r\n" + 
		   		"	}\r\n" + 
		   		"	\r\n" + 
		   		"	public int getAddedVariable(){\r\n" + 
		   		"		return addedVariable;\r\n" + 
		   		"	}\r\n" + 
		   		"	\r\n" + 
		   		"	public void setAddedVariable(int addedVariable){\r\n" + 
		   		"		this.addedVariable = addedVariable;\r\n" + 
		   		"	}\r\n" + 
		   		"	\r\n" + 
		   		"}",
			   testInput2="package com.quakearts.test.hibernate;\r\n" + 
						"\r\n" + 
						"import java.io.Serializable;\r\n" + 
						"\r\n" + 
						"import javax.persistence.Id;\r\n" + 
						"import javax.persistence.ManyToOne;\r\n" + 
						"\r\n" + 
						"public class TestBean3 implements Serializable {\r\n" + 
						"	\r\n" + 
						"	/**\r\n" + 
						"	 * \r\n" + 
						"	 */\r\n" + 
						"	private static final long serialVersionUID = -2165482054340952213L;\r\n" + 
						"	@Id\r\n" + 
						"	private int id1;\r\n" + 
						"	private int id2;\r\n" + 
						"	@ManyToOne\r\n" + 
						"	private TestBean4 testBean4;\r\n" + 
						"	@ManyToOne\r\n" + 
						"	private TestBean5 testBean5;\r\n" + 
						"	\r\n" + 
						"	public int getId1() {\r\n" + 
						"		return id1;\r\n" + 
						"	}\r\n" + 
						"	public void setId1(int id1) {\r\n" + 
						"		this.id1 = id1;\r\n" + 
						"	}\r\n" + 
						"	public int getId2() {\r\n" + 
						"		return id2;\r\n" + 
						"	}\r\n" + 
						"	public void setId2(int id2) {\r\n" + 
						"		this.id2 = id2;\r\n" + 
						"	}\r\n" + 
						"	public TestBean4 getTestBean4() {\r\n" + 
						"		return testBean4;\r\n" + 
						"	}\r\n" + 
						"	public void setTestBean4(TestBean4 testBean4) {\r\n" + 
						"		this.testBean4 = testBean4;\r\n" + 
						"	}\r\n" + 
						"	public TestBean5 getTestBean5() {\r\n" + 
						"		return testBean5;\r\n" + 
						"	}\r\n" + 
						"	public void setTestBean5(TestBean5 testBean5) {\r\n" + 
						"		this.testBean5 = testBean5;\r\n" + 
						"	}\r\n" + 
						"	\r\n" + 
						"	\r\n" + 
						"}";
		
		String result = new String(DifferenceConsolidatorFactory.getInstance().getDifferenceConsolidator().consolidate(toBufferedReader(testInput1), toBufferedReader(testInput2), testInput1.length()));
		
		assertThat(result, is("package com.quakearts.test.hibernate;\r\n" + 
				"\r\n" + 
				"import java.io.Serializable;\r\n" + 
				"\r\n" + 
				"import javax.persistence.Id;\r\n" + 
				"import javax.persistence.ManyToOne;\r\n" + 
				"\r\n" + 
				"public class TestBean3 implements Serializable {\r\n" + 
				"	\r\n" + 
				"	/**\r\n" + 
				"	 * \r\n" + 
				"	 */\r\n" + 
				"	private static final long serialVersionUID = -2165482054340952213L;\r\n" + 
				"	@Id\r\n" + 
				"	private int id1;\r\n" + 
				"	private int id2;\r\n" + 
				"	@ManyToOne\r\n" + 
				"--	private TestBean4 testBean4;\r\n" + 
				"--	@ManyToOne\r\n" + 
				"	private TestBean5 testBean5;\r\n" + 
				"++	private int addedVariable;\r\n" + 
				"	\r\n" + 
				"	public int getId1() {\r\n" + 
				"		return id1;\r\n" + 
				"	}\r\n" + 
				"	public void setId1(int id1) {\r\n" + 
				"		this.id1 = id1;\r\n" + 
				"	}\r\n" + 
				"	public int getId2() {\r\n" + 
				"		return id2;\r\n" + 
				"	}\r\n" + 
				"	public void setId2(int id2) {\r\n" + 
				"		this.id2 = id2;\r\n" + 
				"	}\r\n" + 
				"--	public TestBean4 getTestBean4() {\r\n" + 
				"--		return testBean4;\r\n" + 
				"--	}\r\n" + 
				"--	public void setTestBean4(TestBean4 testBean4) {\r\n" + 
				"--		this.testBean4 = testBean4;\r\n" + 
				"--	}\r\n" + 
				"	public TestBean5 getTestBean5() {\r\n" + 
				"		return testBean5;\r\n" + 
				"	}\r\n" + 
				"	public void setTestBean5(TestBean5 testBean5) {\r\n" + 
				"		this.testBean5 = testBean5;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"++	public int getAddedVariable(){\r\n" + 
				"++		return addedVariable;\r\n" + 
				"++	}\r\n" + 
				"++	\r\n" + 
				"++	public void setAddedVariable(int addedVariable){\r\n" + 
				"++		this.addedVariable = addedVariable;\r\n" + 
				"++	}\r\n" + 
				"	\r\n" + 
				"}\r\n"));
	}
}
