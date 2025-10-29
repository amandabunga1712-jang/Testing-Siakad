package com.siakad.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    // Test untuk CourseFullException
    @Test
    void testCourseFullException_StringConstructor() {
        CourseFullException exception = new CourseFullException("Course is full");
        assertEquals("Course is full", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCourseFullException_StringAndThrowableConstructor() {
        RuntimeException cause = new RuntimeException("Root cause");
        CourseFullException exception = new CourseFullException("Course is full", cause);

        assertEquals("Course is full", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Root cause", exception.getCause().getMessage());
    }

    // Test untuk CourseNotFoundException
    @Test
    void testCourseNotFoundException_StringConstructor() {
        CourseNotFoundException exception = new CourseNotFoundException("Course not found");
        assertEquals("Course not found", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCourseNotFoundException_StringAndThrowableConstructor() {
        IllegalArgumentException cause = new IllegalArgumentException("Invalid course code");
        CourseNotFoundException exception = new CourseNotFoundException("Course not found", cause);

        assertEquals("Course not found", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Invalid course code", exception.getCause().getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    // Test untuk EnrollmentException
    @Test
    void testEnrollmentException_StringConstructor() {
        EnrollmentException exception = new EnrollmentException("Enrollment error occurred");
        assertEquals("Enrollment error occurred", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testEnrollmentException_StringAndThrowableConstructor() {
        IllegalStateException cause = new IllegalStateException("System error");
        EnrollmentException exception = new EnrollmentException("Enrollment failed", cause);

        assertEquals("Enrollment failed", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("System error", exception.getCause().getMessage());
    }

    // Test untuk PrerequisiteNotMetException
    @Test
    void testPrerequisiteNotMetException_StringConstructor() {
        PrerequisiteNotMetException exception = new PrerequisiteNotMetException("Prerequisite not met");
        assertEquals("Prerequisite not met", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testPrerequisiteNotMetException_StringAndThrowableConstructor() {
        Exception cause = new Exception("Missing prerequisite course");
        PrerequisiteNotMetException exception = new PrerequisiteNotMetException("Prerequisites not satisfied", cause);

        assertEquals("Prerequisites not satisfied", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Missing prerequisite course", exception.getCause().getMessage());
    }

    // Test untuk StudentNotFoundException
    @Test
    void testStudentNotFoundException_StringConstructor() {
        StudentNotFoundException exception = new StudentNotFoundException("Student not found");
        assertEquals("Student not found", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testStudentNotFoundException_StringAndThrowableConstructor() {
        NullPointerException cause = new NullPointerException("Student ID is null");
        StudentNotFoundException exception = new StudentNotFoundException("Student not found", cause);

        assertEquals("Student not found", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Student ID is null", exception.getCause().getMessage());
        assertTrue(exception.getCause() instanceof NullPointerException);
    }

    // Test inheritance hierarchy
    @Test
    void testExceptionInheritance() {
        // Test bahwa semua exception adalah RuntimeException
        assertTrue(new CourseFullException("test") instanceof RuntimeException);
        assertTrue(new CourseNotFoundException("test") instanceof RuntimeException);
        assertTrue(new EnrollmentException("test") instanceof RuntimeException);
        assertTrue(new PrerequisiteNotMetException("test") instanceof RuntimeException);
        assertTrue(new StudentNotFoundException("test") instanceof RuntimeException);
    }

    // Test exception behavior in try-catch block
    @Test
    void testExceptionThrowingAndCatching() {
        // Test CourseFullException
        assertThrows(CourseFullException.class, () -> {
            throw new CourseFullException("Test throw");
        });

        // Test CourseNotFoundException
        assertThrows(CourseNotFoundException.class, () -> {
            throw new CourseNotFoundException("Test throw");
        });

        // Test EnrollmentException
        assertThrows(EnrollmentException.class, () -> {
            throw new EnrollmentException("Test throw");
        });

        // Test PrerequisiteNotMetException
        assertThrows(PrerequisiteNotMetException.class, () -> {
            throw new PrerequisiteNotMetException("Test throw");
        });

        // Test StudentNotFoundException
        assertThrows(StudentNotFoundException.class, () -> {
            throw new StudentNotFoundException("Test throw");
        });
    }

    // Test exception with specific messages
    @Test
    void testExceptionSpecificMessages() {
        CourseFullException fullException = new CourseFullException("CS101 is full. Capacity: 30, Enrolled: 30");
        assertTrue(fullException.getMessage().contains("CS101"));
        assertTrue(fullException.getMessage().contains("full"));

        CourseNotFoundException notFoundException = new CourseNotFoundException("Course CS999 not found in database");
        assertTrue(notFoundException.getMessage().contains("CS999"));
        assertTrue(notFoundException.getMessage().contains("not found"));

        StudentNotFoundException studentException = new StudentNotFoundException("Student with ID 12345 not found");
        assertTrue(studentException.getMessage().contains("12345"));
    }

    // Test exception chaining
    @Test
    void testExceptionChaining() {
        // Create a chain of exceptions
        NullPointerException rootCause = new NullPointerException("Root cause");
        IllegalArgumentException intermediate = new IllegalArgumentException("Intermediate", rootCause);
        StudentNotFoundException topLevel = new StudentNotFoundException("Top level", intermediate);

        assertEquals("Top level", topLevel.getMessage());
        assertEquals("Intermediate", topLevel.getCause().getMessage());
        assertEquals("Root cause", topLevel.getCause().getCause().getMessage());
        assertTrue(topLevel.getCause().getCause() instanceof NullPointerException);
    }
}