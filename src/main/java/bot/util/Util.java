package bot.util;

import bot.model.Project;

public class Util {
    public static Boolean isAdmin(Long id) {
        return id == 460498710 || id == 408906445 || id == 537308122;
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
}
