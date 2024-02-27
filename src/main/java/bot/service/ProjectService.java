package bot.service;

import bot.model.Project;
import bot.repo.ProjectRepository;
import bot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static bot.bot.Bot.PART_OF_PATH;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository repository;

    @Autowired
    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<Project> getAll() {
        return repository.findAll();
    }

    @Transactional
    public void saveProject(Project project, MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String fileName = UUID.randomUUID() + ".jpg";
                String savePath = PART_OF_PATH + fileName;
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(savePath));
                outputStream.write(bytes);
                outputStream.close();
                project.setFileName(fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        repository.save(project);
    }
    @Transactional
    public void saveProject(Project project) {
        repository.save(project);
    }

    public Project getProjectById(Integer id) {
        return repository.getReferenceById(id);
    }

    @Transactional
    public void updateProject(Project project, Integer id) {
        Project projectToUpdate = repository.getReferenceById(id);
        Util.prepareToUpdate(project, projectToUpdate);
        repository.save(projectToUpdate);
    }
    @Transactional
    public void deleteProject(Integer id) throws IOException {
        Files.delete(Path.of(PART_OF_PATH + repository.getReferenceById(id).getFileName()));
        repository.deleteById(id);
    }
}
