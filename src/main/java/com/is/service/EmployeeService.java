package com.is.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.model.Employee;
import com.is.model.VersionUpdate;
import com.is.model.Visitor;
import com.is.system.dao.IntelligenceDao;
import com.is.util.JavaSms;
import com.is.util.Page;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	public VersionUpdate autoUpdate(){
		return intelligenceDao.autoUpdate();
	}

}
