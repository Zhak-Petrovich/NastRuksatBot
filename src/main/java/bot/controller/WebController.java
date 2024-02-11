package bot.controller;

import bot.model.Project;
import bot.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    private final ProjectService service;

    public WebController(ProjectService service) {
        this.service = service;
    }

    @GetMapping("")
    public String getAllProjects(ModelMap modelMap) {
        modelMap.addAttribute("projects", service.getAll());
        return "index";
    }

    @GetMapping("/new")
    public String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "newProject";
    }

    @PostMapping("")
    public String saveProject(@ModelAttribute("project") Project project) {
        service.saveProject(project);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable("id") Integer id,
                              Model model) {

        model.addAttribute("project", service.getProjectById(id));
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProject(@ModelAttribute("project") Project project,
                                @PathVariable("id") Integer id) {
        service.updateProject(project, id);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String confirmProjectDeleting(@PathVariable("id") Integer id, ModelMap modelMap) {
        modelMap.addAttribute("project", service.getProjectById(id));
        return "delete";
    }

    @GetMapping("/delete")
    public String deleteProjectById(@ModelAttribute("id") Integer id) {
        service.deleteProject(id);
        return "redirect:/";
    }
    @GetMapping("/show/{id}")
    public String getProjectById(@PathVariable("id") Integer id,
                                 Model model) {
        model.addAttribute("project", service.getProjectById(id));
        return "index";
    }
}
