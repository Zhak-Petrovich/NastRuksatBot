package bot.controller;

import bot.model.Category;
import bot.model.Project;
import bot.service.CategoryService;
import bot.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {
    private final ProjectService projectService;
    private final CategoryService categoryService;

    public WebController(ProjectService projectService, CategoryService categoryService) {
        this.projectService = projectService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String getAllProjects(ModelMap modelMap) {
        modelMap.addAttribute("projects", projectService.getAll());
        modelMap.addAttribute("category", categoryService.getCategoryById(1));
        return "index";
    }

    @GetMapping("/new")
    public String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "newProject";
    }

    @PostMapping("")
    public String saveProject(@ModelAttribute("project") Project project) {
        projectService.saveProject(project);
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
    public String deleteProjectById(@ModelAttribute("id") Integer id) {
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
        model.addAttribute("category", categoryService.getCategoryById(1));
        return "editCategory";
    }

    @PostMapping("/editCategory")
    public String updateSeasonCategory(Category category) {
        categoryService.updateCategory(category, 1);
        return "redirect:/";
    }
}
