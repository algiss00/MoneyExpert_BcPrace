package cz.cvut.fel.controller;

import cz.cvut.fel.model.User;
import cz.cvut.fel.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAll() {
        List<User> usersAll = userService.getAll();
        if (usersAll.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(usersAll, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> add(@RequestBody User user) throws URISyntaxException {
        if (!userService.persist(user)){
            return new ResponseEntity<User>(HttpStatus.CONFLICT);
        }
        String myUrl = "/users/" + user.getId();
        URI myURI = new URI(myUrl);
        return ResponseEntity.created(myURI).body(user);
    }
}
