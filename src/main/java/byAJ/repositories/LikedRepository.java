package byAJ.repositories;

import byAJ.models.Liked;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Meeliana on 7/13/2017.
 */
public interface LikedRepository extends CrudRepository<Liked, Long> {
    @Query(value = "SELECT likednum FROM  liked s1 WHERE photoid=?1" +
            " AND likednum=(SELECT MAX(s2.likednum) FROM liked s2 WHERE s1.photoid = s2.photoid);  ",nativeQuery = true)
    long findDistinc(long number);
}
