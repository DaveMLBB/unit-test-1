package co.develhope.testController.controller;

import co.develhope.testController.entities.User;
import co.develhope.testController.repositories.UserRepo;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    // create
    @PostMapping("")
    public @ResponseBody User create(@RequestBody User user){
        return userRepo.save(user);
    }

    // read all
    @GetMapping("/")
    public @ResponseBody
    List<User> getStudents(){
        return userRepo.findAll();
    }

    // read just one
    @GetMapping("/{id}")
    public @ResponseBody  User getAStudent(@PathVariable Long id){
        Optional<User> student =  userRepo.findById(id);
        if(student.isPresent()){
            return student.get();
        }else{
            return null;
        }
    }

    // update the id of a user
    @PutMapping("/{id}")
    public @ResponseBody User update(@PathVariable Long id, @RequestBody  @NotNull User user){
        user.setId(id);
        return userRepo.save(user);
    }

    // delete a student
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userRepo.deleteById(id);
    }
}
