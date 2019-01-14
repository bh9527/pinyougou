var app=angular.module('pinyougou',[]);
/*加载服务用引号'$sce',使用则不需要*/
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {/*后台传到前台时需要被过滤的内容*/
        return $sce.trustAsHtml(data);/*返回过滤后的内容($sce.trustHtml信任html)*/
    }

}]);



