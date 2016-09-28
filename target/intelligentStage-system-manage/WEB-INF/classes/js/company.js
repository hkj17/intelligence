/**
 * 公司界面设计
 * 李树桓
 * 2016年4月5日
 */

var table;
$(function() {
	$.ajax({
		type: "get",
		url: "/intelligentStage-system-manage/rest/admin/getCompanyList",
		datatype: "json",
		
		success: function (data) {
			var info = data.data;
			 table = $('#companylist').DataTable({
				  "ordering":true,
				  "bProcessing": true,
				  "bPaginate": true,
				  "pagingType": "full_numbers",
				  "bFilter": true,
				  "bLengthChange": true,
				  "bInfo": true,
				  "bSort": true,
				  "retrieve":true,
				  "destroy": true,
				  "oLanguage": {
					  "sZeroRecords": "没有数据",
					  "oPaginate": {
						"sFirst": "首页",
						"sPrevious": "前一页",
						"sNext": "后一页",
						"sLast": "尾页"
					}
				},

				"columnDefs": [
					{
					"targets": [7],
					"defaultContent": "<button name='edit' title='编辑'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/edit.jpg'></button>"+
					"<button name='delete' title='删除'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/delete.png'></button>"
				},
					{ "bSearchable": false, "aTargets": [0,2,3,4,5,6]}
				],
				data:info,
				columns: [
                    {data: 'companyId'},      
					{data: 'companyName'},
					{data: "address"},
					{data: 'contact'},
					{data: 'admin.username'},
					{data: 'timeWork'},
					{data: 'timeRest'}
				]
			});
			
		}
	})
	
	$('#companylist tbody').on( 'click', "button[name='edit']", function (){
		var data=table.row( $(this).parents('tr') ).data();
		$('#editCompany').dialog({
			width : 700,
			height : 600
		});	
		$("#comid").val(data.companyId);
		$("#editComanyName").val(data.companyName);
		$("#editAddress").val(data.address);
		$("#editStartTime").val(data.timeWork);
		$("#editEndTime").val(data.timeRest);
		
	})
	
	
	$('#companylist tbody').on( 'click', "button[name='delete']", function (){
		var data=table.row( $(this).parents('tr') ).data();
		$('#superbox3').show();
		$('#hidebg').css("display","block");
		$('#superdel3').on('click',function(){
		     var companyId=data.companyId;
	    	$.ajax({
				"type" : 'post',
				"url" : "/intelligentStage-system-manage/rest/clock/getEmployeeByCompany",
				"dataType" : "json",
				"data" : {
					"companyId" : companyId,
					

				},
				success : function(data) {
					if(data.result_code==0){
						var emp=data.data;
						if(emp.length==0){
							deleteEmp(companyId);
						}
						else{
							alert("请先删除该公司的员工.");
			                window.location.href="company.html";
						}
						
		            }else{
		                alert("数据获取失败");
		            }
				}
			})
	    })
	     
	 
    })
    $('.shut4').on('click', function() {
		$("#superbox3").hide();
		$("#hidebg").css('display', '');
	});
	$('#superdel4').on('click',function(){
		$("#superbox3").css('display','none');
		$("#hidebg").css('display','none');
		
    });
	
	
})


function deleteEmp(e){
	$.ajax({
		"type" : 'post',
		"url" : "/intelligentStage-system-manage/rest/company/deleteCompany",
		"dataType" : "json",
		"data" : {
			"companyId" : e,
		},
		success : function(data) {
			if(data.result_code==0){
                alert("删除成功");
                window.location.href="company.html";
            }else{
                alert("删除失败");
            }
		}
	})
}


function editConpany(){
	var companyId=$("#comid").val();
	var companyName = $("#editComanyName").val();
	var address = $("#editAddress").val();
	var startTime = $("#editStartTime").val();
	var endTime = $("#editEndTime").val();
	if (companyName == "") {
		alert("公司名不能为空，请核对!");
		document.getElementById("editComanyName").focus();
		return false;
	} else if (address == "") {
		alert("地址不能为空，请核对!");
		document.getElementById("editAddress").focus();
		return false;
	} else if (startTime == "") {
		alert("规定上班时间不能为空，请核对!");
		document.getElementById("editStartTime").focus();
		return false;
	} else if (endTime == "") {
		alert("规定下班时间不能为空，请核对!");
		document.getElementById("editEndTime").focus();
		return false;
	} else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/company/editCompany",
			"dataType" : "json",
			"data" : {
				"companyId":companyId,
				"companyName" : companyName,
				"address" : address,
				"startTime":startTime,
				"endTime":endTime
			},
			success : function(data) {
				if(data.result_code==0){
	                alert("编辑成功");
	                window.location.href="company.html";
	            }else{
	                alert("编辑失败");
	            }
			}
		})
	}
}

function confirm(){
	var companyName = $("#companyName").val();
	var address = $("#address").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	var adminName = $("#adminName").val();
	var password = $("#password").val();
	var confirm = $("#confirm").val();
	var name = $("#name").val();
	var contact = $("#contact").val();
	if (companyName == "") {
		alert("公司名不能为空，请核对!");
		document.getElementById("companyName").focus();
		return false;
	} else if (address == "") {
		alert("地址不能为空，请核对!");
		document.getElementById("address").focus();
		return false;
	} else if (startTime == "") {
		alert("规定上班时间不能为空，请核对!");
		document.getElementById("startTime").focus();
		return false;
	} else if (endTime == "") {
		alert("规定下班时间不能为空，请核对!");
		document.getElementById("endTime").focus();
		return false;
	} else if (adminName == "") {
		alert("姓名不能为空，请核对!");
		document.getElementById("adminName").focus();
		return false;
	} else if (password == "") {
		alert("密码不能为空，请核对!");
		document.getElementById("password").focus();
		return false;
	} else if (password != confirm) {
		alert("两次密码输入不一致，请重输!");
		document.getElementById("confirm").focus();
		return false;
	} else if (name == "") {
		alert("姓名不能为空，请核对!");
		document.getElementById("name").focus();
		return false;
	} else if (contact == "") {
		alert("联系方式不能为空，请核对!");
		document.getElementById("contact").focus();
		return false;
	} else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/company/addCompany",
			"dataType" : "json",
			"data" : {
				"companyName" : companyName,
				"address" : address,
				"startTime":startTime,
				"endTime":endTime,
				"adminName":adminName,
				"password":password,
				"name":name,
				"contact":contact
				

			},
			success : function(data) {
				if(data.result_code==0){
	                alert("添加成功");
	                window.location.href="company.html";
	            }else{
	                alert("添加失败");
	            }
			}
		})
	}
}


function addCompany(){
	$('#addCompany').dialog({
    	width:700,
    	height:600
    });
	$("#companyName").val("");
	$("#address").val("");
	$("#startTime").val("");
	$("#endTime").val("");
	$("#adminName").val("");
	$("#password").val("");
	$("#confirm").val("");
	$("#name").val("");
	$("#contact").val("");
}

function companyfilter(){
	$('#companylist').DataTable().search($('#searchinput').val()).draw();
}