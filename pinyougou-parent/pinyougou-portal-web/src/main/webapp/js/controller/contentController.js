app.controller('contentController',function ($scope,contentService) {

    $scope.contentList=[];//广告列表
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                //返回的值存入到数组中.把索引1给了轮播图广告
                $scope.contentList[categoryId]=response;
            }
        );
    };
    

    //主页传递参数到搜索页
    $scope.search=function(){
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})