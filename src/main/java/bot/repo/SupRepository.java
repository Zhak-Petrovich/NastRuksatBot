package bot.repo;

import bot.model.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupRepository extends JpaRepository<Support, Integer> {
    Optional<Support> findById(Integer id);
}
