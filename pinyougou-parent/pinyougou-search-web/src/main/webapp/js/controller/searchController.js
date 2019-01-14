app.controller('searchController', function ($scope, searchService,$location) {

    //定义搜索对象的结构 category:商品分类 ,brand:品牌分类 ,spec:规格, price:价格,pageNo:当前页,pageSize:每页条数 ,sort:升序降序 , sortField:排序字段
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 10,
        'sort': '',
        'sortField': ''
    };
    //搜索
    $scope.search = function () {
        //搜索选框里面输入的值是字符串,所以这里做一个转换,不管当前页是什么类型,直接转成int类型
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;

                //构建分页栏
                buildPageLabel();
                // $scope.searchMap.pageNo=1;//点击查询后初始化页码,避免查询还停留在之前的页面.

            }
        );
    };


    //构建分页栏
    buildPageLabel = function () {
        //构建分页栏
        $scope.pageLabel = [];
        var firstPage = 1;//开始页码
        var lastPage = $scope.resultMap.totalPages;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点

        if ($scope.resultMap.totalPages > 5) {  //如果页码数量大于5

            if ($scope.searchMap.pageNo <= 3) {//如果当前页码小于等于3 ，显示前5页
                lastPage = 5;
                $scope.firstDot = false;//前面没点
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {//显示后5页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;//后边没点
            } else {  //显示以当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }


        //构建页码
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //添加搜索项,也就是改变searchMap的值
    $scope.addSearchItem = function (key, value) {

        if (key == 'category' || key == 'brand' || key == 'price') {//用户点击的商品或品牌分类
            $scope.searchMap[key] = value;//给对应的key赋值(点那个就是那个值)

        } else {//用户点击的规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search()
    };


    $scope.removeSearchItem = function (key) {

        if (key == 'category' || key == 'brand' || key == 'price') {//用户点击x的商品或品牌分类
            $scope.searchMap[key] = '';//给对应的key赋值(点那个就是那个值)

        } else {//用户点击X的规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search()
    };


    //分页查询         pageNo点击页
    $scope.queryByPage = function (pageNo) {
//如果当前页是第一页,或者当前页大于总页数,则不执行任何操作
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;//把页码条件发到后端
        $scope.search();//查询结果
    }


    //判断当前页是否为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }

    //判断当前页是否为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }


    //排序查询
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;//给查询map封装排序字段
        $scope.searchMap.sort = sort;//给查询map封装排序方式

        $scope.search();//查询方法
    }


    //判断关键字是否包含品牌

    $scope.keywordsIsBrand = function () {

        //循环传过来的关键字与品牌进行对比
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            //判断传入map中传入的关键字里面是否品牌关键字,如索引位置有则会大于等于0,否则等于-1 包含返回true
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >=0) {
                return true;
            }
        }
        return false;
    }


    //加载关键字
    $scope.loadkeywords=function(){

        //$location.search()['keywords'] 首页传过来的值  返回值.[key]
        $scope.searchMap.keywords= $location.search()['keywords'];

        $scope.search();//查询
    }


});