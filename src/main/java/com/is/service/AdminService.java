package com.is.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.constant.ParameterKeys;
import com.is.constant.ResponseCode;
import com.is.dao.CloudDao;
import com.is.dao.IntelligenceDao;
import com.is.map.DeviceService;
import com.is.map.EmployeeFoldMap;
import com.is.map.FutureMap;
import com.is.map.PhotoMap;
import com.is.model.Admin;
import com.is.model.CollectionPhoto;
import com.is.model.Company;
import com.is.model.Department;
import com.is.model.Device;
import com.is.model.Employee;
import com.is.model.Template;
import com.is.util.CommonUtil;
import com.is.util.PasswordUtil;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import static com.is.constant.ParameterKeys.EMPLOYEE_FACE;
import static com.is.constant.ParameterKeys.EMPLOYEE_TEMPLATE;

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
	
	private static Logger logger = Logger.getLogger(AdminService.class);

	public boolean AccountAssignment(String adminName, String password, String auth, String employeeId,
			String deviceId) {
		Admin admin = new Admin();
		String adminId = CommonUtil.generateRandomUUID();
		admin.setAdminId(adminId);
		admin.setUsername(adminName);
		admin.setPassword(PasswordUtil.generatePassword(password));
		admin.setAuthority(Integer.parseInt(auth));
		admin.setDeviceId(deviceId);
		cloudDao.add(admin);
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		employee.setAdmin(admin);
		cloudDao.update(employee);
		return true;

	}

	public Admin getAdminByName(String name) {
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
			if (PasswordUtil.validatePassword(admin.getPassword(), password)) {
				admin.setResponseCode(ResponseCode.SUCCESS);
				return admin;
			} else {
				admin = new Admin();
				admin.setResponseCode(ResponseCode.WRONG_PSW);
				return admin;
			}
		}
	}

	public List<Employee> getEmployeeList(int companyId) {
		return intelligenceDao.getEmployeeList(companyId);
	}

	public List<Company> getCompanyList() {
		return intelligenceDao.getCompanyList();
	}

	public String addEmployee(String name, String birth, String contact, String deviceId, String photo, String position,
			String jobId, String address, String email, String idCard, String workPos, String department, String sex,
			String isduty, String cid) throws InterruptedException, ExecutionException, TimeoutException {
		String employeeId = CommonUtil.generateRandomUUID();
		String strangerId = null;
		if (photo != null) {
			//TODO: 容错
			strangerId = photo.substring(photo.lastIndexOf("/") + 1, photo.lastIndexOf("."));
		}

		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			logger.warn("addEmployee: cannot find channel for device ID "+ deviceId);
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "103_2", future);
		
		String templateId = CommonUtil.generateRandomUUID();
		
		//103-1单发
		ServiceDistribution.handleJson103_1(employeeId, templateId, strangerId, name, birth, deviceId);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "103_2");
		
		if (result != null) {
			String employeeFold=EmployeeFoldMap.getData(employeeId);	
			Admin admin = new Admin();
			String adminId = CommonUtil.generateRandomUUID();
			admin.setAdminId(adminId);
			admin.setAuthority(3);
			admin.setDeviceId(deviceId);
			admin.setUsername(contact);
			admin.setPassword(PasswordUtil.generatePassword("123456"));
			cloudDao.add(admin);

			Employee employee = new Employee();
			Template template = new Template();
			// 鍒ゆ柇鏄惁涓烘眽瀛�
			Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
			Matcher m = p.matcher(name);
			if (m.find()) {
				String pingyin = converterToSpell(name);
				employee.setPingyin(pingyin);
			}
			employee.setEmployeeFold(employeeFold);
			employee.setEmployeeId(employeeId);
			employee.setAdmin(admin);
			employee.setEmployeeName(name);
			employee.setPosition(position);
			employee.setJobId(jobId);
			employee.setAddress(address);
			employee.setDeviceId(deviceId);
			employee.setEmail(email);
			employee.setWorkPos(workPos);
			if (null != department) {
				Department depart = intelligenceDao.getDepartmentById(department);
				employee.setDepartment(depart);
			}

			employee.setIdCard(idCard);
			if (!StringUtil.isNullOrEmpty(birth)) {
				employee.setBirth(birth);
			}
			employee.setTelphone(contact);
			Company company = intelligenceDao.getCompanyByDeviceId(deviceId);
			employee.setCompany(company);
			if (sex != null) {
				employee.setSex(Integer.parseInt(sex));
			}
			if (isduty != null) {
				employee.setIsDuty(Integer.parseInt(isduty));
			}
			if (photo != null) {
				File file = new File(photo);

				String newpath = EMPLOYEE_FACE + deviceId;
				if (!(new File(newpath).isDirectory())) {
					new File(newpath).mkdirs();
				}
				newpath = newpath + "/" + file.getName();
				file.renameTo(new File(newpath));
				employee.setPhotoPath(newpath);
				template.setPhotoPath(newpath);

				Employee photoem = intelligenceDao.getEmployeeByPhotoPath(newpath);
				if (photoem != null) {
					return null;
				}
			}
			
			String path = EMPLOYEE_TEMPLATE + deviceId + "/" + employeeId;
			employee.setTemplatePath(path);
			employee.setEmpVersion(0);
			employee.setTempVersion(0);
			cloudDao.add(employee);

			template.setTemplateId(templateId);
			template.setEmployeeId(employeeId);
			Date javaDate = new Date();
			Timestamp timestamp = new Timestamp(javaDate.getTime());
			template.setCreatedAt(timestamp);
			template.setCreatedBy(System.getProperty("user.name"));
			template.setTemplatePath(path);
			cloudDao.add(template);
			
			if (!(new File(path).isDirectory())) {
				new File(path).mkdirs();
			}
			
			if (!StringUtil.isNullOrEmpty(cid)) {
				CollectionPhoto collectionPhoto = intelligenceDao.getCollectionPhotoById(cid);
				cloudDao.delete(collectionPhoto);
			}
			return employeeId;
		} else {
			return null;
		}

	}
	
	public JSONObject addTemplate(String deviceId, String employeeId, String photo, String cid) throws InterruptedException, ExecutionException, TimeoutException{
		JSONObject jsonObject = new JSONObject();
		String templateId = CommonUtil.generateRandomUUID();
		if(StringUtil.isNullOrEmpty(employeeId)){
			logger.warn("addTemplate: employee ID is null");
			return null;
		}
		
		String strangerId = null;
		if (!StringUtil.isNullOrEmpty(photo)) {
			//TODO: 容错
			strangerId = photo.substring(photo.lastIndexOf("/") + 1, photo.lastIndexOf("."));
		}else{
			logger.warn("addTemplate: photo path is null");
			return null;
		}
		
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		if(employee==null){
			logger.warn("addTemplate: cannot find employee with employee ID " + employeeId);
			return null;
		}
		
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			logger.warn("addTemplate: cannot find channel for device ID "+deviceId);
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "109_2", future);
		
		//单发109-1
		ServiceDistribution.handleJson109_1(employeeId, templateId, strangerId, employee.getEmployeeName(), ctx);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "109_2");
		
		if(result!=null){
			Template template = new Template();
			template.setTemplateId(templateId);
			template.setEmployeeId(employeeId);
			
			if (photo != null) {
				File file = new File(photo);

				String newpath = EMPLOYEE_FACE + deviceId;
				if (!(new File(newpath).isDirectory())) {
					new File(newpath).mkdirs();
				}
				newpath = newpath + "/" + file.getName();
				file.renameTo(new File(newpath));
				template.setPhotoPath(newpath);

				//该照片已经存在
				Employee photoem = intelligenceDao.getEmployeeByPhotoPath(newpath);
				if (photoem != null) {
					logger.warn("addTemplate: employee existed with photo path "+newpath);
					return null;
				}
			}
			
			String path = EMPLOYEE_TEMPLATE + deviceId + "/" + employeeId;
			template.setTemplatePath(path);
			
			//未设置模板路径
			Date javaDate = new Date();
			Timestamp timestamp = new Timestamp(javaDate.getTime());
			template.setCreatedAt(timestamp);
			template.setCreatedBy(System.getProperty("user.name"));
			cloudDao.add(template);
			updateTemplateVersion(employeeId);
			
			
			//删除采集模板
			if (!StringUtil.isNullOrEmpty(cid)) {
				CollectionPhoto collectionPhoto = intelligenceDao.getCollectionPhotoById(cid);
				cloudDao.delete(collectionPhoto);
			}
			jsonObject.put(ParameterKeys.EMPLOYEE_ID, employeeId);
			jsonObject.put(ParameterKeys.TEMPLATE_ID, templateId);
			return jsonObject;
		}else{
			logger.warn("addTemplate: no response from future");
			return null;
		}
	}

	public Boolean deleteUser(String deviceId, String employeeId)
			throws InterruptedException, ExecutionException, TimeoutException {
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "105_2", future);
		List<String> list=intelligenceDao.getDeviceListAll(deviceId);
		for(String did:list){
			ServiceDistribution.handleJson105_1(did, employee.getEmployeeId());
		}
			
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "105_2");
		if (result != null) {
			//删除模板
			List<Template> templates = intelligenceDao.getTemplateListByEmployeeId(employeeId);
			for(Template t : templates){
				if(t == null){
					continue;
				}
				
				String templatePhotoPath = t.getPhotoPath();
				if(!StringUtil.isNullOrEmpty(templatePhotoPath)) {
				File photo = new File(templatePhotoPath);
					if(photo.exists()){
						photo.delete();
					}
				}
				
				deleteFolder(t.getTemplatePath(), null);
				cloudDao.delete(t);
			}
			
			File photo = new File(employee.getPhotoPath());
			if(photo.exists()){
				photo.delete();
			}
			
			Admin admin = employee.getAdmin();
			cloudDao.delete(employee);
			if (admin != null) {
				cloudDao.delete(admin);
			}
			return true;
		} else {
			return false;
		}

	}
	
	public boolean deleteTemplate(String deviceId, String employeeId, String photoPath) throws InterruptedException, ExecutionException, TimeoutException {
		Template template = intelligenceDao.getTemplateByPhotoPath(photoPath);
		if(template==null){
			logger.warn("deleteTemplate: cannot find template by photo path "+photoPath);
			return false;
		}
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			logger.warn("deleteTemplate: cannot find channel for device ID: "+deviceId);
			return false;
		}
		
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		
		FutureMap.addFuture(ctx.channel().id().asLongText() + "105_4", future);
		List<String> list=intelligenceDao.getDeviceListAll(deviceId);
		for(String did : list){
			ServiceDistribution.handleJson105_3(employeeId, template.getTemplateId(), employee.getEmployeeName(), did);
		}
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "105_4");
		if(result!=null){
			//删除模板中的头像文件
			String templatePhotoPath = template.getPhotoPath();
			if(!StringUtil.isNullOrEmpty(templatePhotoPath) && !templatePhotoPath.equals(employee.getPhotoPath())) {
				File file = new File(templatePhotoPath);
				if(file.exists()) {
					file.delete();
				}
			}
			
			//删除模板文件夹
			deleteFolder(template.getTemplatePath(), template.getTemplateId());
			cloudDao.delete(template);
			updateTemplateVersion(employeeId);
			return true;
		}else{
			logger.warn("deleteTemplate: no response from future");
			return false;
		}
	}

	public Boolean editEmployee(String employeeId, String name, String birth, String contact, String deviceId,
			String position, String jobId, String address, String email, String idCard, String workPos, String sex,
			String path, String departmentId, String adminName) {
		try {
			Employee employee = intelligenceDao.getEmployeeById(employeeId);
			String strangerId = null;
			if (path != null) {
				employee.setPhotoPath(path);
				strangerId = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			} else {
				String opath = employee.getPhotoPath();
				strangerId = opath == null ? null : opath.substring(opath.lastIndexOf("/") + 1, opath.lastIndexOf("."));
			}

			SyncFuture<String> future = new SyncFuture<>();
			ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
			if (ctx == null) {
				return null;
			}
			List<String> list=intelligenceDao.getDeviceListAll(deviceId);
			for(String did:list){
				ServiceDistribution.handleJson104_1(did, employeeId, name, birth, strangerId);
			}
			FutureMap.addFuture(ctx.channel().id().asLongText() + "104_2", future);
			ServiceDistribution.handleJson104_1(deviceId, employeeId, name, birth, strangerId);
			String result = future.get(6, TimeUnit.SECONDS);
			FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "104_2");
			if (result != null) {
				employee.setEmployeeName(name);
				if (!StringUtil.isNullOrEmpty(birth)) {
					employee.setBirth(birth);
				}

				if (!StringUtil.isNullOrEmpty(adminName)) {
					Admin admin = employee.getAdmin();
					admin.setUsername(adminName);
					cloudDao.update(admin);
					employee.setAdmin(admin);
				}

				employee.setTelphone(contact);
				employee.setPosition(position);
				employee.setJobId(jobId);
				Department depart = intelligenceDao.getDepartmentById(departmentId);
				employee.setDepartment(depart);
				employee.setAddress(address);
				employee.setEmail(email);
				employee.setIdCard(idCard);
				employee.setSex(Integer.parseInt(sex));
				employee.setWorkPos(workPos);
				employee.setEmpVersion(employee.getEmpVersion()+1);

				cloudDao.update(employee);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public Boolean editPassword(String username, String password) {
		Admin admin = intelligenceDao.getAdminByName(username);
		admin.setPassword(PasswordUtil.generatePassword(password));
		cloudDao.update(admin);
		return true;
	}

	public Employee getEmployeeById(String id) {
		return intelligenceDao.getEmployeeById(id);
	}

	public List<Employee> getEmployeeByName(String name, int companyId) {
		return intelligenceDao.getEmployeeByName(name, companyId);
	}

	public List<Employee> getEmployeeByWhere(String word, String department, int companyId) {
		return intelligenceDao.getEmployeeByWhere(word, department, companyId);
	}
	
	public Boolean excuteCollection(String deviceId) {
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		Future<String> sFuture = FutureMap.getFutureMap(ctx.channel().id().asLongText() + "101_2");
		if (sFuture != null) {
			SyncFuture<String> future = AddFuture.setFuture(deviceId, "101_2");
			CheckResponse response = new CheckResponse(deviceId, "101_2", future);
			response.start();
		}
		boolean state = ServiceDistribution.handleJson101_1(deviceId);
		return state;
	}

	public String completeCollection(String deviceId) {
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		Future<String> sFuture = FutureMap.getFutureMap(ctx.channel().id().asLongText() + "101_2");
		if (sFuture != null) {
			SyncFuture<String> future = AddFuture.setFuture(deviceId, "102_2");
			CheckResponse response = new CheckResponse(deviceId, "102_2", future);
			response.start();
		}
		String path = PhotoMap.getMap(deviceId);
		ServiceDistribution.handleJson102_1(deviceId);
		return path;
	}

	public static String converterToSpell(String chines) {
		StringBuffer pinyinName = new StringBuffer();
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
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
		return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
	}

	private static String parseTheChineseByObject(List<Map<String, Integer>> list) {
		Map<String, Integer> first = null;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> temp = new Hashtable<String, Integer>();
			if (first != null) {
				for (String s : first.keySet()) {
					for (String s1 : list.get(i).keySet()) {
						String str = s + s1;
						temp.put(str, 1);
					}
				}
				if (temp != null && temp.size() > 0) {
					first.clear();
				}
			} else {
				for (String s : list.get(i).keySet()) {
					String str = s;
					temp.put(str, 1);
				}
			}
			if (temp != null && temp.size() > 0) {
				first = temp;
			}
		}
		String returnStr = "";
		if (first != null) {
			for (String str : first.keySet()) {
				returnStr += (str + ",");
			}
		}
		if (returnStr.length() > 0) {
			returnStr = returnStr.substring(0, returnStr.length() - 1);
		}
		return returnStr;
	}

	private static List<Map<String, Integer>> discountTheChinese(String theStr) {
		List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> onlyOne = null;
		String[] firsts = theStr.split(" ");
		for (String str : firsts) {
			onlyOne = new Hashtable<String, Integer>();
			String[] china = str.split(",");
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

	public List<Admin> searchAdmin(String name, String auth, String deviceId) {
		return intelligenceDao.searchAdmin(name, auth, deviceId);
	}

	public Boolean editAdmin(String id, String name, String password, String auth) {
		Admin admin = intelligenceDao.getAdminById(id);
		admin.setUsername(name);
		if (!StringUtil.isNullOrEmpty(password)) {
			admin.setPassword(PasswordUtil.generatePassword(password));
		}
		if (!StringUtil.isNullOrEmpty(auth)) {
			admin.setAuthority(Integer.parseInt(auth));
		}
		cloudDao.update(admin);
		return true;
	}

	public Boolean adminManage(String deviceId, String companyId, String username, String password) {
		try {
			Admin admin = intelligenceDao.getAdminByName(username);
			if(admin == null){//当该管理员不存在时添加管理员账号
				admin = new Admin();
				String adminId = CommonUtil.generateRandomUUID();
				admin.setAdminId(adminId);
				admin.setAuditAuth(1);
				admin.setAuthority(0);
				admin.setDeviceId(deviceId);
				admin.setPassword(PasswordUtil.generatePassword(password));
				admin.setUsername(username);
				cloudDao.add(admin);

				int compId = Integer.parseInt(companyId);
				Company companyById = intelligenceDao.getCompanyById(compId);
				if(companyById == null){//新公司
					companyById = new Company();
					//公司ID为自增
					companyById.setAdminId(adminId);
					cloudDao.add(companyById);
				}
				
				Company companyByDeviceId = intelligenceDao.getCompanyByDeviceId(deviceId);
				if(companyByDeviceId==null){//新设备
					Device device = new Device();
					String id = CommonUtil.generateRandomUUID();
					device.setId(id);
					device.setCompany(companyById);
					device.setDeviceId(deviceId);
					Date javaDate = new Date();
					Timestamp timestamp = new Timestamp(javaDate.getTime());
					device.setCreatedAt(timestamp);
					device.setCreatedBy(System.getProperty("user.name"));
					cloudDao.add(device);
				}
				
				Employee employee = new Employee();
				String employeeId = CommonUtil.generateRandomUUID();
				employee.setEmployeeId(employeeId);
				employee.setAdmin(admin);
				employee.setEmployeeName(username);
				employee.setDeviceId(deviceId);
				employee.setCompany(companyById);
				cloudDao.add(employee);
				return true;
			}else{
				System.err.println("管理员用户" + username + "已存在");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean deleteAdmin(String id) {
		Admin admin = intelligenceDao.getAdminById(id);
		Employee employee = intelligenceDao.getEmployeeByAdmin(id);
		if (null != employee) {
			employee.setAdmin(null);
			cloudDao.update(employee);
		}
		if (null != admin) {
			cloudDao.delete(admin);
		}

		return true;
	}

	public Employee getEmployeeByAdminId(String adminId) {
		return intelligenceDao.getEmployeeByAdmin(adminId);
	}

	public List<Employee> getAuditPersonList(String deviceId) {
		return intelligenceDao.getAuditPersonList(deviceId);
	}

	public List<String> getPhotoByTemplate(String employeeId) {
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		String path = null;
		if (employee != null) {
			path = employee.getTemplatePath();
		}
		File file = new File(path);
		List<String> list = new ArrayList<>();
		if (file.isDirectory()) {
			File[] all = file.listFiles();
			if (all != null) {
				for (File photo : all) {
					String photoPath = photo.getAbsolutePath();
					list.add(photoPath);
				}
			}
		}
		return list;
	}

	public Boolean resetPassword(String adminId) {
		Admin admin = intelligenceDao.getAdminById(adminId);
		if (admin != null) {
			admin.setPassword(PasswordUtil.generatePassword("123456"));
			cloudDao.update(admin);
			return true;
		} else {
			return false;
		}

	}

	public Boolean checkPhoneNumber(String phone) {
		String employee = intelligenceDao.getEmployeeByMobile(phone);
		if (employee == null) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean editAdminPassword(String oldPassword, String newPassword, String adminId) {
		Admin admin = intelligenceDao.getAdminById(adminId);
		boolean state = PasswordUtil.validatePassword(admin.getPassword(), oldPassword);
		if (state) {
			admin.setPassword(PasswordUtil.generatePassword(newPassword));
			cloudDao.update(admin);
		}
		return state;
	}

	public void updateEmployeeFoldAndSync(String employeeId, String templateId) {
		Employee employee=intelligenceDao.getEmployeeById(employeeId);
		List<String> list=intelligenceDao.getDeviceList(employee.getDeviceId());
		if(list!=null){
			for(String deviceId:list){
				ServiceDistribution.handleJson118_1(deviceId,employee.getEmployeeId(), employee.getEmployeeFold(),employee.getEmployeeName(),
						templateId, employee.getBirth(),employee.getPhotoPath());
			}
		}
	}
	
	public List<Employee> getEmployeeListByDeviceId(String deviceId){
		return intelligenceDao.getEmployeeListByDeviceId(deviceId);
	}
	
	public List<Template> getTemplateListByEmployeeId(String employeeId){
		return intelligenceDao.getTemplateListByEmployeeId(employeeId);
	}
	
	private void updateTemplateVersion(String employeeId){
		Employee employee = intelligenceDao.getEmployeeById(employeeId);
		employee.setTempVersion(employee.getTempVersion()+1);
		cloudDao.update(employee);
	}
	
	private static boolean deleteFolder(String folder, String templateId){
		//TODO: 文件夹必须只有一级目录
		if(StringUtil.isNullOrEmpty(folder)){
			logger.warn("template folder is null");
			return false;
		}
		File tempPath = new File(folder);
		if(tempPath.isDirectory()){
			File[] files = tempPath.listFiles();
			boolean ret = true;
			for(File f : files){
				if(templateId==null || f.getName().startsWith(templateId))
					ret = ret && f.delete();
			}
			ret = ret && tempPath.delete();
			return ret;
		}else{
			return false;
		}
	}
//	public void addFirstTemplate() {
//		List<Employee> employeeList = cloudDao.findByHql("from Employee e");
//		for(Employee e : employeeList){
//			if(StringUtil.isNullOrEmpty(e.getPhotoPath())){
//				continue;
//			}
//			
//			String templateId = CommonUtil.generateRandomUUID();
//			String templatePath = e.getTemplatePath();
//			Template t = new Template();
//			t.setTemplateId(templateId);
//			t.setEmployeeId(e.getEmployeeId());
//			t.setPhotoPath(e.getPhotoPath());
//			t.setTemplatePath(templatePath);
//			
//			if(!StringUtil.isNullOrEmpty(templatePath)){
//				File folder = new File(templatePath);
//				if(folder.isDirectory()){
//					File[] files=folder.listFiles();
//					for(File file : files){
//						file.renameTo(new File(templatePath+File.separator + templateId+"_"+file.getName()));
//					}
//				}
//			}
//			cloudDao.add(t);
//		}
//		
//	}
	
}
