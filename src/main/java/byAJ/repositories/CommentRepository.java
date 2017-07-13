package byAJ.repositories;

import byAJ.models.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query(value = "SELECT comment.commentid, photos.id, comment.commented, comment.date,comment.username, photos.image FROM comment,photos WHERE comment.photoid=photos.id;", nativeQuery = true)
    List<Comment> findByQuery();
}
