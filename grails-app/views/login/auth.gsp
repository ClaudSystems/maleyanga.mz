<html>
<head>
	<title><g:message code="springSecurity.login.title"/></title>
	<meta name="layout" content="kickstart" />

	<g:set var="layout_nomainmenu"		value="${true}" scope="request"/>
	<g:set var="layout_nosecondarymenu" value="${true}" scope="request"/>
</head>

<body>

<section id="login" class="first">
	<div class="row">
		<div class="col-md-3"></div>

		<div class="col-md-6">
			<form role="form" id='loginForm' class='form-horizontal' action='${postUrl}' method='POST'
				  autocomplete='off'>

				<div class="panel-body ${hasErrors(bean: _DemoPageInstance, field: 'name', 'error')} ">
					<h3 style="color:#00aa00"><g:message code="springSecurity.login.header"/></h3>
				</div>

				<div style="background: white"
					 class="panel-body ${hasErrors(bean: _DemoPageInstance, field: 'name', 'error')} ">
					<div class="col-lg-2">
						<label style="background: white" for='username' class="control-label"><g:message
								code="springSecurity.login.username.label"/>:</label>

					</div>

					<div class="col-lg-8">
						<input type='text' class='form-control col-md-4' name='j_username' id='username'/>
					</div>
				</div>

				<div style="background: white"
					 class="panel-body ${hasErrors(bean: _DemoPageInstance, field: 'name', 'error')} ">
					<div class="col-lg-2">
						<label style="background: white" for='password' class="control-label"><g:message
								code="springSecurity.login.password.label"/>:</label>

					</div>

					<div class="col-lg-8">
						<input type='password' class='form-control col-md-4' name='j_password' id='password'/>
					</div>
				</div>

				<div style="background: white" id="remember_me_holder" class="panel-body">
					<label for='remember_me' class="control-label"><g:message
							code="springSecurity.login.remember.me.label"/></label>

					<div class="controls">
						<bs:checkBox class="form-control col-md-4" name="${rememberMeParameter}" value="${hasCookie}"/>
					</div>
				</div>

				<div class="panel-body">
					<input type='submit' id="submit" class="btn btn-success" value='${message(code: "springSecurity.login.button")}'/>
				</div>
		</form>
	</div>
	<div class="col-md-3"></div>
</div>
</section>

<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>

</body>
</html>
