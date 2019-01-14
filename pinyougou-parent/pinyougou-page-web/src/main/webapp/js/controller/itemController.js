app.controller('itemController', function ($scope,$http) {


    $scope.specificationItems = {};//记录用户选择的规格

    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }


//用户选择规格 
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;

        searchSku();//查询SKU(用户选择是,价格和标题等内容随着选择的进行变化)
    }
    //判断某规格选项是否被用户选中
    $scope.isSelected = function (name, value) {

        if ($scope.specificationItems[name] == value) {

            return true;
        } else {
            return false;

        }
    }

    $scope.sku = {};//当前选择的SKU

    //加载默认的SKU

    $scope.loadSku = function () {
        $scope.sku = skuList[0];//默认加载第一个
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec)); //深克隆,并把字符串转成对象
    }


    //比较两个对象是否相等
    //比较两个tb_item表的spec值(一个点击的,一个是后台获取的从循环中匹配一个和点击的相同的)
    matchObject = function (map1, map2) {
        //点击spec里面的value值匹配和遍历里面的value值相同的数据,不同就返回false
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            //遍历里面的value值和点击spec里面的value值匹配相同的数据,不同就返回false
            if (map2[k] != map1[k]) {
                return false
            }
        }
        //相同的
        return true;

    }


    //根据规格查询SKU,显示对应SKU除规格外的其他信息,价格,产品名字等
    searchSku = function () {
        //skuList是页面根据SPU获取得到的所有SKU
        for (var i = 0; i < skuList.length; i++) {
            //遍历和用户所选的进行if对比.
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                //把一样的,赋值给SKU也就是默认加载SKU.
                $scope.sku = skuList[i];
                return;
            }
        }
        //如果没有一样的,就把其他信息显示为空类型
        $scope.sku = {id: 0, title: '-----', price: 0};
    }

    //添加商品到购物车
    $scope.addToCart = function () {
        //alert('SKUID:'+$scope.sku.id );
        //添加商品到购物车,参数1:添加的SKU信息 参数2:数量  withCredentials 客户端也需要同意操作cookie(证书)
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id + '&num=' + $scope.num, {'withCredentials': true}).success(
            function (response) {
                if (response.success) {
                    //添加成功则跳转到购物车页面
                    location.href = 'http://localhost:9107/cart.html';
                } else {
                    alert(response.message);
                }
            }
        );
    }


});
