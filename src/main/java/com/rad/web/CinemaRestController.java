package com.rad.web;

import com.rad.dao.FilmRepository;
import com.rad.dao.TicketRepository;
import com.rad.dao.UserRepository;
import com.rad.entities.AuthRequest;
import com.rad.entities.Film;
import com.rad.entities.Ticket;
import com.rad.entities.User;
import com.rad.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin("*")
public class CinemaRestController {
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @GetMapping(path="/imagesFilm/{id}", produces= MediaType.IMAGE_JPEG_VALUE)
    public byte[] image(@PathVariable(name="id") Long id) throws IOException {
        Film f = filmRepository.findById(id).get();
        String photoName = f.getPhoto();
        File file = new File(System.getProperty("user.home") + "/cinema/images/" + photoName);
        Path path = Paths.get(file.toURI());
        return Files.readAllBytes(path);
    }
    @PostMapping("/payerTickets")
    @Transactional
    public List<Ticket> payerTickets(@RequestBody TicketForm ticketForm){
        List<Ticket> listTickets = new ArrayList<>();
        ticketForm.getTickets().forEach(idTicket -> {
            Ticket ticket = ticketRepository.findById(idTicket).get();
            ticket.setNomClient(ticketForm.getNomClient());
            ticket.setReserve(true);
            ticketRepository.save(ticket);
            listTickets.add(ticket);
        });
        return listTickets;
    }

    @PostMapping("/authenticate")
    public HashMap<String, Object> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );
        }catch (Exception e){
            throw new Exception("Invalid username or password");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("access_token", jwtUtil.generateToken(authRequest.getUserName()));
        map.put("user", userRepository.findByUsername(authRequest.getUserName()));
        return map;
    }

    @PostMapping("/register")
    public User Registration(@RequestBody User user) throws Exception {
        return userRepository.save(user);
    }
}
@Data
class TicketForm{
    private List<Long> tickets = new ArrayList<>();
    private String nomClient;
}
