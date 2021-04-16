
package net.playtogether.jpa.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import net.playtogether.jpa.entity.Meeting;
import net.playtogether.jpa.entity.Sport;
import net.playtogether.jpa.entity.Usuario;
import net.playtogether.jpa.service.MeetingService;
import net.playtogether.jpa.service.SportService;
import net.playtogether.jpa.service.UsuarioService;

@Controller
public class MeetingController {

	@Autowired
	MeetingService	meetingService;

	@Autowired
	UsuarioService	userService;

	@Autowired
	SportService	sportService;

	@Autowired
	UsuarioService	usuarioService;


	@InitBinder("meeting")
	public void initMeetingBinder(final WebDataBinder dataBinder) {
		dataBinder.setValidator(new MeetingValidator());
	}

	@GetMapping("/sports/{sportId}/meetings/add")
	public String initCreationMeeting(final ModelMap model, @PathVariable("sportId") final Integer sportId, final Principal principal) {
		Integer listSports = this.sportService.findAll().size();
		Usuario usuario = this.userService.findByUsername(principal.getName());
		if (sportId > 0 && sportId <= listSports) {
			Collection<Meeting> meetingMonth = this.meetingService.findMeetingThisMonthToUser(usuario.getId());
			if (usuario.getType().getId() == 1 && meetingMonth.size() > 0) {
				Collection<Meeting> meetings = this.meetingService.listMeetingsBySport(sportId);
				Sport sport = this.sportService.findSportById(sportId);
				model.addAttribute("meetings", meetings);
				model.addAttribute("deporte", sportId);
				model.addAttribute("nombreDeporte", sport.getName());
				model.addAttribute("limiteMes", true);
				return "meetings/listMeeting";
			} else {
				model.put("meeting", new Meeting());
				model.put("sportId", sportId);
				model.put("sport", this.sportService.findSportById(sportId));
				return "meetings/createMeetingForm";
			}
		} else {
			return "error-500";
		}
	}

	@PostMapping("/sports/{sportId}/meetings/add")
	public String postCreationMeeting(@Valid final Meeting meeting, final BindingResult result, final ModelMap model, @PathVariable("sportId") final Integer sportId, final Principal principal) {
		Sport sport = this.sportService.findSportById(sportId);
		if (result.hasErrors()) {
			model.put("sport", sport);
			model.put("sportId", sportId);
			return "meetings/createMeetingForm";
		} else {
			Usuario usuario = this.usuarioService.usuarioLogueado(principal.getName());
			meeting.setMeetingCreator(usuario);

			List<Usuario> participants = new ArrayList<>();
			participants.add(usuario);
			meeting.setParticipants(participants);
			meeting.setNumberOfPlayers(sport.getNumberOfPlayersInTeam() * 2);
			meeting.setCreationDate(LocalDate.now());
			this.meetingService.save(meeting);
			usuario.setPuntos(usuario.getPuntos() + 7);
			this.usuarioService.saveUsuarioAlreadyRegistered(usuario);

			return "redirect:/sports/" + sportId + "/meetings";
		}

	}

	@GetMapping("/sports/{sportId}/meetings/{meetingId}/edit")
	public String initUpdateMeeting(final ModelMap model, @PathVariable("sportId") final Integer sportId, @PathVariable("meetingId") final Integer meetingId, final Principal principal) {
		Meeting meeting = this.meetingService.findMeetingById(meetingId);
		Usuario usuario = this.usuarioService.usuarioLogueado(principal.getName());
		if (meeting.getMeetingCreator().equals(usuario)) {
			model.put("sport", this.sportService.findSportById(sportId));
			model.put("meeting", meeting);
			return "meetings/updateMeetingForm";
		} else {
			return "error-403";
		}

	}

	@PostMapping("/sports/{sportId}/meetings/{meetingId}/edit")
	public String postUpdateMeeting(@Valid final Meeting meeting, final BindingResult result, final ModelMap model, @PathVariable("sportId") final Integer sportId, @PathVariable("meetingId") final Integer meetingId) {
		if (result.hasErrors()) {
			model.put("sport", this.sportService.findSportById(sportId));
			model.put("meeting", meeting);
			return "meetings/updateMeetingForm";
		} else {
			Meeting meetingToUpdate = this.meetingService.findMeetingById(meetingId);
			BeanUtils.copyProperties(meeting, meetingToUpdate, "id", "sport", "numberOfPlayers", "meetingCreator", "participants", "creationDate");
			this.meetingService.save(meetingToUpdate);
			model.addAttribute("message", "¡Quedada actualizada correctamente!");
			return "redirect:/sports/" + sportId + "/meetings";
		}

	}

	@GetMapping("/sports/{sportId}/meetings")
	public String listMeetings(final ModelMap model, @PathVariable("sportId") final Integer sportId) {
		Collection<Meeting> meetings = this.meetingService.listMeetingsBySport(sportId);
		Sport sport = this.sportService.findSportById(sportId);
		model.addAttribute("meetings", meetings);
		model.addAttribute("deporte", sportId);
		model.addAttribute("nombreDeporte", sport.getName());
		return "meetings/listMeeting";
	}

	@GetMapping("/sports/{sportId}/meetings/{meetingId}")
	public String meetingDetails(final ModelMap model, @PathVariable("meetingId") final Integer meetingId, final Principal principal) {
		Meeting meeting = this.meetingService.findMeetingById(meetingId);
		model.addAttribute("meeting", meeting);
		Boolean b = true;
		Boolean estaLlena = false;
		Usuario u = this.usuarioService.usuarioLogueado(principal.getName());
		List<Usuario> usuarios = meeting.getParticipants();

		if (meeting.getMeetingCreator().equals(u)) {
			model.put("esCreador", true);
			if(u.getUser().getAuthorities().stream().anyMatch(x->x.getAuthority().equals("premium"))) {
				model.put("puedeEliminar", true);
			}
		}

		if (!meeting.getParticipants().contains(u)) {
			b = false;
		}
		model.addAttribute("sport", meeting.getSport());
		if (meeting.getNumberOfPlayers() <= meeting.getParticipants().size()) {
			estaLlena = true;
		}
		model.addAttribute("existe", b);
		model.addAttribute("estaLlena", estaLlena);
		model.addAttribute("logged_user", u);

		if (usuarios.stream().anyMatch(x -> u.equals(x))) {
			model.put("leave", true);
		}

		return "meetings/meetingDetails";
	}

	@GetMapping("/meetings/{meetingId}/join")
	public String meetingJoin(final ModelMap model, @PathVariable("meetingId") final Integer meetingId, final Principal principal) {
		Meeting meeting = this.meetingService.findMeetingById(meetingId);
		Usuario u = this.usuarioService.usuarioLogueado(principal.getName());

		if (!meeting.getParticipants().contains(u) && meeting.getNumberOfPlayers() > meeting.getParticipants().size()) {

			List<Usuario> list = meeting.getParticipants();
			list.add(u);
			meeting.setParticipants(list);

			this.meetingService.save(meeting);

			u.setPuntos(u.getPuntos() + 5);
			this.usuarioService.saveUsuarioAlreadyRegistered(u);

		}

		return this.meetingDetails(model, meetingId, principal);
	}

	@GetMapping("/sports/{sportId}/meetings/{meetingId}/leave")
	public String leaveMeeting(final ModelMap model, @PathVariable("sportId") final Integer sportId, @PathVariable("meetingId") final Integer meetingId, final Principal principal) {
		Meeting meeting = this.meetingService.findMeetingById(meetingId);

		model.addAttribute("meeting", meeting);

		List<Usuario> usuarios = meeting.getParticipants();
		Usuario usuario = this.userService.usuarioLogueado(principal.getName());

		usuarios.remove(usuario);
		this.meetingService.save(meeting);
		if (meeting.getMeetingCreator().equals(usuario)) {
			Integer puntos = usuario.getPuntos() - 7;
			usuario.setPuntos(puntos);
			this.userService.saveUsuario(usuario);
		} else {
			Integer puntos = usuario.getPuntos() - 5;
			usuario.setPuntos(puntos);
			this.userService.saveUsuario(usuario);
		}
		return "redirect:/sports/" + sportId + "/meetings/" + meetingId;

	}

	@GetMapping("/sports/{sportId}/meetings/{meetingId}/{userId}/delete")
	public String deleteMeetingPlayer(final ModelMap model, @PathVariable("sportId") final Integer sportId, @PathVariable("meetingId") final Integer meetingId, @PathVariable("userId") final Integer userId, final Principal principal) {
		Meeting meeting = this.meetingService.findMeetingById(meetingId);

		List<Usuario> usuarios = meeting.getParticipants();
		Usuario usuario = this.userService.usuarioLogueado(principal.getName());
		Usuario deletedUser = this.userService.findUserById(userId);

		model.addAttribute("meeting", meeting);
		if (usuarios.stream().anyMatch(u -> usuario.equals(u))) {
			model.put("leave", true);
		}

		if (!meeting.getMeetingCreator().getUser().getUsername().equals(principal.getName())) {
			model.put("loggedUserIsNotTheMeetingCreator", true);
			return "meetings/meetingDetails";
		} else {
			if (!deletedUser.equals(meeting.getMeetingCreator())) {
				usuarios.removeIf(u -> deletedUser.equals(u));
				this.meetingService.save(meeting);
				Integer puntos = deletedUser.getPuntos() - 5;
				deletedUser.setPuntos(puntos);
				this.userService.saveUsuario(deletedUser);
				model.addAttribute("sport", sportService.findSportById(sportId));
				return "meetings/meetingDetails";
			} else {
				model.put("userToDeleteIsMeetingCreator", true);
				return "meetings/meetingDetails";
			}

		}

	}

}
