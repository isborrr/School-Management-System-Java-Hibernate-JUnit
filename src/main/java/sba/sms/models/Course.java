package sba.sms.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Course is a POJO, configured as a persistent class that represents (or maps to) a table
 * name 'course' in the database. A Course object contains fields that represent course
 * information and a mapping of 'courses' that indicate an inverse or referencing side
 * of the relationship. Implement Lombok annotations to eliminate boilerplate code.
 */

@Entity
@Table(name = "course")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString(exclude = "students")
public class Course {
    @Id
    @Column(name = "Courses_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull
    @Column(name = "Course_name", length = 50, nullable = false)
    private String name;
    @NonNull
    @Column(name = "Instructor_name")
    private String instructor;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "courses")
    private Set<Student> students = new HashSet<>(); // Initialize to avoid null pointer issues

    // Manually override equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return  id == course.id &&
                Objects.equals(name, course.name) &&
                Objects.equals(instructor, course.instructor);
    }

    // Manually override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id, name, instructor);
    }

    // Helper method
    public String getStudentInfo() {
        return String.format("Course[id='%s', name='%s', instructor=%s]", id, name, instructor);
    }

}
