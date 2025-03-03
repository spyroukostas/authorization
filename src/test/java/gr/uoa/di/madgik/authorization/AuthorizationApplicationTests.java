package gr.uoa.di.madgik.authorization;

import gr.uoa.di.madgik.authorization.config.AuthorizationAutoConfiguration;
import gr.uoa.di.madgik.authorization.domain.Permission;
import gr.uoa.di.madgik.authorization.service.Authorization;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = {AuthorizationAutoConfiguration.class}, properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationApplicationTests {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Authorization authorization;

    private static final String SUBJECT_USER1 = "user1";
    private static final String SUBJECT_USER2 = "user2";
    private static final String ACTION_READ = "read";
    private static final String ACTION_WRITE = "write";
    private static final String ACTION_MANAGE = "manage";
    private static final String ACTION_VALIDATE = "validate";
    private static final String OBJECT_RESOURCE1 = "resource1";
    private static final String OBJECT_RESOURCE2 = "resource2";


    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void testUpdateUsingTransactional() {
        // create entry
        Permission triple = new Permission("test", "write", "test");
        triple = permissionRepository.save(triple);

        Long id = triple.getId();

        // Update using setters
        triple.setSubject("user");
        triple.setAction("read");
        triple.setObject("resource");

        // retrieve result from db
        Optional<Permission> resultOp = permissionRepository.findById(id);
        assertTrue(resultOp.isPresent());
        Permission result = resultOp.get();

        // test triple-result values
        assertEquals("Subject: ", triple.getSubject(), result.getSubject());
        assertEquals("Action: ", triple.getAction(), result.getAction());
        assertEquals("Object: ", triple.getObject(), result.getObject());

        permissionRepository.delete(triple);
    }

    @Test
    @Order(2)
    void createEntries() {
        Permission triple = new Permission(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);

        triple = new Permission(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        permissionRepository.save(triple);

        triple = new Permission(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2);
        permissionRepository.save(triple);
    }

    @Test
    @Order(3)
    void findEntriesBySubject() {
        List<Permission> list = permissionRepository.findBySubject("user1");
        assertEquals("user2 triples = 4", 4, list.size());

        list = permissionRepository.findBySubject("user2");
        assertEquals("user2 triples = 4", 4, list.size());
    }

    @Test
    @Order(4)
    void entryExists() {
        boolean hasAccess = authorization.canDo(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        assertEquals("user2 can read resource2 = true", true, hasAccess);
    }

    @Test
    @Order(5)
    void entryNotExists() {
        boolean hasAccess = authorization.canDo(SUBJECT_USER2, ACTION_VALIDATE, OBJECT_RESOURCE2);
        assertEquals("user2 can validate resource2 = false", false, hasAccess);
    }

    @Test
    @Order(6)
    void deleteEntries() {
        Permission triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);
        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);
        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);
        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);

        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);
        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1).get();
        permissionRepository.delete(triple);

        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2).get();
        permissionRepository.delete(triple);
        triple = permissionRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2).get();
        permissionRepository.delete(triple);
    }
}
