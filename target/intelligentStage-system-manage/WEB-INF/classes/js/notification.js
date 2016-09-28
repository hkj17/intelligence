/**
 * 
 */

var table;
$(function() {
	$.ajax({
		type : "post",
		url : "/intelligentStage-system-manage/rest/notification/getNotifyList",
		datatype : "json",

		success : function(data) {
			var info = data.data;
			table = $('#newslist').DataTable({
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

				"aoColumnDefs" : [ {
					"aTargets": [2],
					"defaultContent": "<button name='edit' title='编辑'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/edit.jpg'></button>"+
					"<button name='delete' title='删除'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/delete.png'></button>"
				},{
					"aTargets":[0],
					"mRender": function(data, type, full){
						return "<a name='moue' title='查看详情' style='cursor:pointer;'>"+data+"</a>";
					}
				
				},{
					"aTargets":[1],
					"mRender": function(data, type, full){
						return "<a style='cursor:pointer;font-size:10px;'>"+data+"</a>";
					}
				
				},
				],
				data : info,
				columns : [ {
					data : 'noteTitle'
				}, {
					data : 'noteTime'
				} ,{
					data : ''
				} ]
			});

		}
	})
	
	
	$('#newslist tbody').on( 'click', "a", function () {
		 $('#newsShow').dialog({
		    	width:700,
		    	height:600
		    });
		 var data=table.row( $(this).parents('tr') ).data();
		 document.getElementById('newsTitle').innerText=data.noteTitle;
		 document.getElementById('newstext').innerText=data.noteText;
		 document.getElementById('person').innerText=data.noteAuthor;
		 document.getElementById('editTime').innerText=data.noteTime;
	})
	
	
	$('#newslist tbody').on( 'click', "button[name='delete']", function (){
		var data=table.row( $(this).parents('tr') ).data();
		$('#superbox3').show();
		$('#hidebg').css("display","block");
		$('#superdel3').on('click',function(){
		     var noteId=data.noteId;
	    	$.ajax({
				"type" : 'post',
				"url" : "/intelligentStage-system-manage/rest/notification/deleteNews",
				"dataType" : "json",
				"data" : {
					"noteId" : noteId,
					

				},
				success : function(data) {
					if(data.result_code==0){
		                alert("删除成功");
		                window.location.href="notification.html";
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
})





function addNews(){
	 $('#news').dialog({
	    	width:700,
	    	height:600
	    });
}

function confirm(){
	var name=$("#author").val();
	var title=$("#title").val();
	var text=$("#newsContent").val();
	if (name == "") {
		alert("发布人不能为空，请核对!");
		document.getElementById("author").focus();
		return false;
	} else if (title == "") {
		alert("标题不能为空，请核对!");
		document.getElementById("title").focus();
		return false;
	} else if (text == "") {
		alert("内容不能为空，请核对!");
		document.getElementById("newsContent").focus();
		return false;
	}else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/notification/addnews",
			"dataType" : "json",
			"data" : {
				"name" : name,
				"title" : title,
				"text":text
				

			},
			success : function(data) {
				if(data.result_code==0){
	                alert("添加成功");
	                window.location.href="notification.html";
	            }else{
	                alert("添加失败");
	            }
			}
		})
	}
}

var tables;
var num=0;
function appoint(){
	$.ajax({
		type : "post",
		url : "/intelligentStage-system-manage/rest/notification/getAppointList",
		datatype : "json",

		success : function(data) {
			var info = data.data;
			tables = $('#applist').DataTable({
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

				"aoColumnDefs" : [ {
					"aTargets": [4],
					"defaultContent": "<button name='editA' title='编辑'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/edit.jpg'></button>"+
					"<button name='deleteA' title='删除'  style='cursor:pointer;color:#009ACD; background-color:transparent;border:none;outline:none;'><img src='./images/delete.png'></button>"
				},{
					"aTargets":[2],
					"mRender": function(data, type, full){
						if(data==0){
							return "<a name='typeA' title='查看详情' style='cursor:pointer;'>快递</a>";
						}
						else{
							return "<a name='typeA' title='查看详情' style='cursor:pointer;'>预约</a>";
						}
					}
				
				},{
					"aTargets":[1],
					"mRender": function(data, type, full){
						
						return "<a name='typeA' title='查看详情' style='cursor:pointer;'>"+data+"</a>";
					}
				},{
					"aTargets":[0],
					"mRender": function(data, type, full){
						num+=1;
						return num;
						
					}
				
				},
				],
				data : info,
				columns : [ {
					data : ''
				}, {
					data : 'employee.employeeName'
				} ,{
					data : 'type'
				},{
					data : 'time'
				} ]
			});

		}
	})
	
	$('#applist tbody').on( 'mousemove', "tr", function () {
		this.style.backgroundColor="#f3f3f1";
	})
	
	$('#applist tbody').on( 'mouseout', "tr", function () {
		this.style.backgroundColor="#FFFFFF";
	})
	
	$('#applist tbody').on( 'click', "a[name='typeA']", function () {
		var data=tables.row( $(this).parents('tr') ).data();
		 $('#appointInfo').dialog({
		    	width:500,
		    	height:400
		    });
		 document.getElementById('apptext').innerText=data.things;
		 document.getElementById('appTime').innerText=data.time;
	})
	
	$('#applist tbody').on( 'click', "button[name='deleteA']", function () {
		var data=tables.row( $(this).parents('tr') ).data();
		$('#superbox').show();
		$('#hidebg').css("display","block");
		$('#superdel').on('click',function(){
		     var apId=data.apId;
	    	$.ajax({
				"type" : 'post',
				"url" : "/intelligentStage-system-manage/rest/notification/deleteAppoint",
				"dataType" : "json",
				"data" : {
					"apId" : apId,
					

				},
				success : function(data) {
					if(data.result_code==0){
		                alert("删除成功");
		                window.location.href="notification.html";
		            }else{
		                alert("删除失败");
		            }
				}
			})
	    })
	})
	
	$('.shut4').on('click', function() {
		$("#superbox").hide();
		$("#hidebg").css('display', '');
	});
	$('#superdela').on('click',function(){
		$("#superbox").css('display','none');
		$("#hidebg").css('display','none');
		
    });
}



function addAppoint(){
	 $('#appointAdd').dialog({
	    	width:500,
	    	height:500
	 });
	 $.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/notification/getEmployeeByAdmin",
			"dataType" : "json",
			
			success : function(data) {
				var employeeList=data.data;
				$.each(employeeList,function(k,p){
					var option = "<option value='" + p.employeeId + "'>" + p.employeeName + "</option>";
					$("#employee").append(option);
				});
			}
		})
}

function addAppointment(){
	var employeeId=$("#employee").val();
	var appType=$("#appType").val();
	var text=$("#addText").val();
	if (employeeId == "") {
		alert("员工不能为空，请核对!");
		document.getElementById("employee").focus();
		return false;
	} else if (text == "") {
		alert("内容不能为空，请核对!");
		document.getElementById("addText").focus();
		return false;
	}else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/notification/addAppointment",
			"dataType" : "json",
			"data" : {
				"employeeId" : employeeId,
				"appType" : appType,
				"text":text
				

			},
			success : function(data) {
				if(data.result_code==0){
	                alert("添加成功");
	                window.location.href="notification.html";
	            }else{
	                alert("添加失败");
	            }
			}
		})
	}
	
	
}