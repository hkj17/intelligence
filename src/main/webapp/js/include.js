/**
 * 李树桓
 * 用于密码修改
 */
$(function(){
    // dialog init
    $('#passwordEdit').dialog({
        autoOpen: false,
        width: 500,
        height: 400,
        title:"密码修改",
        
    });
    
});

function editPass(){
	$('#passwordEdit').dialog('open');
	$("#passwordf").contents().find("#oldPassword").val("");
	$("#passwordf").contents().find("#newPassword").val("");
	$("#passwordf").contents().find("#confirmPassword").val("");
}

function editPassword(){
	var oldPassword=$("#oldPassword").val();
	var newPassword=$("#newPassword").val();
	var confirmPassword=$("#confirmPassword").val();
	if (oldPassword == "") {
		alert("旧密码不能为空，请核对!");
		document.getElementById("oldPassword").focus();
		return false;
	} else if (newPassword == "") {
		alert("新密码不能为空，请核对!");
		document.getElementById("newPassword").focus();
		return false;
	} else if (confirmPassword != newPassword) {
		alert("两次输入的密码不一致，请重输!");
		return false;
	} else{
		$.ajax({
			"type" : 'post',
			"url" : "/intelligentStage-system-manage/rest/admin/confirm",
			"dataType" : "json",
			"data" : {
				"oldPassword":oldPassword,
			},
			success : function(data) {
				if(data.result_code==0){
					changePassword(newPassword);
	            }else{
	                alert(data.data);
	            }
			}
		})
	}
}


function changePassword(password){
	$.ajax({
		"type" : 'post',
		"url" : "/intelligentStage-system-manage/rest/admin/editPassword",
		"dataType" : "json",
		"data" : {
			"password" : password,
		},
		success : function(data) {
			if(data.result_code==0){
                alert("修改成功"); 
                parent.$("#passwordEdit").dialog('close');
            }else{
                alert("修改失败");
            }
		}
	})
}