app.controller('cartController', function ($scope, cartService) {

    //查看购物车数据
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    };

    //添加
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();//添加成,刷新页面
                } else {
                    alert(response.message)
                }

            }
        );
    };


    //获取当前用户的地址列表
    $scope.findAddressList = function () {

        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == "1") {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }

            }
        );
    }


    //选中地址

    $scope.selectAddress = function (address) {
        $scope.address = address;
    }


    //判断是否选中地址
    $scope.isSeletedAddress = function (address) {
        if (address == $scope.address) {
            return true;
        } else {
            return false;
        }
    }


    //定义订单表对象
    $scope.order = {paymentType: '1'};//订单对象(设定默认值)

    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }


    $scope.submitOrder = function () {
        //给order对象添加收货地址信息
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                //alert(response.message);
                if (response.success) {
                    //页面跳转
                    if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                        location.href = "pay.html";
                    } else {//如果货到付款，跳转到提示页面
                        location.href = "paysuccess.html";
                    }

                } else {
                    alert(response.message);	//也可以跳转到提示页面
                }

            }
        );
    }
});