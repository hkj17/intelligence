package com.is.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;

@Transactional
@Component("visitorService")
public class VisitorService {

	private String IMAGES_PATH="/cloudweb/server/tomcat_intel/webapps/visitor_img/";
	
	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;
	
	
	public String addVisitorInfo(String name,String company,String position,
			String telphone,String email,String companyUrl,
			String deviceId,String importance,String birth,String path){
		VisitorInfo info=new VisitorInfo();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		info.setId(id);
		info.setDeviceId(deviceId);
		info.setName(name);
		info.setCompany(company);
		info.setPosition(position);
		info.setTelphone(telphone);
		info.setEmail(email);
		info.setCompanyUrl(companyUrl);
		info.setImportance(Integer.parseInt(importance));
		info.setBirth(birth);
		info.setPhotoPath(path+".jpg");
		cloudDao.add(info);
		
		String strangerId=path==null?null:path.substring(path.lastIndexOf("/")+1);
		ServiceDistribution.handleJson103_11(id, strangerId, name, company,position,birth, deviceId);
		CheckResponse response=new CheckResponse(deviceId, "103_12");
		response.start();
		return id;
	}
	
	public Boolean addImage(InputStream uploadedInputStream,String id) throws IOException{
		//1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		//解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8"); 
		FileOutputStream out = new FileOutputStream(IMAGES_PATH + id+".jpg");
		try {
			
			byte buffer[] = new byte[1024];
			//判断输入流中的数据是否已经读完的标识
			int len = 0;
			//循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while((len=uploadedInputStream.read(buffer))>0){
			//使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
			out.write(buffer, 0, len);
			}
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			uploadedInputStream.close();
			out.close();
		}
		return true;
	}
	
	
	public List<Visitor> indexVisitor(String depaertmentId,String name,String startTime,String endTime){
		return intelligenceDao.indexVisitor(depaertmentId,name,startTime, endTime);
	}
	
	public VisitorInfo getVisitorById(String id){
		return intelligenceDao.getVisitorInfoById(id);
	}
	
	public Boolean addVisitorLeaveTime(String time,String id) throws ParseException{
		Visitor visitor=intelligenceDao.getVisitorById(id);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		visitor.setEndTime(formatter.parse(time));
		cloudDao.update(visitor);
		return true;
	}

}
