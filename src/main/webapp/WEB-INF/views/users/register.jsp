<%@ page session="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="playtogether" tagdir="/WEB-INF/tags"%>

<playtogether:layout pageName="registroPlaytogether">
	<jsp:body>
	<div class="thirteen">
	<h1>
		Nuevo usuario
	</h1>
	</div>
	<div class="crearMeeting">
	<form:form modelAttribute="usuario" class="form-horizontal"
				id="add-usuario-form">
		
			<playtogether:inputField label="*Nombre" name="name" />
			<playtogether:inputField label="*Correo" name="correo" />
			<playtogether:inputField label="*Telefono" name="phone" />
			<playtogether:inputField label="*Usuario" name="user.username" />
			<playtogether:inputField label="*Contrase�a" name="user.password" />
			<playtogether:localDate label="*Fecha nacimiento" name="birthdate"
					id="birthdate" />
			
		
		<div class="form-group">
			<button class="botonMeeting" type="submit">
			<b>Registrarme</b>
			</button>
		</div>
		<br>
			
			</form:form>
		</div>
	
	 </jsp:body>
</playtogether:layout>