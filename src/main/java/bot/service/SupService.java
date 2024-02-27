package bot.service;

import bot.model.Support;
import bot.repo.SupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupService {
    private final SupRepository repository;

    @Autowired
    public SupService(SupRepository repository) {
        this.repository = repository;
    }

    public Support getSupportById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    @Transactional
    public void updateValue(Support sup, Integer id) {
        Support supToUpdate = repository.getReferenceById(id);
        supToUpdate.setValue(sup.getValue());
        repository.save(supToUpdate);
    }

}
