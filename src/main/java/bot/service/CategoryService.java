package bot.service;

import bot.model.Category;
import bot.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;

    @Autowired
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category getCategoryById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    @Transactional
    public void updateCategory(Category category, Integer id) {
        Category categoryToUpdate = repository.getReferenceById(id);
        categoryToUpdate.setName(category.getName());
        repository.save(categoryToUpdate);
    }

}
