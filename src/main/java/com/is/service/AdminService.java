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
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author lishuhuan
 * @date 2016年3月31日 类说明
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
			String idCard, String workPos,String department) {
		Employee employee = new Employee();
		// 判断是否为汉字
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
		/*if (null != department) {
			Department depart = intelligenceDao.getDepartmentById(department);
			employee.setDepartment(depart);
		}
*/
		employee.setIdCard(idCard);
		if (!"".equals(birth) && null != birth) {
			employee.setBirth(birth);
		}
		employee.setTelphone(contact);
		Company company=intelligenceDao.getCompanyByDeviceId(deviceId);
		employee.setCompany(company);

		employee.setPhotoPath(photo+ ".jpg");
		employee.setPhotoStatus("1");

		cloudDao.add(employee);
		String strangerId=photo.substring(photo.lastIndexOf("/")+1);
		ServiceDistribution.handleJson103_1(employeeId, strangerId, name, birth, deviceId);
		CheckResponse response=new CheckResponse(deviceId, "103_2");
		response.start();
		return employeeId;
	}


	public Boolean deleteUser(String id) {
		Employee employee = intelligenceDao.getEmployeeById(id);
		Admin admin = employee.getAdmin();
		cloudDao.delete(employee);
		if(admin!=null){
			cloudDao.delete(admin);
		}
		return true;

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
		return true;
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

	public List<Employee> getEmployeeByName(String name) {
		return intelligenceDao.getEmployeeByName(name);
	}

	/*public Boolean updateEmployee() {
		List<Employee> list = intelligenceDao.getEmployeeList();
		for (Employee employee : list) {
			if (employee.getEmployeeId().length() <= 5) {
				Message message = new Message();
				message.setEmployeeId(employee.getEmployeeId());
				message.setMessage("请等待");
				message.setTime(new Date());
				cloudDao.add(message);

				Message message2 = new Message();
				message2.setEmployeeId(employee.getEmployeeId());
				message2.setMessage("请留言");
				message2.setTime(new Date());
				cloudDao.add(message2);

				Message message3 = new Message();
				message3.setEmployeeId(employee.getEmployeeId());
				message3.setMessage("门口有快递");
				message3.setTime(new Date());
				cloudDao.add(message3);

				Message message4 = new Message();
				message4.setEmployeeId(employee.getEmployeeId());
				message4.setMessage("您的外卖到了");
				message4.setTime(new Date());
				cloudDao.add(message4);

				Message message5 = new Message();
				message5.setEmployeeId(employee.getEmployeeId());
				message5.setMessage("司机正在楼下");
				message5.setTime(new Date());
				cloudDao.add(message5);

				Message message6 = new Message();
				message6.setEmployeeId(employee.getEmployeeId());
				message6.setMessage("您的大饼糊啦！");
				message6.setTime(new Date());
				cloudDao.add(message6);
			}
		}
		return true;

	}*/

	public Boolean updateEmployee(String id, String path) {
		Employee employee = intelligenceDao.getEmployeeById(id);
		if (null != employee) {
			employee.setPhotoPath(path);
			employee.setPhotoStatus("1");
			cloudDao.update(employee);
			return true;
		} else {
			return false;
		}

	}

	public List<Employee> getEmployeeByWhere(String word, String company, String department) {
		return intelligenceDao.getEmployeeByWhere(word, company, department);
	}

	public Boolean excuteCollection(String deviceId) {
		boolean state = ServiceDistribution.handleJson101_1(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "101_2");
		response.start();
		return state;
	}

	public String completeCollection(String deviceId) {
		String path = PhotoMap.getMap(deviceId);
		PhotoMap.removeMap(deviceId);
		ServiceDistribution.handleJson102_1(deviceId);
		CheckResponse response=new CheckResponse(deviceId, "102_2");
		response.start();
		return path;
	}

	/**
	 * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失
	 * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen
	 * ,chongdangshen,zhongdangshen,chongdangcan）
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
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
					// 取得当前汉字的所有全拼
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
	 * 解析并组合拼音，对象合并方案(推荐使用)
	 * 
	 * @return
	 */
	private static String parseTheChineseByObject(List<Map<String, Integer>> list) {
		Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
		// 遍历每一组集合
		for (int i = 0; i < list.size(); i++) {
			// 每一组集合与上一次组合的Map
			Map<String, Integer> temp = new Hashtable<String, Integer>();
			// 第一次循环，first为空
			if (first != null) {
				// 取出上次组合与此次集合的字符，并保存
				for (String s : first.keySet()) {
					for (String s1 : list.get(i).keySet()) {
						String str = s + s1;
						temp.put(str, 1);
					}
				}
				// 清理上一次组合数据
				if (temp != null && temp.size() > 0) {
					first.clear();
				}
			} else {
				for (String s : list.get(i).keySet()) {
					String str = s;
					temp.put(str, 1);
				}
			}
			// 保存组合数据以便下次循环使用
			if (temp != null && temp.size() > 0) {
				first = temp;
			}
		}
		String returnStr = "";
		if (first != null) {
			// 遍历取出组合字符串
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
	 * 去除多音字重复数据
	 * 
	 * @param theStr
	 * @return
	 */
	private static List<Map<String, Integer>> discountTheChinese(String theStr) {
		// 去除重复拼音后的拼音列表
		List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
		// 用于处理每个字的多音字，去掉重复
		Map<String, Integer> onlyOne = null;
		String[] firsts = theStr.split(" ");
		// 读出每个汉字的拼音
		for (String str : firsts) {
			onlyOne = new Hashtable<String, Integer>();
			String[] china = str.split(",");
			// 多音字处理
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
		for (Employee employee : list) {
			String photo = employee.getPhotoPath().substring(employee.getPhotoPath().lastIndexOf("/") + 1);
			photo = "/cloudweb/server/tomcat_intel/webapps/employee_img/" + photo;
			// photo.replaceAll("tomcat_zcl", "tomcat_intel");
			// photo.replace("tomcat_zcl", "tomcat_intel");
			employee.setPhotoPath(photo);
			cloudDao.update(employee);
		}
	}
	
	public List<Admin> searchAdmin(String name,String auth){
		return intelligenceDao.searchAdmin(name, auth);
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
		if(null!=admin){
			cloudDao.delete(admin);
		}
		return true;
	}

}
