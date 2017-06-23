package com.is.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.crypto.Data;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.is.map.DeviceService;
import com.is.map.FutureMap;
import com.is.model.ClockAbnormal;
import com.is.model.ClockAppeal;
import com.is.model.ClockRecordDept;
import com.is.model.ClockTime;
import com.is.model.ClockRecord;
import com.is.model.ClockRecordSelect;
import com.is.model.Company;
import com.is.model.Employee;
import com.is.model.EmployeeClock;
import com.is.system.dao.CloudDao;
import com.is.system.dao.IntelligenceDao;
import com.is.util.CommonUtil;
import com.is.websocket.AddFuture;
import com.is.websocket.CheckResponse;
import com.is.websocket.ServiceDistribution;
import com.is.websocket.SyncFuture;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lishuhuan
 * @date 2016年4月6日 类说明
 */
@Transactional
@Component("clockService")
public class ClockService {

	@Autowired
	private IntelligenceDao intelligenceDao;

	@Autowired
	private CloudDao cloudDao;

	public List<ClockRecord> getClockList() throws ParseException {
		List<ClockRecord> clockList = intelligenceDao.getClockList();
		/*List<ClockRecord> newClock = new ArrayList<>();
		for (int i = 0; i < clockList.size(); i++) {
			ClockRecord clockRecord = clockList.get(i);
			if (clockRecord.getState() == null || "".equals(clockRecord.getState())) {
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
				Company company = intelligenceDao.getCompanyByEmployee(clockRecord.getEmployeeId());
				if (null == company) {
					continue;
				}
				String start = company.getTimeWork();
				String end = company.getTimeRest();
				Date startTime = sdf.parse(start);
				Date endTime = sdf.parse(end);

				if (clockRecord.getStartClock() == null || "".equals(clockRecord.getStartClock())
						|| clockRecord.getEndClock() == null || "".equals(clockRecord.getEndClock())) {
					clockRecord.setState("4");
					newClock.add(clockRecord);
					continue;
				}

				Date startT = sdf.parse(clockRecord.getStartClock().substring(11, 19));
				Date enDate = sdf.parse(clockRecord.getEndClock().substring(11, 19));

				if (startT.getTime() > startTime.getTime() && enDate.getTime() < endTime.getTime()) {
					clockRecord.setState("3");
					newClock.add(clockRecord);
					continue;
				}
				if (startT.getTime() > startTime.getTime()) {
					clockRecord.setState("1");
					newClock.add(clockRecord);
					continue;
				}
				if (enDate.getTime() < endTime.getTime()) {
					clockRecord.setState("2");
					newClock.add(clockRecord);
					continue;
				} else {
					clockRecord.setState("0");
					newClock.add(clockRecord);
				}

			} else {
				newClock.add(clockRecord);
			}
		}*/

		return clockList;
	}

	public List<ClockRecordSelect> getClockByWhere(String department, String user, String stratClock, String endClock,
			String rule, String companyId) {
		return intelligenceDao.getClockByWhere(department, user, stratClock, endClock, rule, companyId);
	}

	public List<Employee> getEmployeeByCompany(String companyId) {
		return intelligenceDao.getEmployeeByCompany(Integer.parseInt(companyId));
	}

	public Boolean addClock(String employeeId, String morningClock, String nightClock) {
		ClockRecord clockRecord = new ClockRecord();
		clockRecord.setEmployeeId(employeeId);
		if (!"".equals(morningClock) && null != morningClock) {
			clockRecord.setStartClock(morningClock);
		}
		if (!"".equals(nightClock) && null != nightClock) {
			clockRecord.setEndClock(nightClock);
		}
		cloudDao.add(clockRecord);
		return true;
	}

	public Boolean clockTimeAppeal(String deviceId, String employeeId, String firstClock, String lastClock,
			String appealReason, String appealContent, String auditPersonId, String appealTime) {
		ClockAppeal clockAppeal = new ClockAppeal();
		String id = CommonUtil.generateRandomUUID();
		clockAppeal.setId(id);
		clockAppeal.setEmployeeId(employeeId);
		clockAppeal.setFirstClock(firstClock);
		clockAppeal.setLastClock(lastClock);
		clockAppeal.setAppealReason(appealReason);
		clockAppeal.setAppealContent(appealContent);
		clockAppeal.setAuditPersonId(auditPersonId);
		clockAppeal.setAppealTime(appealTime);
		clockAppeal.setDeviceId(deviceId);
		cloudDao.add(clockAppeal);
		return true;
	}

	public Boolean deleteClockTimeAppeal(String appealId) {
		ClockAppeal clockAppeal = intelligenceDao.getClockAppealById(appealId);
		cloudDao.delete(clockAppeal);
		return true;
	}

	public Boolean checkHandClock(String clockId, String result, String deviceId) {
		ClockAbnormal abnormal = intelligenceDao.getHandClockById(clockId);
		if (result.equals("1")) {
			try {
				addClocknormal(deviceId, abnormal.getEmployeeId(), abnormal.getClockTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cloudDao.delete(abnormal);
		return true;
	}

	public List<ClockAppeal> getClockTimeAppeal(String employeeId) {
		return intelligenceDao.getClockTimeAppealByEmployee(employeeId);

	}

	public List<ClockAppeal> getClockAuditList(String auditId) {
		return intelligenceDao.getClockAuditList(auditId);
	}

	public ClockRecord getClockByMc(String id, String morningClock) {
		return intelligenceDao.getClockByMc(id, morningClock);
	}

	public ClockRecord getClockByNc(int id, String nightClock) {
		return intelligenceDao.getClockByNc(id, nightClock);
	}

	public Boolean updateClock(String crId, String employeeId, String morningClock, String nightClock) {
		ClockRecord clockRecord = new ClockRecord();
		// Employee employee=intelligenceDao.getEmployeeById(employeeId);
		clockRecord.setEmployeeId(employeeId);
		if (!"".equals(morningClock) && null != morningClock) {
			clockRecord.setStartClock(morningClock);
		}
		if (!"".equals(nightClock) && null != nightClock) {
			clockRecord.setEndClock(nightClock);
		}
		clockRecord.setCrId(Integer.parseInt(crId));
		cloudDao.merge(clockRecord);
		return true;
	}

	public Boolean addClockAbnormal(String deviceId, String employeeId, String time)
			throws InterruptedException, ExecutionException, TimeoutException {
		SyncFuture<String> future = new SyncFuture<>();
		ChannelHandlerContext ctx = DeviceService.getSocketMap(deviceId);
		if (ctx == null) {
			return null;
		}
		FutureMap.addFuture(ctx.channel().id().asLongText() + "110_2", future);
		ServiceDistribution.handleJson110_1(deviceId, employeeId, time);
		String result = future.get(6, TimeUnit.SECONDS);
		FutureMap.removeFutureMap(ctx.channel().id().asLongText() + "110_2");
		if (result != null) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean addClocknormal(String deviceId, String id, String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ClockRecord clockRecord = getClockByMc(id, time.substring(0, 10));
		if (null == clockRecord) {
			addClock(id, time, null);
		} else if (sdf.parse(clockRecord.getStartClock()).getTime()>sdf.parse(time).getTime()) {
			if(clockRecord.getEndClock()==null){
				updateClock(String.valueOf(clockRecord.getCrId()), id, time, clockRecord.getStartClock());
			}
			else{
				updateClock(String.valueOf(clockRecord.getCrId()), id, time, clockRecord.getEndClock());
			}
		} else {
			int crId = clockRecord.getCrId();
			String morningClock = clockRecord.getStartClock();
			String nightClock = clockRecord.getEndClock();
			if (nightClock == null) {
				updateClock(String.valueOf(crId), id, morningClock, time);
			} else {

				Date timenew = sdf.parse(time);
				Date timeold = sdf.parse(nightClock);
				if (timenew.getTime() > timeold.getTime()) {
					updateClock(String.valueOf(crId), id, morningClock, time);
				}

			}
		}
		ClockTime clockTime = new ClockTime();
		clockTime.setClockTime(time);
		clockTime.setDeviceId(deviceId);
		clockTime.setEmployeeId(id);
		cloudDao.add(clockTime);
		return true;

	}

	public List<ClockTime> getClockPhoto() {
		return intelligenceDao.getClockPhoto();
	}

	public void insertAbonormalClockPhoto(String employeeId, String path, String deviceId) {
		ClockAbnormal clockAbnormal = new ClockAbnormal();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String id = CommonUtil.generateRandomUUID();
		clockAbnormal.setId(id);
		clockAbnormal.setEmployeeId(employeeId);
		clockAbnormal.setClockTime(sdf.format(new Date()));
		clockAbnormal.setPhotoPath(path);
		clockAbnormal.setDeviceId(deviceId);
		cloudDao.add(clockAbnormal);
	}

	public List<ClockRecord> getClockByEmployee(String id) {
		return intelligenceDao.getClockByEmployee(id);
	}

	public List<ClockTime> getDetailClock(String employeeId, String time) {
		return intelligenceDao.getDetailClock(employeeId, time);
	}

	public List<ClockAbnormal> getHandClockList(String startTime, String endTime, String deviceId) {
		return intelligenceDao.getHandClockList(startTime, endTime, deviceId);
	}

	public List<ClockRecordDept> getClockByDepartmentData(String company_id,
			String departmentId, String date) {
		// TODO Auto-generated method stub
		return intelligenceDao.getClockByDepartmentData(company_id, departmentId, date);
	}

	public List<EmployeeClock> getClockByEmployeeData(String employee_id,String company_id,String date) {
		// TODO Auto-generated method stub
		return intelligenceDao.getClockByEmployeeData(employee_id,company_id, date);
	}

	public List<EmployeeClock> getClockByEmployeeDataKey(String key,String company_id, String date) {
		// TODO Auto-generated method stub
		return intelligenceDao.getClockByEmployeeDataKey(key,company_id, date);
	}

	public HSSFWorkbook export(List<Map<String, String>> list) {
		// TODO Auto-generated method stub
		return intelligenceDao.export(list);
	}

}
