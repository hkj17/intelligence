/**
 * 用户界面设计 李树桓 2016年4月1日
 */

var table;
$(function() {
	$
			.ajax({
				type : "get",
				url : "/intelligentStage-system-manage/rest/admin/getUserList",
				datatype : "json",
				/*
				 * beforeSend : function(){ $('#loadingalert').dialog({
				 * width:300 }); }, complete : function(){
				 * 
				 * $('#loadingalert').dialog('close');
				 * $("#alert_message").innerHTML="数据加载中请稍候。。。" },
				 */
				success : function(data) {
					var info = data.data;
					table = $('#userlist')
							.DataTable(
									{
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

										"columnDefs" : [
												{
													"targets" : [ 11 ],
													"render" : function(data,
															type, full) {
														if(full.admin.authority==2){
															return "<button name='edit' title='编辑' style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/edit.jpg'></button>";
														}
														else{
															return "<button name='edit' title='编辑' style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/edit.jpg'></button>"
															+ "<button name='delete' title='删除' style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/delete.png'></button>";
														}
														
													}
													
												},
												{
													"targets" : [ 0 ],
													"render" : function(data,
															type, full) {
														return '<input type="checkbox" name="check" value="'
																+ data
																+ '"  />';
													}

												},
												{
													"targets" : [ 3 ],
													"render" : function(data,
															type, full) {
														if (data == 1) {
															return "<td>男</td>";
														} else if(data == 2) {
															return "<td>女</td>";
														} else{
															return "<td></td>";
														}
													}
												},
												{
													"targets" : [ 9 ],
													"render" : function(data,
															type, full) {
														if (data == 1) {
															return "<td>超级管理员</td>";
														}
														if (data == 2) {
															return "<td>公司管理员</td>";
														}
														if (data == 3) {
															return "<td>员工</td>";
														} else {
															return "<td>产品设备</td>";
														}
													}
												},
												{
													"bSearchable" : true,
													"aTargets" : [ 1, 2, 5, 7,
															8 ]
												} ],
										data : info,
										columns : [ {
											data : 'employeeId'
										}, {
											data : 'admin.username'
										}, {
											data : "employeeName"
										}, {
											data : 'sex'
										}, {
											data : 'birth'
										}, {
											data : 'telphone'
										}, {
											data : 'entryTime'
										}, {
											data : 'wechat'
										}, {
											data : 'company.companyName'
										}, {
											data : 'admin.authority'
										}, {
											data : 'content'
										}, ]
									});

				}

			})

	$.ajax({
		"type" : 'get',
		"url" : "/intelligentStage-system-manage/rest/admin/getCompanyList",
		"dataType" : "json",

		success : function(data) {
			var companyList = data.data;
			$.each(companyList, function(k, p) {
				var option = "<option value='" + p.companyId + "'>"
						+ p.companyName + "</option>";
				$("#company").append(option);
				$("#editcompany").append(option);
			});
		}
	})

	$("#photo").change(function() {
		var objUrl = getObjectURL(this.files[0]);
		if (objUrl) {
			$("#showPhoto").attr("src", objUrl);
		}
	});

	var options = {
		beforeSubmit : showthis,
		success : function(data) {
			if (data.result_code == 0) {
				alert("添加成功");
				window.location.href = "homePage.html";
			} else {
				alert("添加失败"+data.data);
			}
		}
	}

	$("#picForm").ajaxForm(options);
	
	
	$('#userlist tbody').on( 'click', "button[name='delete']", function (){
		var data=table.row( $(this).parents('tr') ).data();
		$('#superbox3').show();
		$('#hidebg').css("display","block");
		$('#superdel3').on('click',function(){
		     var employeeId=data.employeeId;
	    	$.ajax({
				"type" : 'post',
				"url" : "/intelligentStage-system-manage/rest/admin/deleteUser",
				"dataType" : "json",
				"data" : {
					"employeeId" : employeeId,
					

				},
				success : function(data) {
					if(data.result_code==0){
		                alert("删除成功");
		                window.location.href="homePage.html";
		            }else{
		                alert("删除失败");
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
	
	
	$('#userlist tbody').on( 'click', "button[name='edit']", function (){
		var data=table.row( $(this).parents('tr') ).data();
		$('#editEmployee').dialog({
			width : 700,
			height : 600
		});	
		$("#emid").val(data.employeeId);
		$("#editname").val(data.employeeName);
		$("#editsex").find('option[value="'+data.sex+'"]').attr("selected",true);
		$("#editbirth").val(data.birth);
		$("#editcontact").val(data.telphone);
		$("#editentryTime").val(data.entryTime);
		$("#editwechat").val(data.wechat);
		$("#editcompany").find('option[value="'+data.company.companyId+'"]').attr("selected",true);
		$("#editcontent").val(data.content); 
	 
    })

})

function editEmployee(){
	var employeeId=$("#emid").val();
	var name =$("#editname").val();
	var sex = $("#editsex").val()
	var birth =$("#editbirth").val();
	var contact = $("#editcontact").val();
	var entryTime = $("#editentryTime").val();
	var wechat = $("#editwechat").val();
	var company = $("#editcompany").val();
	var content =$("#editcontent").val();
	if (name == "") {
		alert("姓名不能为空，请核对!");
		document.getElementById("editname").focus();
		return false;
	}  else if (company == "不限") {
		alert("公司不能为空，请核对!");
		document.getElementById("company").focus();
		return false;
	}else if (contact == "") {
		alert("联系方式不能为空，请核对!");
		document.getElementById("editcontact").focus();
		return false;
	}else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/admin/editEmployee",
			"dataType" : "json",
			"data" : {
				"employeeId" : employeeId,
				"name" : name,
				"sex":sex,
				"birth":birth,
				"contact":contact,
				"entryTime":entryTime,
				"wechat":wechat,
				"company":company,
				"content":content
				

			},
			success : function(data) {
				if(data.result_code==0){
	                alert("编辑成功");
	                window.location.href="homePage.html";
	            }else{
	                alert("编辑失败");
	            }
			}
		})
	}
}

function showthis() {
	var adminName = $("#adminName").val();
	var name = $("#name").val();
	var password = $("#password").val();
	var confirm = $("#confirm").val();
	var sex = $("#sex").val();
	var birth = $("#birth").val();
	var contact = $("#contact").val();
	var entryTime = $("#entryTime").val();
	var wechat = $("#wechat").val();
	var company = $("#company").val();
	var content = $("#content").val();
	var file=$("#photo").val();
	if (adminName == "") {
		alert("用户名不能为空，请核对!");
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
	} else if (company == "不限") {
		alert("公司不能为空，请核对!");
		document.getElementById("company").focus();
		return false;
	}/*else if (file == "") {
		alert("请上传图片");
		return false;
	} */else if (contact == "") {
		alert("联系方式不能为空，请核对!");
		document.getElementById("contact").focus();
		return false;
	}
}

function getObjectURL(file) {
	var url = null;
	if (window.createObjectURL != undefined) { // basic
		url = window.createObjectURL(file);
	} else if (window.URL != undefined) { // mozilla(firefox)
		url = window.URL.createObjectURL(file);
	} else if (window.webkitURL != undefined) { // webkit or chrome
		url = window.webkitURL.createObjectURL(file);
	}
	return url;
}

function add() {
	$('#addemployee').dialog({
		width : 700,
		height : 600
	});	
	$("#adminName").val("");
	$("#name").val("");
	$("#password").val("");
	$("#confirm").val("");
	$("#birth").val("");
	$("#contact").val("");
	$("#entryTime").val("");
	$("#wechat").val("");
	$("#content").val("");
	$("#photo").val("");
	$("#showPhoto").attr('src',""); 
}


function userfilter() {
	$('#userlist').DataTable().search($('#searchinput').val()).draw();
}
