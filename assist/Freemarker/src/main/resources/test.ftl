<html>

<head>
    <title>呆毛</title>
    <meta charset="UTF-8">
</head>

<body>

<#--我是一个注释,不会输出到页面-->

<!--html注释-->
<#--引入其他模板-->
<#include "head.ftl">
<#--基本-->
${name},你好.${message}
<#--当前页直接写入数据-->
<#assign linkman="周先生"><br>
联系人:${linkman}<br>

<#--if-->
<#if success=true>
    恭喜你成功越狱
<#else>
    呵呵,失败了,吊毛

</#if>



---- 买水果咯----<br>
<#--list-->
<#list goodsList as good>
<#--索引,固定写法-->
  ${good_index}  商品名称: ${good.name} 价格:${good.price}<br>

</#list>
<br>
<#--获取集合大小-->
一共${goodsList?size}条记录

<#--json字符串转换成对象-->
    <#--定义json-->
<#assign text="{'name':'张三'}"/><br>
<#--需要转换那个json对象-->
<#assign data=text?eval><br>
<#--取出值-->
${data.name}<br>


<#--日期-->
日期:${today?date}<br>
时间:${today?time}<br>
日期+时间:${today?datetime}<br>
日期格式化:${today?string("yyyy年MM月")}<br>


<#--int转字符串-->
积分:${point}<br>
字符串积分:${point?c}<br>

<#--空值运算符-->
<#if aaa??> <br>
aaa存在 ${aaa}<br>
<#else>
aaa不存在<br>

</#if><br>
<#--简写-->
${bbb!'bbb不存在'}<br>


</body>
</html>