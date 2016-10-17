package com.is.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.constant.ResponseCode;
import com.is.map.PhotoMap;
import com.is.model.Admin;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Employee;
import com.is.model.Message;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author lishuhuan
 * @date 2016骞�3鏈�31鏃� 绫昏鏄�
 */
@Transactional
@Component("adminService")
public class AdminService {

	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;

	public void test2() {
		System.out.println("123");
	}

	public boolean AccountAssignment(String adminName, String password, String auth, String employeeId,
			String deviceId) {
		Admin admin = new Admin();
		String adminId = UUID.randomUUID().toString().trim().replaceAll("-", "");
		admin.setAdminId(adminId);
		admin.setUsername(adminName);
		admin.setPassword(password);
		admin.setAuthority(Integer.parseInt(auth));
		admin.setDeviceId(deviceId);
		cloudDao.add(admin);
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		employee.setAdmin(admin);
		cloudDao.update(employee);
		return true;

	}
	
	public Admin getAdminByName(String name){
		return intelligenceDao.getAdminByName(name);
	}

	public Admin login(String username, String password) {
		Admin admin = null;
		if (username == null || password == null) {
			admin = new Admin();
			admin.setResponseCode(ResponseCode.USER_NOT_EXIST);
			return admin;
		} else {
			admin = intelligenceDao.getAdminByName(username);
			if (admin == null) {
				admin = new Admin();
				admin.setResponseCode(ResponseCode.USER_NOT_EXIST);
				return admin;
			}
			if (admin.getPassword().equals(password)) {
				admin.setResponseCode(ResponseCode.SUCCESS);
				return admin;
			} else {
				admin = new Admin();
				admin.setResponseCode(ResponseCode.WRONG_PSW);
				return admin;
			}
		}
	}

	public List<Employee> getEmployeeList() {
		return intelligenceDao.getEmployeeList();
	}

	public List<Company> getCompanyList() {
		return intelligenceDao.getCompanyList();
	}

	public String addEmployee(String name, String birth, String contact,
			String deviceId,  String photo, String position, String jobId, String address, String email,
			String idCard, String workPos,String department,String sex,String isduty) {
		Employee employee = new Employee();
		// 鍒ゆ柇鏄惁涓烘眽瀛�
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(name);
		if (m.find()) {
			String pingyin = converterToSpell(name);
			employee.setPingyin(pingyin);
		}

		String employeeId = UUID.randomUUID().toString().trim().replaceAll("-", "");
		employee.setEmployeeId(employeeId);
		employee.setEmployeeName(name);
		employee.setPosition(position);
		employee.setJobId(jobId);
		employee.setAddress(address);
		employee.setEmail(email);
		employee.setWorkPos(workPos);
		if (null != department) {
			Department depart = intelligenceDao.getDepartmentById(department);
			employee.setDepartment(depart);
		}

		employee.setIdCard(idCard);
		if (!"".equals(birth) && null != birth) {
			employee.setBirth(birth);
		}
		employee.setTelphone(contact);
		Company company=intelligenceDao.getCompanyByDeviceId(deviceId);
		employee.setCompany(company);
		if(sex!=null){
			employee.setSex(Integer.parseInt(sex));
		}
		if(isduty!=null){
			employee.setIsDuty(Integer.parseInt(isduty));
		}
		String strangerId=null;
		if(photo!=null){
			employee.setPhotoPath(photo);
			strangerId=photo.substring(photo.lastIndexOf("/")+1,photo.lastIndexOf("."));
		}

		cloudDao.add(employee);
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "103_2",future);
		response.start();
		ServiceDistribution.handleJson103_1(employeeId, strangerId, name, birth, deviceId);
		return employeeId;
	}


	@SuppressWarnings("unchecked")
	public Boolean deleteUser(String deviceId,String id) {
		Employee employee = intelligenceDao.getEmployeeById(id);
		Admin admin = employee.getAdmin();
		cloudDao.delete(employee);
		if(admin!=null){
			cloudDao.delete(admin);
		}
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "105_2",future);
		response.start();
		boolean state=ServiceDistribution.handleJson105_1(deviceId, employee.getEmployeeId());
		return state;

	}

	public Boolean editEmployee(String employeeId, String name, String birth, String contact, 
			String deviceId, String position, String jobId, String address, String email,
			String idCard, String workPos) {
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		employee.setEmployeeName(name);
		employee.setBirth(birth);
		employee.setTelphone(contact);
		employee.setPosition(position);
		employee.setJobId(jobId);
		employee.setAddress(address);
		employee.setEmail(email);
		employee.setIdCard(idCard);
		employee.setWorkPos(workPos);
		cloudDao.update(employee);
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "104_2",future);
		response.start();
		boolean state = ServiceDistribution.handleJson104_1(deviceId, employeeId, name, birth);
		return state;
	}

	public Boolean editPassword(String username, String password) {
		Admin admin = intelligenceDao.getAdminByName(username);
		admin.setPassword(password);
		cloudDao.update(admin);
		return true;
	}

	public Employee getEmployeeById(String id) {
		return intelligenceDao.getEmployeeById(id);
	}

	public List<Employee> getEmployeeByName(String name,String deviceId) {
		return intelligenceDao.getEmployeeByName(name,deviceId);
	}

	/*public Boolean updateEmployee() {
		List<Employee> list = intelligenceDao.getEmployeeList();
		for (Employee employee : list) {
			if (employee.getEmployeeId().length() <= 5) {
				Message message = new Message();
				message.setEmployeeId(employee.getEmployeeId());
				message.setMessage("璇风瓑寰�");
				message.setTime(new Date());
				cloudDao.add(message);

				Message message2 = new Message();
				message2.setEmployeeId(employee.getEmployeeId());
				message2.setMessage("璇风暀瑷�");
				message2.setTime(new Date());
				cloudDao.add(message2);

				Message message3 = new Message();
				message3.setEmployeeId(employee.getEmployeeId());
				message3.setMessage("闂ㄥ彛鏈夊揩閫�");
				message3.setTime(new Date());
				cloudDao.add(message3);

				Message message4 = new Message();
				message4.setEmployeeId(employee.getEmployeeId());
				message4.setMessage("鎮ㄧ殑澶栧崠鍒颁簡");
				message4.setTime(new Date());
				cloudDao.add(message4);

				Message message5 = new Message();
				message5.setEmployeeId(employee.getEmployeeId());
				message5.setMessage("鍙告満姝ｅ湪妤间笅");
				message5.setTime(new Date());
				cloudDao.add(message5);

				Message message6 = new Message();
				message6.setEmployeeId(employee.getEmployeeId());
				message6.setMessage("鎮ㄧ殑澶чゼ绯婂暒锛�");
				message6.setTime(new Date());
				cloudDao.add(message6);
			}
		}
		return true;

	}*/


	public List<Employee> getEmployeeByWhere(String word, String company, String department) {
		return intelligenceDao.getEmployeeByWhere(word, company, department);
	}

	public Boolean excuteCollection(String deviceId) {
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "101_2",future);
		response.start();
		boolean state = ServiceDistribution.handleJson101_1(deviceId);
		return state;
	}

	public String completeCollection(String deviceId) {
		SyncFuture<String> future=AddFuture.setFuture(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "102_2",future);
		response.start();
		//String path="/cloudweb/server/tomcat_intel/webapps/employee_img/1.jpg";
		String path = PhotoMap.getMap(deviceId);
		//PhotoMap.removeMap(deviceId);
		ServiceDistribution.handleJson102_1(deviceId);
		return path;
	}

	/**
	 * 姹夊瓧杞崲浣嶆眽璇叏鎷硷紝鑻辨枃瀛楃涓嶅彉锛岀壒娈婂瓧绗︿涪澶�
	 * 鏀寔澶氶煶瀛楋紝鐢熸垚鏂瑰紡濡傦紙閲嶅綋鍙�:zhongdangcen,zhongdangcan,chongdangcen
	 * ,chongdangshen,zhongdangshen,chongdangcan锛�
	 * 
	 * @param chines
	 *            姹夊瓧
	 * @return 鎷奸煶
	 */
	public static String converterToSpell(String chines) {
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					// 鍙栧緱褰撳墠姹夊瓧鐨勬墍鏈夊叏鎷�
					String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					if (strs != null) {
						for (int j = 0; j < strs.length; j++) {
							pinyinName.append(strs[j]);
							if (j != strs.length - 1) {
								pinyinName.append(",");
							}
						}
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName.append(nameChar[i]);
			}
			pinyinName.append(" ");
		}
		// return pinyinName.toString();
		return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
	}

	/**
	 * 瑙ｆ瀽骞剁粍鍚堟嫾闊筹紝瀵硅薄鍚堝苟鏂规(鎺ㄨ崘浣跨敤)
	 * 
	 * @return
	 */
	private static String parseTheChineseByObject(List<Map<String, Integer>> list) {
		Map<String, Integer> first = null; // 鐢ㄤ簬缁熻姣忎竴娆�,闆嗗悎缁勫悎鏁版嵁
		// 閬嶅巻姣忎竴缁勯泦鍚�
		for (int i = 0; i < list.size(); i++) {
			// 姣忎竴缁勯泦鍚堜笌涓婁竴娆＄粍鍚堢殑Map
			Map<String, Integer> temp = new Hashtable<String, Integer>();
			// 绗竴娆″惊鐜紝first涓虹┖
			if (first != null) {
				// 鍙栧嚭涓婃缁勫悎涓庢娆￠泦鍚堢殑瀛楃锛屽苟淇濆瓨
				for (String s : first.keySet()) {
					for (String s1 : list.get(i).keySet()) {
						String str = s + s1;
						temp.put(str, 1);
					}
				}
				// 娓呯悊涓婁竴娆＄粍鍚堟暟鎹�
				if (temp != null && temp.size() > 0) {
					first.clear();
				}
			} else {
				for (String s : list.get(i).keySet()) {
					String str = s;
					temp.put(str, 1);
				}
			}
			// 淇濆瓨缁勫悎鏁版嵁浠ヤ究涓嬫寰幆浣跨敤
			if (temp != null && temp.size() > 0) {
				first = temp;
			}
		}
		String returnStr = "";
		if (first != null) {
			// 閬嶅巻鍙栧嚭缁勫悎瀛楃涓�
			for (String str : first.keySet()) {
				returnStr += (str + ",");
			}
		}
		if (returnStr.length() > 0) {
			returnStr = returnStr.substring(0, returnStr.length() - 1);
		}
		return returnStr;
	}

	/**
	 * 鍘婚櫎澶氶煶瀛楅噸澶嶆暟鎹�
	 * 
	 * @param theStr
	 * @return
	 */
	private static List<Map<String, Integer>> discountTheChinese(String theStr) {
		// 鍘婚櫎閲嶅鎷奸煶鍚庣殑鎷奸煶鍒楄〃
		List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
		// 鐢ㄤ簬澶勭悊姣忎釜瀛楃殑澶氶煶瀛楋紝鍘绘帀閲嶅
		Map<String, Integer> onlyOne = null;
		String[] firsts = theStr.split(" ");
		// 璇诲嚭姣忎釜姹夊瓧鐨勬嫾闊�
		for (String str : firsts) {
			onlyOne = new Hashtable<String, Integer>();
			String[] china = str.split(",");
			// 澶氶煶瀛楀鐞�
			for (String s : china) {
				Integer count = onlyOne.get(s);
				if (count == null) {
					onlyOne.put(s, new Integer(1));
				} else {
					onlyOne.remove(s);
					count++;
					onlyOne.put(s, count);
				}
			}
			mapList.add(onlyOne);
		}
		return mapList;
	}

	public void test() {
		List<Employee> list = intelligenceDao.getEmployeeList();
		int i=0;
		for (Employee employee : list) {
			List<Department> departments=intelligenceDao.getDepartmentByCompany("3");
			if(i>12){
				i=0;
			}
			employee.setDepartment(departments.get(i));
			cloudDao.update(employee);
			i++;
		}
	}
	
	public List<Admin> searchAdmin(String name,String auth,String deviceId){
		return intelligenceDao.searchAdmin(name, auth,deviceId);
	}
	
	public Boolean editAdmin(String id,String name,String password,String auth){
		Admin admin=intelligenceDao.getAdminById(id);
		admin.setUsername(name);
		admin.setPassword(password);
		admin.setAuthority(Integer.parseInt(auth));
		cloudDao.update(admin);
		return true;
	}
	
	public Boolean deleteAdmin(String id){
		Admin admin=intelligenceDao.getAdminById(id);
		Employee employee=intelligenceDao.getEmployeeByAdmin(id);
		if(null!=employee){
			employee.setAdmin(null);
			cloudDao.update(employee);
		}
		if(null!=admin){
			cloudDao.delete(admin);
		}
		
		
		return true;
	}
	
	public Employee getEmployeeByAdminId(String adminId){
		return intelligenceDao.getEmployeeByAdmin(adminId);
	}
	
	public List<Employee> getAuditPersonList(String deviceId){
		return intelligenceDao.getAuditPersonList(deviceId);
	}
	
	public void updateTemplatePath(String employeeId,String path){
		Employee employee=intelligenceDao.getEmployeeById(employeeId);
		if(employee!=null){
			employee.setTemplatePath(path);
			cloudDao.update(employee);
		}
	}

}
