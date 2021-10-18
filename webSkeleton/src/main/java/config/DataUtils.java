package config;

import java.util.Hashtable;

import utility.ExcelReader;

public class DataUtils {

	public String sheetName;

	public static Object[][] getData(ExcelReader xls, String testName, String sheetName){
		// reads data for only testCaseName

		int testStartRowNum=1;
		while(!xls.getCellData(sheetName, 0, testStartRowNum).equals(testName)){
			testStartRowNum++;
		}
		int colStartRowNum=testStartRowNum+1;
		int dataStartRowNum=testStartRowNum+2;

		// calculate rows of data
		int rows=0;
		while(!xls.getCellData(sheetName, 0, dataStartRowNum+rows).equals("")){
			rows++;
		}

		//calculate total cols
		int cols=0;
		while(!xls.getCellData(sheetName, cols, colStartRowNum).equals("")){
			cols++;
		}
		Object[][] data = new Object[rows][1];
		//read the data
		int dataRow=0;
		Hashtable<String,String> table=null;
		for(int rNum=dataStartRowNum;rNum<dataStartRowNum+rows;rNum++){
			table = new Hashtable<String,String>();
			for(int cNum=0;cNum<cols;cNum++){
				String key=xls.getCellData(sheetName,cNum,colStartRowNum);
				String value= xls.getCellData(sheetName, cNum, rNum);
				table.put(key, value);
				// 0,0 0,1 0,2
				//1,0 1,1
			}
			data[dataRow][0] =table;
			dataRow++;
		}
		return data;
	}
	
	// true - N
	// false - Y
	//Checks the test is runnable or not 
	public static boolean isSkip(ExcelReader xls, String testName){
		int rows = xls.getRowCount(Constants.TESTCASES_SHEET);

		for(int rNum=2;rNum<=rows;rNum++){
			String tcid = xls.getCellData(Constants.TESTCASES_SHEET, Constants.TCID_COL, rNum);
			if(tcid.equals(testName)){
				String runmode = xls.getCellData(Constants.TESTCASES_SHEET, Constants.RUNMODE_COL, rNum);
				if(runmode.equals("Y"))
					return false;
				else
					return true;
			}
		}

		return true;//default

	}

	public static String testDescription(ExcelReader xls, String testName){

		int rows = xls.getRowCount(Constants.TESTCASES_SHEET);

		for(int rNum=2;rNum<=rows;rNum++){
			String tcid = xls.getCellData(Constants.TESTCASES_SHEET, Constants.TCID_COL, rNum);
			if(tcid.equals(testName)){
				String runmode = xls.getCellData(Constants.TESTCASES_SHEET, Constants.RUNMODE_COL, rNum);
				if(runmode.equals("Y")){
					String desc = xls.getCellData(Constants.TESTCASES_SHEET, Constants.DESCRIPTION_COL, rNum);
					return desc;
				}
			}
		}
		return null;
	}
}