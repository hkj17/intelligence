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
							//user_level = resp.result_code;
						    var date=new Date(); 
			                /*date.setTime(date.getTime()+10*60*1000); //设置date为当前时间+？分，记得修改，调试时候会很烦
			                document.cookie="query=user_level; expires="+date.toGMTString(); //将date赋值给expires  
		*/				    window.location.href="homePage.html";



						}else if(data.result_code==6){
							
								alert("密码错误");
							
						}else if(data.result_code==19){
							 alert("用户不存在");
						}
					}
				})
	}
}