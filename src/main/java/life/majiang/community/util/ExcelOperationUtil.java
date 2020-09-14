package life.majiang.community.util;

import life.majiang.community.model.User;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExcelOperationUtil{
	//导入,文件里不需要编号
	public List<User> readExcelFileToDB(String filePath, Class<?> t){
		List<User> list = new ArrayList<User>();
        try{
			FileInputStream fis = new FileInputStream(filePath);
			POIFSFileSystem fs  = new POIFSFileSystem(fis);
			HSSFWorkbook workbook = new HSSFWorkbook(fs);//创建Excel工作簿对象
			HSSFSheet sheet = workbook.getSheetAt(0);//获取第1个工作表
			HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
			for(int i=1;i<=sheet.getLastRowNum();i++){//循环Excel文件的每一行
				HSSFRow row = sheet.getRow(i);//获取第i行
				User emp=new User();
				for(int j = 1; j<sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
					HSSFCell cell = row.getCell(j);//获取第i行第j列
					cell.setCellType(cell.CELL_TYPE_STRING);
					String data = cell.getStringCellValue();//获取第i行的第j个单元格的数据
					Field[] f=t.getDeclaredFields();
					String attributeName=f[j].getName();
					String methodName=attributeName.substring(0,1).toUpperCase()+attributeName.substring(1);
					try{
						//获取Test类当前属性的setXXX方法（私有和公有方法）
						/*Method setMethod=Test.class.getDeclaredMethod("set"+methodName);*/
						//获取Test类当前属性的setXXX方法（只能获取公有方法）
                        //string类型
						Method setMethod=t.getMethod("set"+methodName,String.class);
						//执行该set方法
						setMethod.invoke(emp,data);
					}catch (NoSuchMethodException e) {
						try {
							Method setMethod = t.getMethod("set" + methodName, Long.class);
							setMethod.invoke(emp, Long.parseLong(data));
						} catch (Exception e2) {
							f[j].set(emp,data);
						}
					}
				}
				list.add(emp);
			}
			fis.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
}

