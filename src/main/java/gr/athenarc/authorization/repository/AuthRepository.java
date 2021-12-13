package gr.athenarc.authorization.repository;

import gr.athenarc.authorization.domain.AuthTriple;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends CrudRepository<AuthTriple, Long> {

    List<AuthTriple> findBySubject(String sub);
    List<AuthTriple> findByAction(String action);
    List<AuthTriple> findByObject(String obj);

    List<AuthTriple> findBySubjectAndAction(String sub, String action);
    List<AuthTriple> findBySubjectAndObject(String sub, String obj);
    List<AuthTriple> findByActionAndObject(String action, String obj);

    Optional<AuthTriple> findBySubjectAndActionAndObject(String sub, String action, String obj);

    void deleteAllBySubject(String sub);
    void deleteBySubjectAndActionAndObject(String sub, String action, String obj);
    boolean existsBySubjectAndActionAndObject(String sub, String action, String obj);

}
