package byAJ.controllers;

import byAJ.configs.CloudinaryConfig;
import byAJ.models.*;
import byAJ.repositories.CommentRepository;
import byAJ.repositories.LikedRepository;
import byAJ.repositories.PhotoRepository;
import byAJ.services.UserService;
import byAJ.validators.UserValidator;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoRepository photoRepo;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikedRepository likedRepository;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
    long idSessio, sessionNumber=0;
    @RequestMapping(value = "/loginSuccess", method = RequestMethod.GET)
    public String getLogin(Model model,  Principal principal){
        model.addAttribute("photo",new Photo());
        model.addAttribute("comobj",new Comment());
        String loggedName = principal.getName();
        Iterable<Photo> photoList = photoRepo.findByUsername(loggedName);
        List<String> newList = new ArrayList<>();
        String url=null;
        for(Photo pl:photoList){
            newList.add(pl.getImage());
            url  = pl.getImage();
        }

        model.addAttribute("srcSession", url);
        model.addAttribute("Album",photoList);
        return "profile";
    }

    @RequestMapping(value = "/galgalery", method = RequestMethod.GET)
    public String Gallery(Model model,  Principal principal){
        model.addAttribute("photo",new Photo());
        model.addAttribute("comobj",new Comment());
        model.addAttribute("liked", new Liked());
        String loggedName = principal.getName();
        Iterable<Photo> photoList = photoRepo.findByUsername(loggedName);
        List<String> newList = new ArrayList<>();
        String url=null;
        for(Photo pl:photoList){
            newList.add(pl.getImage());
            url  = pl.getImage();
        }

        model.addAttribute("srcSession", url);
        model.addAttribute("Album",photoList);
        return "gallery";
    }




    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Model model){
        model.addAttribute(new Photo());
        model.addAttribute("com",new Comment());
        return "/profile";
    }
    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @RequestMapping("/memelink/{username}")
    public String linktoMeme(@PathVariable("id") Long id, Model model){
        Photo p = photoRepo.findById(id);
        List<Photo> plist = new ArrayList<Photo>();
        plist.add(p);
        String single = p.getImage();
        model.addAttribute("images",plist);
        model.addAttribute("single",single);
        return "gallery";
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) throws UnsupportedEncodingException {

        model.addAttribute("user", user);
        userValidator.validate(user, result);

        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }

        return "index";
    }

    public UserValidator getUserValidator() {
        return userValidator;
    }

    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @GetMapping("/upload")
    public String uploadForm(Model model) {
        model.addAttribute("p", new Photo());
        model.addAttribute("liked", new Liked());
        return "upload";
    }

    @PostMapping("/upload")
    public String singleImageUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
                                    Model model, @ModelAttribute Photo p, Principal principal, Liked liked){

        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("message","Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));

            model.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            String filename = uploadResult.get("public_id").toString() + "." + uploadResult.get("format").toString();
            String effect = p.getTitle();
            p.setImage("<img src='http://res.cloudinary.com/henokzewdie/image/upload/" + effect + "/" +filename+"' width='200px'/>");
            //System.out.printf("%s\n", cloudc.createUrl(filename,900,900, "fit"));
            p.setCreatedAt(new Date());
            p.setUsername(principal.getName());
            photoRepo.save(p);
            setupGallery(model);
            liked.setUsername(principal.getName());
            liked.setPhotoid(p.getId());
            liked.setLikednum(0);
            likedRepository.save(liked);
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "gallery";
    }

    @RequestMapping("/gallery")
    public String gallery(Model model){
        setupGallery(model);
        return "gallery";
    }

    @RequestMapping("/img/{id}")
    public String something(@PathVariable("id") long id, Model model, Comment comment, Liked liked){
        model.addAttribute("photo", photoRepo.findById(id));
        model.addAttribute("comobj",new Comment());
        model.addAttribute("liked",new Liked());
        model.addAttribute("foto", new Photo());
        idSessio=id;
        sessionNumber = likedRepository.findByPhotoid(id);
        if(sessionNumber==0){return "textgen";}

        else {sessionNumber = likedRepository.findDistinc(id);
        return "textgen";}
    }

    @RequestMapping("/textgen")
    public String textgen(Model model){
        model.addAttribute("photo", new Photo());
        model.addAttribute("liked", new Liked());
        return "textgen";
    }

    @PostMapping("/creatememe")
    public String creatememe(@ModelAttribute Photo photo, Model model, Principal principal) throws UnsupportedEncodingException {
        User u = userService.findByUsername(principal.getName());
        model.addAttribute("liked", new Liked());
        photoRepo.save(photo);
        setupGallery(model);
        model.addAttribute("Meme created");
        sendEmailWithoutTemplating(u.getUsername(), u.getEmail(), photo.getId());
        return "gallery";
    }

    @RequestMapping("/select/{id}")
    public String selectSomthign(@PathVariable("id") String type, Model model){
        List<Photo> list = photoRepo.findAllByType(type);
        model.addAttribute("images", list);
        model.addAttribute("liked", new Liked());
        return "makememe";
    }

    @GetMapping("/makememe")
    public String getMeme(Model model){
        model.addAttribute("com", new Comment());
        Iterable<Photo> list = photoRepo.findAllByBotmessageEqualsAndTopmessageEquals(null, null);
        List<Photo> list2 = new ArrayList<Photo>();
        for(Photo p : list){
            boolean check = true;
            for(Photo p2 : list2){
                if(p2.getType().equals(p.getType())){
                    check = false;
                    break;
                }
                else{
                    check = true;
                }
            }
            if(check){
                list2.add(p);
            }
            System.out.printf("3 %s\n", p.getType());
        }
        Set<Photo> myList = new HashSet<Photo>();
        for(Photo p2 : list2){
            myList.add(p2);
        }
        model.addAttribute("photoList", myList);
        return "makememe";
    }

    private void setupGallery(Model model){
        Iterable<Photo> photoList = photoRepo.findAllByBotmessageIsNotAndTopmessageIsNot("","");

        model.addAttribute("images", photoList);
    }
    @Autowired
    public EmailService emailService;
    public void sendEmailWithoutTemplating(String username, String email2, Long id) throws UnsupportedEncodingException {
        final Email email = DefaultEmail.builder()
                .from(new InternetAddress("daylinzack@gmail.com", "General Alaadin"))
                .to(Lists.newArrayList(new InternetAddress(email2, username)))
                .subject("Your meme is here and ready for consumption")
                .body("Hi youre meme is: localhost:3000/memelink/" + String.valueOf(id) )
                .encoding("UTF-8").build();
        emailService.send(email);
    }

       @RequestMapping(value = "/createcomment", method = RequestMethod.GET)
       public String commentGet(Model model){
           model.addAttribute("comobj",new Comment());
           return "/createcomment";
       }
    @RequestMapping(value = "/createcomment", method = RequestMethod.POST)
    public String commentPost(@ModelAttribute Comment comobj, Model model, Principal principal){
        model.addAttribute("comobj",new Comment());
        comobj.setUsername(principal.getName());
        comobj.setDate(new Date());
        commentRepository.save(comobj);
        return "redirect:/loginSuccess";
    }
    @RequestMapping(value = "/showcomment", method = RequestMethod.GET)
    public String ShowcommentGet(Model model){
        model.addAttribute("comobj",new Comment());
        model.addAttribute(new Photo());
        return "showcomment";
    }
    @RequestMapping(value = "/showcomment", method = RequestMethod.POST)
    public String ShowcommentPost(@ModelAttribute Comment comment, Model model){
        model.addAttribute("comobj",new Comment());
        model.addAttribute(new Photo());
        Iterable<Comment> commentList = commentRepository.findByQuery();
        model.addAttribute("commentList",commentList);
        return "display";
    }
    @RequestMapping(value = "/likeme", method = RequestMethod.GET)
    public String Likeme(Model model){
        model.addAttribute("liked",new Liked());
        return "likeme";
    }
    @RequestMapping(value = "/likeme", method = RequestMethod.POST)
    public String LikemePOST( @ModelAttribute Liked liked, Model model, Principal principal){
        model.addAttribute("photo",new Photo());
        Photo photo= new Photo();
        System.out.print("NUMBER IS " + sessionNumber);
        liked.setLikednum(sessionNumber + 1);
        liked.setUsername(principal.getName());
        liked.setPhotoid(idSessio);
        likedRepository.save(liked);
        return "redirect:/galgalery";
    }
}