
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>tcc transacton biz sample</title>
</head>
<body>
<div class="page">
	列表:
	<hr/>
	<#list userLst?sort_by("userId") as item>
		<b>${item.userName}</b> &nbsp; <a href="del/user/${item.userId}">删除</a><br/>
	</#list>
	<hr/>
	共${userLst?size}条<br/>
</div>
</body>
</html>