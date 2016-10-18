/**
 * 
 */

function login() {
	var username = $("#inputUser").val();
	var password = $("#inputPassword").val();
	if (username == "") {
		alert("用户名不能为空，请核对!");
		document.getElementById("inputUser").focus();
		return false;
	} else if (password == "") {
		alert("密码不能为空，请核对!");
		document.getElementById("inputPassword").focus();
		return false;
	} else {
		$.ajax({
			        "type" : 'post',
					"url" : "/intelligentStage-system-manage/rest/admin/login",
					"dataType" : "json",
					"data" : {
						"adminName" : username,
						"password" : password
					},
					success : function(data) {
						if(data.result_code==0){
					    window.location.href="index.html#admin";
						}else if(data.result_code==6){					
								alert("密码错误");					
						}else if(data.result_code==19){
							 alert("用户不存在");
						}
					}
				})
	}
}