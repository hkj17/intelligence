/**
 * 
 */

$(function() {
	$.ajax({
		"type" : 'post',
		"url" : "/intelligentStage-system-manage/rest/clock/getClockPhoto",
		"dataType" : "json",
		success : function(data) {
			var photo=data.data;

			
			$.each(photo,function(k,p){
				var img = "<img src='" + p.photo + "' style='height:150px;width:300px;'>";
				$("#photoList").append(img);
			});
		}
	})
})