<#-- "page" is the name of the macro which will be used in any ftl files that import this template -->
<#macro page title="SparkJavaTest">
<html>
<head>
    <title>${title}</title>
</head>
<body>
<#-- This processes the enclosed content:  -->
    <#nested>
</body>
</html>
</#macro>