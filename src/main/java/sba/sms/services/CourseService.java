package sba.sms.services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CourseService is a concrete class. This class implements the
 * CourseI interface, overrides all abstract service methods and
 * provides implementation for each method.
 */
public class CourseService implements CourseI{
   private final  SessionFactory sessionFactory = HibernateUtil.getSessionFactory() ;

    @Override
    public void createCourse(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
             transaction = session.beginTransaction();
            session.persist(course);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Handle exception

        }
    }

    @Override
    public Course getCourseById(int courseId) {
        Course course = null;
        try(Session session = sessionFactory.openSession()) {
            course = session.get(Course.class,courseId);

        }catch (Exception e) {
            e.printStackTrace(); // Handle exception
        }
        return course;


    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = null;
        try(Session session = sessionFactory.openSession()) {
            courses=  session.createQuery("from Course", Course.class).list();
        }catch (Exception e) {
            e.printStackTrace(); // Handle exception
        }
        return courses;
    }
}
