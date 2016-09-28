/**
 * 
 */

var table;
$(function() {
	$.ajax({
		type : "post",
		url : "/intelligentStage-system-manage/rest/clock/getClockList",
		datatype : "json",

		success : function(data) {
			var info = data.data;
			table = $('#companylist').DataTable({
				"ordering" : true,
				"bProcessing" : true,
				"bPaginate" : true,
				"pagingType" : "full_numbers",
				"bFilter" : true,
				"bLengthChange" : true,
				"bInfo" : true,
				"bSort" : true,
				"retrieve" : true,
				"destroy" : true,
				"oLanguage" : {
					"sZeroRecords" : "没有数据",
					"oPaginate" : {
						"sFirst" : "首页",
						"sPrevious" : "前一页",
						"sNext" : "后一页",
						"sLast" : "尾页"
					}
				},

				"columnDefs" : [ {
					"targets" : [ 5 ],
					"render" : function(data, type, full) {
						if (data == 1) {
							return "<td>迟到</td>";
						}
						if (data == 2) {
							return "<td>早退</td>";
						}
						if (data == 3) {
							return "<td>迟到并早退</td>";
						}
						if (data == 4) {
							return "<td>忘打卡</td>";
						} else {
							return "<td>正常</td>";
						}
					}

				}, ],
				data : info,
				columns : [ {
					data : 'crId'
				}, {
					data : 'employee.employeeName'
				}, {
					data : "employee.company.companyName"
				}, {
					data : 'startClock'
				}, {
					data : 'endClock'
				}, {
					data : 'state'
				} ]
			});

		}
	})

})

function clockfilter() {
	var name = $("#searchinput").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	var rule = $("#rule").val();

	$.ajax({
		type : "post",
		url : "/intelligentStage-system-manage/rest/clock/getClockByWhere",
		datatype : "json",
		data : {
			"name" : name,
			"startTime" : startTime,
			"endTime" : endTime,
			"rule":rule
		},
		beforeSend : function(){
			$("#hidebg").css('display','block');
			$('#loading').show();
		},
		complete : function(XMLHttpRequest,status){
			$("#hidebg").css('display','');
			$('#loading').hide();
			if(status=='timeout'){
					//ajaxTimeOut.abort(); 
					alert("网络超时，请刷新后重试");
			}
		},
		success : function(data) {
			var info = data.data;
			$('#companylist').dataTable().fnClearTable();
			$('#companylist').dataTable().fnDestroy();
			table = $('#companylist').DataTable({
				"ordering" : true,
				"bProcessing" : true,
				"bPaginate" : true,
				"pagingType" : "full_numbers",
				"bFilter" : true,
				"bLengthChange" : true,
				"bInfo" : true,
				"bSort" : true,
				"retrieve" : true,
				"destroy" : true,
				"oLanguage" : {
					"sZeroRecords" : "没有数据",
					"oPaginate" : {
						"sFirst" : "首页",
						"sPrevious" : "前一页",
						"sNext" : "后一页",
						"sLast" : "尾页"
					}
				},

				"columnDefs" : [ {
					"targets" : [ 5 ],
					"render" : function(data, type, full) {
						if (data == 1) {
							return "<td>迟到</td>";
						}
						if (data == 2) {
							return "<td>早退</td>";
						}
						if (data == 3) {
							return "<td>迟到并早退</td>";
						}
						if (data == 4) {
							return "<td>忘打卡</td>";
						} else {
							return "<td>正常</td>";
						}
					}

				}, ],
				data : info,
				columns : [ {
					data : 'crId'
				}, {
					data : 'employee.employeeName'
				}, {
					data : "employee.company.companyName"
				}, {
					data : 'startClock'
				}, {
					data : 'endClock'
				}, {
					data : 'state'
				} ]
			});

		}
	})

}


function addClock(){
	$('#clockPan').dialog({
    	width:700,
    	height:600
    });
	$("#companyName option:gt(0)").remove();
	$("#employeeName option:gt(0)").remove();
	$("#morningClock").val("");
	$("#nightClock").val("");
	
	
	$.ajax({
		"type" : 'get',
		"url" : "/intelligentStage-system-manage/rest/admin/getCompanyList",
		"dataType" : "json",
		
		success : function(data) {
			var companyList=data.data;
			$.each(companyList,function(k,p){
				var option = "<option value='" + p.companyId + "'>" + p.companyName + "</option>";
				$("#companyName").append(option);
			});
			$("#companyName").change(function(){
				var companyId=$(this).val();
				$("#employeeName option:gt(0)").remove();
				$.ajax({
					"type" : 'post',
					"url" : "/intelligentStage-system-manage/rest/clock/getEmployeeByCompany",
					"dataType" : "json",
					"data" : {
						"companyId" : companyId
					},
					success : function(data) {
						var employeeList=data.data;
						$.each(employeeList,function(k,p){
							var option = "<option value='" + p.employeeId + "'>" + p.employeeName + "</option>";
							$("#employeeName").append(option);
						});
					}
				})
			})
		}
	})
}

function comfirm(){
	var employeeId=$("#employeeName").val();
	var morningClock=$("#morningClock").val();
	var nightClock=$("#nightClock").val();
	var date;
	if (employeeId == 0) {
		alert("员工不能为空，请核对!");
		return false;
	} else if (morningClock == "" && nightClock == "") {
		alert("时间不能为空，请核对!");
		return false;
	}
	else{
		
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/clock/getClockByDate",
			"dataType" : "json",
			"data" : {
				"employeeId" : employeeId,
				"morningClock" : morningClock,
				"nightClock":nightClock
				

			},
			success : function(data) {
				var clock=data.data;
				if(clock==null){
					addcon(employeeId,morningClock,nightClock);
				}
				else{
					var morning=clock.startClock;
					var night=clock.endClock;
					var crId=clock.crId;
					if((morning!="" && morningClock!="" && morning!=null && morningClock!=null) || (night!="" && nightClock!="" && night!=null && nightClock!=null)){
						
						var r=confirm("存在重复数据，是否覆盖原数据？");
						if (r==true)
						  {
							if(morningClock==""){
								updateClock(crId,employeeId, morning, nightClock)
							}
							else if(nightClock==""){
								updateClock(crId,employeeId, morningClock, night)
							}
							else {
								updateClock(crId,employeeId, morningClock, nightClock)
							}
						  }
						else
						  {
						    
						  }
						
						
					}
					else{
						if(morningClock==""){
							updateClock(crId,employeeId, morning, nightClock)
						}
						else{
							updateClock(crId,employeeId, morningClock, night)
						}
					}
				}
			}
		})
		
	}
	
}

function addcon(el,mc,nc){
	$.ajax({
		"type" : 'post',
		"url" : "/intelligentStage-system-manage/rest/clock/addClock",
		"dataType" : "json",
		"data" : {
			"employeeId" : el,
			"morningClock" : mc,
			"nightClock":nc
			

		},
		success : function(data) {
			if(data.result_code==0){
                alert("添加成功");
                window.location.href="clock.html";
            }else{
                alert("添加失败");
            }
		}
	})
}

function updateClock(cr,el,mc,nc){
	$.ajax({
		"type" : 'post',
		"url" : "/intelligentStage-system-manage/rest/clock/updateClock",
		"dataType" : "json",
		"data" : {
			"crId":cr,
			"employeeId" : el,
			"morningClock" : mc,
			"nightClock":nc
			

		},
		success : function(data) {
			if(data.result_code==0){
                alert("更新成功");
                window.location.href="clock.html";
            }else{
                alert("更新失败");
            }
		}
	})
}