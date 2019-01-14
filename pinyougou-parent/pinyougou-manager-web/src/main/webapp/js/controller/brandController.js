app.controller('brandController', function($scope, $controller,brandService) {


    $controller('baseController',{$scope:$scope});//继承

    /*查询品牌列表  查询结果给一个函数*/
    $scope.findAll = function() {
        /* 从本html路径地址到controller层获取得到数据 */
        brandService.findAll().success(
            /*response代表获取得到的数据  */
            function(response) {
                /* 把获取得到的数据赋值给本页面list */
                $scope.list = response
            });
    }


    //分页
    $scope.findPage = function(page, size) {
        brandService.findPage(page, size).success(function(response) {
            $scope.list = response.rows;//显示当前页数据
            $scope.paginationConf.totalItems = response.total;//更新总记录数
        });
    }

    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=brandService.update( $scope.entity ); //修改
        }else{
            serviceObject=brandService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }

    //查询单实体,回显
    $scope.findOne = function(id) {
        brandService.findOne(id).success(

            function(response) {
                $scope.entity = response;
            })

    }




    //删除
    $scope.dele = function() {
        if (confirm('确定要删除吗？')) {
            brandService.dele($scope.selectIds).success(function(response) {
                if (response.success) {
                    $scope.reloadList();//刷新
                } else {
                    alert(response.message);
                }
            });
        }

    };



    $scope.searchEntity={};
    //条件查询
    $scope.search=function(page,size){

        brandService.search(page,size,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );

    }


});

