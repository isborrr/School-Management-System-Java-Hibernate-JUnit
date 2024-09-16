package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentServiceTest {

    private StudentService studentService;
    private SessionFactory sessionFactory;
    private Session session;

    @BeforeAll
    public void setUp() {
        studentService = new StudentService();
        sessionFactory = HibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();

        // Set up initial data using Lombok constructors
        session.beginTransaction();
        Student student = new Student("alice.morison@example.com", "Alica Morison", "alice1234");
        session.persist(student);
        Course course = new Course ("Math 101","Halima Martinez");
        session.persist(course);
        session.getTransaction().commit();
    }

    @BeforeEach
    public void beforeEachMethod() {
        session.beginTransaction();
    }

    @Test
    public void testGetAllStudents() {
        List<Student> students = studentService.getAllStudents();
        Assertions.assertNotNull(students);
        Assertions.assertFalse(students.isEmpty());
    }

    @Test
    public void testCreateStudent() {
        // Use Lombok's all-args constructor to create a student
        Student newStudent = new Student("myriam.penny@example.com", "Myriam Penny", "penny123");
        studentService.createStudent(newStudent);

        // Check if the student was persisted
        Student retrievedStudent = session.get(Student.class, "myriam.penny@example.com");
        assertNotNull(retrievedStudent);
        assertEquals("Myriam Penny", retrievedStudent.getName());
        assertEquals("myriam.penny@example.com", retrievedStudent.getEmail());
    }

    @Test
    public void testGetStudentByEmail() {
        Student newStudent = new Student("alex.jakson@example.com", "Alex Jakson", "alex123");
        studentService.createStudent(newStudent);
        Student getStudent = studentService.getStudentByEmail("alex.jakson@example.com");
        assertNotNull(getStudent);
        assertEquals("Alex Jakson", getStudent.getName());
        assertEquals("alex123", getStudent.getPassword());
        assertEquals(newStudent, getStudent);
    }

    @Test
    public void testValidateStudent() {
        boolean isValid = studentService.validateStudent("alice.morison@example.com", "alice1234");
        assertTrue(isValid);

        isValid = studentService.validateStudent("alice.morison@example.com", "alicwrongpassword");
        assertFalse(isValid);
    }



    @AfterEach
    public void afterEachMethod() {
        session.getTransaction().commit();
    }

    @AfterAll
    public void tearDown() {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}