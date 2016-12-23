package com.is.service;

import static com.is.constant.ParameterKeys.VISITOR_FACE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.map.DeviceService;
import com.is.map.FutureMap;
import com.is.model.CollectionPhoto;
import com.is.model.Employee;
import com.is.model.Visitor;
import com.is.model.VisitorInfo;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelHandlerContext;

@Transactional
@Component("visitorService")
public class VisitorService {

	private static Logger logger = Logger.getLogger(VisitorService.class);
	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;

	public void rememPhoto(String path, InputStream uploadedInputStream) throws IOException {
		// path="D:\\IotCloud\\111\\1.jpg";
		// 1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
		FileOutputStream out = new FileOutputStream(path);
		try {
			byte buffer[] = new byte[1024];
			// 判断输入流中的数据是否已经读完的标识
			int len = 0;
			// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while ((len = uploadedInputStream.read(buffer)) > 0) {
				// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" +
				// filename)当中
				out.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			uploadedInputStream.close();
			out.close();
		}
	}

	public Boolean addVisitorInfoByMobile(String id, String name, String company, String position, String telphone,
			String email, String companyUrl, String deviceId, String importance, String birth, String path) throws InterruptedException, ExecutionException, TimeoutException {
		String strangerId = path == null ? null : path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "103_12", future);
		ServiceDistribution.handleJson103_11(id, strangerId, name, company, position, birth, deviceId,
				path);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "103_12");
		if (result==null) {
			return false;
		}
		else{
			VisitorInfo info = new VisitorInfo();
			info.setId(id);
			info.setDeviceId(deviceId);
			info.setName(name);
			info.setCompany(company);
			info.setPosition(position);
			info.setTelphone(telphone);
			info.setEmail(email);
			info.setCompanyUrl(companyUrl);
			info.setImportance(Integer.parseInt(importance));
			if (birth != null && !"".equals(birth)) {
				info.setBirth(birth);
			}
			info.setPhotoPath(path);
			cloudDao.add(info);

			return true;
		}

	}

	public String addVisitorInfo(String name, String company, String position, String telphone, String email,
			String companyUrl, String deviceId, String importance, String birth, String path, String cid) throws InterruptedException, ExecutionException, TimeoutException {
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String strangerId = path == null ? null : path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "103_12", future);
		ServiceDistribution.handleJson103_11(id, strangerId, name, company, position, birth, deviceId,null);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "103_12");
		if(result!=null){
			VisitorInfo info = new VisitorInfo();
			info.setId(id);
			info.setDeviceId(deviceId);
			info.setName(name);
			info.setCompany(company);
			info.setPosition(position);
			info.setTelphone(telphone);
			info.setEmail(email);
			info.setCompanyUrl(companyUrl);
			info.setImportance(Integer.parseInt(importance));
			if (birth != null && !"".equals(birth)) {
				info.setBirth(birth);
			}

			File file = new File(path);
			String visitorPhoto = file.getName();

			String newpath = VISITOR_FACE + deviceId;
			if (!(new File(newpath).isDirectory())) {
				new File(newpath).mkdirs();
			}
			newpath = newpath + "/" + visitorPhoto;
			file.renameTo(new File(newpath));

			info.setPhotoPath(newpath);
			cloudDao.add(info);

			if (cid != null && !"".equals(cid)) {
				CollectionPhoto photo = intelligenceDao.getCollectionPhotoById(cid);
				cloudDao.delete(photo);
			}
			return id;
		}
		else{
			return null;
		}

	}

	public boolean updateVisitorInfo(String deviceId, String id, String name, String company, String position,
			String telphone, String email, String importance, String birth) throws InterruptedException, ExecutionException, TimeoutException {
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return false;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "104_12", future);
		ServiceDistribution.handleJson104_11(deviceId, id, name, company, position, telphone, email,
				importance, birth);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "104_12");

		if (result!=null) {
			VisitorInfo visitorInfo = intelligenceDao.getVisitorInfoById(id);
			visitorInfo.setName(name);
			visitorInfo.setCompany(company);
			visitorInfo.setPosition(position);
			visitorInfo.setTelphone(telphone);
			visitorInfo.setEmail(email);
			visitorInfo.setImportance(Integer.parseInt(importance));
			if (birth != null && !"".equals(birth)) {
				visitorInfo.setBirth(birth);
			}
			cloudDao.update(visitorInfo);
			return true;
		}
		else{
			return false;
		}

	}

	public List<Visitor> indexVisitor(String depaertmentId, String name, String startTime, String endTime,
			String deviceId) {
		return intelligenceDao.indexVisitor(depaertmentId, name, startTime, endTime, deviceId);
	}

	public VisitorInfo getVisitorById(String id) {
		return intelligenceDao.getVisitorInfoById(id);
	}

	public Boolean addVisitorLeaveTime(String time, String id) throws ParseException {
		Visitor visitor = intelligenceDao.getVisitorById(id);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		visitor.setEndTime(formatter.parse(time));
		cloudDao.update(visitor);
		return true;
	}

	public Boolean deleteVisitorInfo(String deviceId, String visitorId) throws InterruptedException, ExecutionException, TimeoutException {
		VisitorInfo visitorInfo = intelligenceDao.getVisitorInfoById(visitorId);
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return false;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "105_12", future);
		ServiceDistribution.handleJson105_11(deviceId, visitorId);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "105_12");

		if (visitorInfo != null && result!=null) {
			cloudDao.delete(visitorInfo);
		}
		return true;
	}

	public String updateVisitorInfoByRecord(String name, String company, String position, String telphone, String email,
			String companyUrl, String deviceId, String importance, String birth, String visitorId) throws InterruptedException, ExecutionException, TimeoutException {
		Visitor visitor = intelligenceDao.getVisitorById(visitorId);
		String path = visitor.getPhoto();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		String strangerId = path == null ? null : path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "103_12", future);
		ServiceDistribution.handleJson103_11(id, strangerId, name, company, position, birth, deviceId, null);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "103_12");
		
		if(result!=null){
			VisitorInfo info = new VisitorInfo();
			info.setId(id);
			info.setDeviceId(deviceId);
			info.setName(name);
			info.setCompany(company);
			info.setPosition(position);
			info.setTelphone(telphone);
			info.setEmail(email);
			info.setCompanyUrl(companyUrl);
			info.setImportance(Integer.parseInt(importance));
			if (birth != null) {
				info.setBirth(birth);
			}
			info.setPhotoPath(path);
			cloudDao.add(info);

			visitor.setVisitorInfo(info);
			cloudDao.update(visitor);

			return id;
		}
		else{
			return null;
		}

	}

	public Boolean deleteVisitorRecord(String id) {
		Visitor visitor = intelligenceDao.getVisitorById(id);
		if (visitor != null) {
			cloudDao.delete(visitor);
		}
		return true;
	}

	public List<VisitorInfo> getVisitorInfoByWhere(String deviceId, String name) {
		return intelligenceDao.getVisitorInfoByWhere(deviceId, name);
	}

	public void updateVisitorTemplate(String id, String path) {
		VisitorInfo visitorInfo = intelligenceDao.getVisitorInfoById(id);
		if (visitorInfo != null) {
			visitorInfo.setTemplatePath(path);
			cloudDao.update(visitorInfo);
		}
	}

	public void insertVisitor(String deviceId, String infoId, String time, String employeeId, String path) {
		Visitor visitor = new Visitor();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		visitor.setId(id);
		if (infoId != null) {
			VisitorInfo visitorInfo = intelligenceDao.getVisitorInfoById(infoId);
			if (visitorInfo != null) {
				visitor.setVisitorInfo(visitorInfo);
				visitor.setPhoto(visitorInfo.getPhotoPath());
			}
		}
		if (path != null) {
			visitor.setPhoto(path);
		}
		if (employeeId != null) {
			Employee employee = intelligenceDao.getEmployeeById(employeeId);
			visitor.setEmployee(employee);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// logger.info(time);
		try {
			if (time != null) {
				visitor.setStartTime(formatter.parse(time));
			} else {
				visitor.setStartTime(new Date());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		visitor.setDeviceId(deviceId);
		cloudDao.add(visitor);
	}

	public void collectionPhoto(String deviceId, String time, String path, String strangerId) {
		CollectionPhoto photo = new CollectionPhoto();
		String id = UUID.randomUUID().toString().trim().replaceAll("-", "");
		photo.setId(id);
		photo.setDeviceId(deviceId);
		photo.setPhoto(path);
		photo.setStrangerId(strangerId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		photo.setTime(sdf.format(new Date()));
		cloudDao.add(photo);

	}

	public void deletePhoto(String cid, String deviceId) {
		CollectionPhoto photo = intelligenceDao.getCollectionPhotoById(cid);
		String strangerId = photo.getStrangerId();
		SyncFuture<String> future = AddFuture.setFuture(deviceId,"105_22");
		CheckResponse response = new CheckResponse(deviceId, "105_22", future);
		response.start();
		boolean state = ServiceDistribution.handleJson105_21(strangerId, deviceId);

		if (photo != null && state) {
			cloudDao.delete(photo);
		}
	}

	public CollectionPhoto getCollectByStrangerId(String id) {
		return intelligenceDao.getCollectByStrangerId(id);
	}

}
