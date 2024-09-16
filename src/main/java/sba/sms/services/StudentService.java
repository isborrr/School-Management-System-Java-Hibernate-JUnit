package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods and
 * provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */

public class StudentService implements StudentI{
   private final  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    @Override
    public List<Student> getAllStudents() {
        List<Student> students = null;
        try (Session session = sessionFactory.openSession()) {
            students = session.createQuery("from Student", Student.class).list();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void createStudent(Student student) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()){
             transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        }catch (Exception e) {
            // Rollback the transaction in case of any errors
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Handle exceptions properly in production code
        }

    }

    @Override
    public Student getStudentByEmail(String email) {
        Student student = null;
        try (Session session = sessionFactory.openSession()){
            student = session.get(Student.class,email);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        Transaction transaction = null;
        boolean isValid = false;
        try (Session session = sessionFactory.openSession()) {
            // Start a transaction
             transaction = session.beginTransaction();

            // Create a query to check if a student with the given email and password exists
            Query<Student> query = session.createQuery(
                    "FROM Student WHERE email = :email AND password = :password", Student.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            // If a student is found, the query will return a non-empty list
            Student student = query.uniqueResult();

            if (student != null) {
                isValid = true; // Student credentials are valid
                // Additional logic if needed, e.g., loading the student's courses
                student.getCourses().size(); // Eagerly load courses if needed
            }

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            // Rollback the transaction in case of any errors
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Handle exceptions properly in production code
        }

        return isValid;
    }


    @Override
    public void registerStudentToCourse(String email, int courseId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            // Retrieve the Student by email
            Student student = (Student) session.createQuery("FROM Student WHERE email = :email")
                    .setParameter("email", email)
                    .uniqueResult();

            // Retrieve the Course by ID
            Course course = session.get(Course.class, courseId);

            if (student != null && course != null) {
                // Add the course to the student's course list
                student.getCourses().add(course);

                // Save the updated student entity
                session.merge(student);
            } else {
                System.out.println("Student or Course not found.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        //
    }

    @Override
    public List<Course> getStudentCourses(String email) {

        List<Course> courses = new ArrayList<>();  // Initialize list to store the courses

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            // Obtain the session from Hibernate SessionFactory (assuming you have a session factory configured)
            transaction = session.beginTransaction();

            // Native SQL query to get courses for a given student email
            String sql = "SELECT c.* FROM course c " +
                    "JOIN student_courses sc ON c.Courses_id = sc.courses_id " +
                    "JOIN student s ON sc.student_email = s.email " +
                    "WHERE s.email = :email";

            // Execute native query and map results to Course entity
            courses = session.createNativeQuery(sql, Course.class)
                    .setParameter("email", email)
                    .getResultList();

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            // Handle exceptions and rollback if needed
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();  // Log exception for debugging
        }
        // Close the session

        return courses;  // Return the list of courses
    }
}
