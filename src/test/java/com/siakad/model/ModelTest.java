package com.siakad.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    // ===== TEST COURSE CLASS =====
    @Test
    void testCourse_DefaultConstructor() {
        Course course = new Course();

        assertNotNull(course);
        assertNull(course.getCourseCode());
        assertNull(course.getCourseName());
        assertEquals(0, course.getCredits());
        assertEquals(0, course.getCapacity());
        assertEquals(0, course.getEnrolledCount());
        assertNull(course.getLecturer());
        assertNotNull(course.getPrerequisites());
        assertTrue(course.getPrerequisites().isEmpty());
    }

    @Test
    void testCourse_ParameterizedConstructor() {
        Course course = new Course("CS101", "Pemrograman", 3, 30, 25, "Dr. Smith");

        assertEquals("CS101", course.getCourseCode());
        assertEquals("Pemrograman", course.getCourseName());
        assertEquals(3, course.getCredits());
        assertEquals(30, course.getCapacity());
        assertEquals(25, course.getEnrolledCount());
        assertEquals("Dr. Smith", course.getLecturer());
        assertNotNull(course.getPrerequisites());
        assertTrue(course.getPrerequisites().isEmpty());
    }

    @Test
    void testCourse_SettersAndGetters() {
        Course course = new Course();

        course.setCourseCode("CS102");
        course.setCourseName("Basis Data");
        course.setCredits(4);
        course.setCapacity(40);
        course.setEnrolledCount(35);
        course.setLecturer("Prof. Johnson");

        List<String> prerequisites = new ArrayList<>(Arrays.asList("CS101", "MATH101"));
        course.setPrerequisites(prerequisites);

        assertEquals("CS102", course.getCourseCode());
        assertEquals("Basis Data", course.getCourseName());
        assertEquals(4, course.getCredits());
        assertEquals(40, course.getCapacity());
        assertEquals(35, course.getEnrolledCount());
        assertEquals("Prof. Johnson", course.getLecturer());
        assertEquals(prerequisites, course.getPrerequisites());
        assertTrue(course.getPrerequisites().contains("CS101"));
        assertTrue(course.getPrerequisites().contains("MATH101"));
    }

    @Test
    void testCourse_AddPrerequisite() {
        Course course = new Course();

        // Test adding to null prerequisites list
        course.addPrerequisite("CS100");
        assertNotNull(course.getPrerequisites());
        assertEquals(1, course.getPrerequisites().size());
        assertTrue(course.getPrerequisites().contains("CS100"));

        // Test adding to existing list
        course.addPrerequisite("MATH100");
        assertEquals(2, course.getPrerequisites().size());
        assertTrue(course.getPrerequisites().contains("MATH100"));
    }

    @Test
    void testCourse_AddPrerequisite_WhenPrerequisitesAlreadyExist() {
        Course course = new Course();

        // Setup: prerequisites sudah ada (tidak null)
        List<String> existingPrereqs = new ArrayList<>();
        existingPrereqs.add("EXISTING_COURSE");
        course.setPrerequisites(existingPrereqs);

        // Test: add prerequisite ketika list sudah ada
        course.addPrerequisite("NEW_COURSE");

        // Verify: list tetap sama object-nya, hanya ditambah item
        assertSame(existingPrereqs, course.getPrerequisites()); // Masih object yang sama
        assertEquals(2, course.getPrerequisites().size());
        assertTrue(course.getPrerequisites().contains("EXISTING_COURSE"));
        assertTrue(course.getPrerequisites().contains("NEW_COURSE"));
    }

    @Test
    void testCourse_AddPrerequisiteWithExistingList() {
        Course course = new Course();
        // Gunakan ArrayList yang mutable
        course.setPrerequisites(new ArrayList<>(Arrays.asList("CS100")));

        course.addPrerequisite("MATH100");
        assertEquals(2, course.getPrerequisites().size());
        assertTrue(course.getPrerequisites().contains("CS100"));
        assertTrue(course.getPrerequisites().contains("MATH100"));
    }


    @Test
    void testCourseGrade_DefaultConstructor() {
        CourseGrade courseGrade = new CourseGrade();

        assertNotNull(courseGrade);
        assertNull(courseGrade.getCourseCode());
        assertEquals(0, courseGrade.getCredits());
        assertEquals(0.0, courseGrade.getGradePoint(), 0.01);
    }

    @Test
    void testCourseGrade_ParameterizedConstructor() {
        CourseGrade courseGrade = new CourseGrade("CS101", 3, 4.0);

        assertEquals("CS101", courseGrade.getCourseCode());
        assertEquals(3, courseGrade.getCredits());
        assertEquals(4.0, courseGrade.getGradePoint(), 0.01);
    }

    @Test
    void testCourseGrade_SettersAndGetters() {
        CourseGrade courseGrade = new CourseGrade();

        courseGrade.setCourseCode("CS102");
        courseGrade.setCredits(4);
        courseGrade.setGradePoint(3.5);

        assertEquals("CS102", courseGrade.getCourseCode());
        assertEquals(4, courseGrade.getCredits());
        assertEquals(3.5, courseGrade.getGradePoint(), 0.01);
    }

    // ===== TEST ENROLLMENT CLASS =====
    @Test
    void testEnrollment_DefaultConstructor() {
        Enrollment enrollment = new Enrollment();

        assertNotNull(enrollment);
        assertNull(enrollment.getEnrollmentId());
        assertNull(enrollment.getStudentId());
        assertNull(enrollment.getCourseCode());
        assertNull(enrollment.getEnrollmentDate());
        assertNull(enrollment.getStatus());
    }

    @Test
    void testEnrollment_ParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Enrollment enrollment = new Enrollment("ENR-001", "STU-123", "CS101", now, "APPROVED");

        assertEquals("ENR-001", enrollment.getEnrollmentId());
        assertEquals("STU-123", enrollment.getStudentId());
        assertEquals("CS101", enrollment.getCourseCode());
        assertEquals(now, enrollment.getEnrollmentDate());
        assertEquals("APPROVED", enrollment.getStatus());
    }

    @Test
    void testEnrollment_SettersAndGetters() {
        Enrollment enrollment = new Enrollment();
        LocalDateTime enrollmentDate = LocalDateTime.of(2024, 10, 25, 10, 30);

        enrollment.setEnrollmentId("ENR-002");
        enrollment.setStudentId("STU-456");
        enrollment.setCourseCode("CS102");
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStatus("PENDING");

        assertEquals("ENR-002", enrollment.getEnrollmentId());
        assertEquals("STU-456", enrollment.getStudentId());
        assertEquals("CS102", enrollment.getCourseCode());
        assertEquals(enrollmentDate, enrollment.getEnrollmentDate());
        assertEquals("PENDING", enrollment.getStatus());
    }

    // ===== TEST STUDENT CLASS =====
    @Test
    void testStudent_DefaultConstructor() {
        Student student = new Student();

        assertNotNull(student);
        assertNull(student.getStudentId());
        assertNull(student.getName());
        assertNull(student.getEmail());
        assertNull(student.getMajor());
        assertEquals(0, student.getSemester());
        assertEquals(0.0, student.getGpa(), 0.01);
        assertNull(student.getAcademicStatus());
    }

    @Test
    void testStudent_ParameterizedConstructor() {
        Student student = new Student("STU-123", "John Doe", "john@university.edu",
                "Computer Science", 3, 3.75, "ACTIVE");

        assertEquals("STU-123", student.getStudentId());
        assertEquals("John Doe", student.getName());
        assertEquals("john@university.edu", student.getEmail());
        assertEquals("Computer Science", student.getMajor());
        assertEquals(3, student.getSemester());
        assertEquals(3.75, student.getGpa(), 0.01);
        assertEquals("ACTIVE", student.getAcademicStatus());
    }

    @Test
    void testStudent_SettersAndGetters() {
        Student student = new Student();

        student.setStudentId("STU-456");
        student.setName("Jane Smith");
        student.setEmail("jane@university.edu");
        student.setMajor("Information Technology");
        student.setSemester(5);
        student.setGpa(3.25);
        student.setAcademicStatus("PROBATION");

        assertEquals("STU-456", student.getStudentId());
        assertEquals("Jane Smith", student.getName());
        assertEquals("jane@university.edu", student.getEmail());
        assertEquals("Information Technology", student.getMajor());
        assertEquals(5, student.getSemester());
        assertEquals(3.25, student.getGpa(), 0.01);
        assertEquals("PROBATION", student.getAcademicStatus());
    }

    // ===== TEST INTEGRATION BETWEEN CLASSES =====
    @Test
    void testCourseAndEnrollmentIntegration() {
        Course course = new Course("CS101", "Pemrograman", 3, 30, 25, "Dr. Smith");
        Enrollment enrollment = new Enrollment("ENR-001", "STU-123", "CS101",
                LocalDateTime.now(), "APPROVED");

        assertEquals(course.getCourseCode(), enrollment.getCourseCode());
    }

    @Test
    void testStudentAndEnrollmentIntegration() {
        Student student = new Student("STU-123", "John Doe", "john@email.com",
                "CS", 3, 3.5, "ACTIVE");
        Enrollment enrollment = new Enrollment("ENR-001", "STU-123", "CS101",
                LocalDateTime.now(), "APPROVED");

        assertEquals(student.getStudentId(), enrollment.getStudentId());
    }

    @Test
    void testMultiplePrerequisites() {
        Course course = new Course();
        course.addPrerequisite("CS100");
        course.addPrerequisite("MATH100");
        course.addPrerequisite("PHYS100");

        assertEquals(3, course.getPrerequisites().size());
        assertTrue(course.getPrerequisites().contains("CS100"));
        assertTrue(course.getPrerequisites().contains("MATH100"));
        assertTrue(course.getPrerequisites().contains("PHYS100"));
    }
}