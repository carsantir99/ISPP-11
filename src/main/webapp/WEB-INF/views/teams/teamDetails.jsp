<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="playtogether" tagdir="/WEB-INF/tags"%>

<playtogether:layout pageName="users">

	<body>
		<div class="cardtitle">
			<h1>
				<strong>Detalles del equipo ${team.name}</strong>
			</h1>
			<br />
		</div>
		
		<c:if test="${leave && championship.matches.size() != 0}">
			<div class="alert alert-danger" style="margin: 2% 20% 5% 20%">
				<p style="color: black; font-size: 20px; font-weight: bolder;">El torneo ha comenzado, no se puede abandonar el equipo.</p>
			</div>
		</c:if>
		
		<c:if test="${loggedUserIsNotTheMeetingCreator}">
			<div class="alert alert-danger" style="margin: 0% 20% 5% 20%">
				<p style="color: black; font-size: 20px; font-weight: bolder;">No
					tienes permisos para hacer esto.</p>
			</div>
		</c:if>
		
		<div class="cardlist">
			<c:if test="${userToDeleteIsTeamOwner}">
				<div class="alert alert-danger" style="margin: 0% 20% 5% 20%">
					<p>No se puede eliminar al creador del equipo</p>
				</div>
			</c:if>
      <c:out value="${eliminado}" />
			<c:if test="${loggedUserIsNotTheTeamOwner}">
				<div class="alert alert-danger" style="margin: 0% 20% 5% 20%">
					<p>No tienes permisos para hacer esto.</p>
				</div>
			</c:if>
			<table id="championshipTable" class="table ">

				<h2>
					Lista de componentes del equipo (Nº participantes:
					<c:out value="${team.participants.size()}" />
					)
				</h2>
				<thead>
					<tr class="rowtable">
						<th class="guiz-awards-header-title" style="width: 20%;">Nombre</th>
						<th class="guiz-awards-header-title" style="width: 20%;">Nombre
							de usuario</th>
						<th class="guiz-awards-header-title" style="width: 20%;">Detalles
							del jugador</th>
					</tr>
				</thead>
				<c:forEach items="${team.participants}" var="participant">
					<tr class="rowtable">
						<td><c:out value="${participant.name}" /></td>
						<td><c:out value="${participant.user.username}" /></td>
						<td><spring:url value="/usuarios/{userId}" var="userDetails">
								<spring:param name="userId" value="${participant.id}" />
							</spring:url>
							<div class="boto">
								<a href="${fn:escapeXml(userDetails)}">Ver detalles</a>
							</div></td>

            <c:if test="${puedeEliminar == true && participant.id!=team.user.id}">
						<td><spring:url
								value="/championships/{championshipId}/teams/{teamId}/{userId}/delete"
								var="deleteUser">
								<spring:param name="championshipId" value="${championship.id}" />
								<spring:param name="teamId" value="${team.id}" />
								<spring:param name="userId" value="${participant.id}" />

							</spring:url>

							<div class="boto">
								<a href="${fn:escapeXml(deleteUser)}">Eliminar jugador</a>
							</div></td>
              </c:if>
					</tr>
				</c:forEach>
			</table>

			<spring:url
				value="/championships/{championshipId}/teams/{teamId}/leave"
				var="leaveTeam">
				<spring:param name="championshipId" value="${championship.id}" />
				<spring:param name="teamId" value="${team.id}" />
			</spring:url>

			<div class="boto">
				<c:if test="${leave && championship.matches.size() == 0}">
					<a href="${fn:escapeXml(leaveTeam)}">Abandonar equipo</a>
				</c:if>
			</div>
    
			<table id="championshipTable" class="table ">
				<h2>Lista de partidos del equipo</h2>
				<thead>
					<tr class="rowtable">
						<th class="guiz-awards-header-title" style="width: 20%;">Equipo
							1</th>
						<th class="guiz-awards-header-title" style="width: 20%;">Equipo
							2</th>
						<th class="guiz-awards-header-title" style="width: 20%;">Fecha
							del partido</th>
					</tr>
				</thead>
				<c:forEach items="${matches}" var="match">
					<tr class="rowtable">
						<td><c:out value="${match.team1}" /></td>
						<td><c:out value="${match.team2}" /></td>
						<td><c:out value="${match.dateTime}" /></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</body>

</playtogether:layout>