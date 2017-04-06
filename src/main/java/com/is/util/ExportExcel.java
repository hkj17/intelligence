package com.is.util;


import java.io.FileNotFoundException;
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.print.attribute.Size2DSyntax;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook; 
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell; 
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.ss.usermodel.Sheet; 
import org.apache.poi.ss.usermodel.Workbook; 

public class ExportExcel { 
    //使用POI创建excel工作簿 
    public static void createWorkBook(HttpServletResponse response) throws IOException {  	
        //创建excel工作簿 
    	response.setContentType("text/html");
    	HSSFWorkbook wb = new HSSFWorkbook(); 
        //创建第一个sheet（页），命名为 new sheet 
        Sheet sheet = wb.createSheet("new sheet"); 
        //创建一个文件 命名为workbook.xls 

        //创建样式      
        /*------------------style-----------------*/
        CellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font = (HSSFFont) wb.createFont();  
        font.setColor(HSSFColor.RED.index);  
        font.setFontName("Arial Unicode MS");
        font.setFontHeightInPoints((short) 18);          
        style.setFont(font); 
        /*--------------style1-----------------*/
        CellStyle style1 = wb.createCellStyle();
        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style1.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style1.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style1.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        HSSFFont font1 = (HSSFFont) wb.createFont();  
        font1.setColor(HSSFColor.BLACK.index);        
        font1.setFontHeightInPoints((short) 10);
        font1.setFontName("Arial Unicode MS");
        style1.setFont(font1); 
        /*--------------style2-----------------*/
        CellStyle style2 = wb.createCellStyle();
        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style1.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
  
        //打印
        export("年月份值班安排表", response, wb);
    }
    
	private static void export(String prefix,
			HttpServletResponse response, Workbook wb) {
		String fileName = prefix + "_" + ".xls";
		// 导出文件到历史文件夹
		//exportFile(fileName,wb);
		// 下载
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		try {
			response.setHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes(), "iso-8859-1"));
			OutputStream outFile = response.getOutputStream();
			wb.write(outFile);
			outFile.flush();
			outFile.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出excel到指定目录
	 * 
	 * @param filePath
	 * @param wb
	 * @return void
	 * @since v 1.0
	 */
	public static void exportFile(String filePath, Workbook wb) {
		FileOutputStream out;
		try {
			out = new FileOutputStream(filePath);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    

} 