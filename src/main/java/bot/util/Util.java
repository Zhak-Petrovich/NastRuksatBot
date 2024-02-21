package bot.util;
import bot.model.Project;
import java.util.List;

public class Util {
    public static Boolean isAdmin(Long id) {
        System.out.println(id);
        return id == 460498710 //Me
                || id == 408906445 // Nast
                || id == 537308122; // Anton
    }

    public static void prepareToUpdate(Project project, Project projectToUpdate) {
        if (project.getName() != null) {
            projectToUpdate.setName(project.getName());
        }
        if (project.getDescription() != null) {
            projectToUpdate.setDescription(project.getDescription());
        }
        if (project.getPrice() != null) {
            projectToUpdate.setPrice(project.getPrice());
        }
        if (project.getQuantity() != null) {
            projectToUpdate.setQuantity(project.getQuantity());
        }
        if (project.getDeadLine() != null) {
            projectToUpdate.setDeadLine(project.getDeadLine());
        }
        if (project.getCategory() != null) {
            projectToUpdate.setCategory(project.getCategory());
        }
    }

    public static List<Project> getFilteredProjects(List<Project> projects, String filter, Boolean isIndividual) {
        if (isIndividual) {
            return projects.stream()
                    .filter(project -> project.getCategory().equalsIgnoreCase(filter))
                    .filter(project -> project.getQuantity().equals("0") || project.getQuantity().isBlank())
                    .toList();
        }
        return projects.stream()
                .filter(project -> project.getCategory().equalsIgnoreCase(filter))
                .filter(project -> !project.getQuantity().equals("0") && !project.getQuantity().isBlank())
                .toList();
    }

}
