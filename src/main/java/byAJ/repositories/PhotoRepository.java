package byAJ.repositories;

import byAJ.models.Photo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PhotoRepository extends CrudRepository<Photo, Long>{
    List<Photo> findAllByBotmessageIsNotAndTopmessageIsNot(String botmessage, String topmessage);
    List<Photo> findAllByBotmessageEqualsAndTopmessageEquals(String botmessage, String topmessage);
    List<Photo> findAllByType(String type);
    Photo findById(Long id);

}
