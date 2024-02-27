package bot.controller;

import bot.model.Support;
import bot.model.Project;
import bot.service.SupService;
import bot.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class WebController {
    private final ProjectService projectService;
    private final SupService supService;

    public WebController(ProjectService projectService, SupService supService) {
        this.projectService = projectService;
        this.supService = supService;
    }

    @GetMapping("")
    public String getAllProjects(ModelMap modelMap) {
        modelMap.addAttribute("projects", projectService.getAll());
        modelMap.addAttribute("category", supService.getSupportById(1));
        modelMap.addAttribute("about", supService.getSupportById(2));
        return "index";
    }

    @GetMapping("/new")
    public String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "newProject";
    }

    @PostMapping("")
    public String saveProject(@ModelAttribute("project") Project project,
                          @RequestParam("file") MultipartFile file) {

        projectService.saveProject(project, file);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable("id") Integer id,
                              Model model) {

        model.addAttribute("project", projectService.getProjectById(id));
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProject(@ModelAttribute("project") Project project,
                                @PathVariable("id") Integer id) {
        projectService.updateProject(project, id);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String confirmProjectDeleting(@PathVariable("id") Integer id, ModelMap modelMap) {
        modelMap.addAttribute("project", projectService.getProjectById(id));
        return "delete";
    }

    @GetMapping("/delete")
    public String deleteProjectById(@ModelAttribute("id") Integer id) throws IOException {
        projectService.deleteProject(id);
        return "redirect:/";
    }
    @GetMapping("/show/{id}")
    public String getProjectById(@PathVariable("id") Integer id,
                                 Model model) {
        model.addAttribute("project", projectService.getProjectById(id));
        return "index";
    }

    @GetMapping("/editCategory")
    public String editSeasonCategory(Model model) {
        model.addAttribute("category", supService.getSupportById(1));
        return "editCategory";
    }

    @PostMapping("/editCategory")
    public String updateSeasonCategory(Support category) {
        supService.updateValue(category, 1);
        return "redirect:/";
    }

    @GetMapping("/editAbout")
    public String editAbout(Model model) {
        model.addAttribute("about", supService.getSupportById(2));
        return "editAbout";
    }

    @PostMapping("/editAbout")
    public String updateAbout(Support about) {
        supService.updateValue(about, 2);
        return "redirect:/";
    }
}
