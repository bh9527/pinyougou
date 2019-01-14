app.service('uploadService',function($http){
	
	//上传文件
	this.uploadFile=function(){
		var formdata=new FormData();//html5提供的文件上传类,传送的是二进制文件
		formdata.append('file',file.files[0]);//file 文件上传框的name file.files[0]第一个文件上传框
		
		return $http({
			url:'../upload.do',		
			method:'post',
			data:formdata,//上传的文件
			headers:{ 'Content-Type':undefined },//如不指定默认上传是json类型,undefined则会自动转上传类型
			transformRequest: angular.identity	//固定格式,表单二进制序列化
		});
		
	}
	
	
});