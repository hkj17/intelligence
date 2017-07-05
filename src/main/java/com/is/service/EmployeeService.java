package com.is.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Employee;
import com.is.model.Template;
import com.is.model.VersionUpdate;
import com.is.model.Visitor;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.util.JavaSms;
import com.is.util.Page;
import com.is.websocket.SocketService;

import io.netty.util.internal.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author lishuhuan
 * @date 2016年9月5日 类说明
 */
@Transactional
@Component("employeeService")
public class EmployeeService {

	@Autowired
	private IntelligenceDao intelligenceDao;
	
	@Autowired
	private CloudDao cloudDao;
	
	private static Logger logger = Logger.getLogger(EmployeeService.class);

	private static String ENCODING = "UTF-8";

	public String getEmployeeByMobile(String mobile) {
		return intelligenceDao.getEmployeeByMobile(mobile);
	}

	public String sendMsg(String employeeId,String tqlId) {
		String result = null;
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		if (null == employee.getEmployeeName()) {
			result = "no user!";
		}
		String tpl_value;
		try {
			tpl_value = URLEncoder.encode("#name#", ENCODING) + "="
					+ URLEncoder.encode(employee.getEmployeeName(), ENCODING);
			result = JavaSms.tplSendSms(tpl_value, employee.getTelphone(),tqlId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject getStrangerPhoto(String departmentId,String name,String startTime,String endTime,String tag,String deviceId){
		JSONObject jsonObject=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int tagnum=Integer.parseInt(tag);
		int count=intelligenceDao.indexVisitorCount(departmentId, name, startTime, endTime,deviceId);
		jsonObject.put("totalPage", count);	
		
		int first=(tagnum-1)*25;
		List<Visitor> visitors=intelligenceDao.indexVisitorPath(departmentId, name, startTime, endTime,deviceId,first);
		jsonObject.put("currentPage", tagnum);
		JSONArray jsonArray=new JSONArray();
		for(Visitor visitor:visitors){
			JSONObject viJsonObject=new JSONObject();
			viJsonObject.put("visitor", visitor);
			viJsonObject.put("url", visitor.getPhoto());
			if(visitor.getStartTime()!=null){
				viJsonObject.put("time", formatter.format(visitor.getStartTime()));
			}
			jsonArray.add(viJsonObject);
		}
		jsonObject.put("photo", jsonArray);
		return jsonObject;
	}
	
	public JSONObject getCollectionPhotoList(String startTime,String endTime,String tag,String deviceId){
		Page page=intelligenceDao.getCollectionPhotoList(startTime,endTime,tag,deviceId);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("totalPage", page.getPageTotal());
		jsonObject.put("currentPage", Integer.parseInt(tag));
		jsonObject.put("photo", page.getList());
		return jsonObject;
		
	}
	
	public static Map<String, Object> group(List<Visitor> list,String tag){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> map=new HashMap<>();
		List<String> result=new ArrayList<>();
		List<Visitor> other=new ArrayList<>();
		for(Visitor visitor:list){
			String compare=sdf.format(visitor.getStartTime());
			if(tag.equals(compare)){
				result.add(visitor.getPhoto());
			}
			else{
				other.add(visitor);
			}
		}
		map.put("result", result);
		map.put("other", other);
		return map;
	}
	
	public VersionUpdate autoUpdate(String deviceId){
		return intelligenceDao.autoUpdate(deviceId);
	}
	
	public static JSONObject needUpdate(String currentVersion, String updateVersion){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("currentVersion", currentVersion);
		jsonObject.put("latestVersion", updateVersion);
		if(currentVersion==null || updateVersion==null){
			jsonObject.put("needUpdate", false);
			return jsonObject;
		}
		try{
			String[] currentVersionCode = currentVersion.split("\\.");
			String[] updateVersionCode = updateVersion.split("\\.");
			int length = Math.max(currentVersionCode.length, updateVersionCode.length);
			for(int i=0;i<length;i++){
				int num0 = i<currentVersionCode.length ? Integer.parseInt(currentVersionCode[i]) : 0;
				int num1 = i<updateVersionCode.length ? Integer.parseInt(updateVersionCode[i]) : 0;
				System.out.println(num0 + " " + num1);
				if(num0 < num1){
					jsonObject.put("needUpdate", true);
					return jsonObject;
				}
				if(num0 > num1){
					jsonObject.put("needUpdate", false);
					return jsonObject;
				}
			}
			jsonObject.put("needUpdate", false);
			return jsonObject;
		}catch(Exception e){
			e.printStackTrace();
			jsonObject.put("needUpdate", false);
			return jsonObject;
		}
	}

	public List<String> getEmployeePhotoList(String employeeId) {
		List<String> photoPathList = new ArrayList<String>();
		List<Template> templates = intelligenceDao.getTemplateListByEmployeeId(employeeId);
		for(Template template: templates){
			String path = template.getPhotoPath();
			if(!StringUtil.isNullOrEmpty(path)){
				photoPathList.add(path);
			}
		}
		logger.info("图片列表: " + photoPathList);
		return photoPathList;
	}

	public boolean updateEmployeePortrait(String employeeId, String photoPath) {
		try{
			Employee employee = intelligenceDao.getEmployeeById(employeeId);
			if(!StringUtil.isNullOrEmpty(photoPath)){
				employee.setPhotoPath(photoPath);
				employee.setEmpVersion(employee.getEmpVersion()+1);
				cloudDao.update(employee);
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
