<%--
  Created by IntelliJ IDEA.
  User: Claudino
  Date: 11/10/2015
  Time: 20:47
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="kickstart"/>
    <title>Relatório de Créditos</title>

</head>

<body>

<g:render template="/_menu/menurelatorios"/>
<sec:ifNotGranted roles="RELATORIOS_CREDITOS">
    <g:render template="/layouts/acessoNegado"/>
</sec:ifNotGranted>
<sec:access expression="hasRole('RELATORIOS_CREDITOS')">
    <z:body/>

</sec:access>

</body>
</html>