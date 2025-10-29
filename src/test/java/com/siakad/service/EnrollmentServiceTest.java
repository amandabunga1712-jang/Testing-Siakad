package com.siakad.service;

import com.siakad.exception.*;
import com.siakad.model.Course;
import com.siakad.model.Enrollment;
import com.siakad.model.Student;
import com.siakad.repository.CourseRepository;
import com.siakad.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private GradeCalculator gradeCalculator;

    @InjectMocks
    private EnrollmentService enrollmentService;

    // ===== MANUAL STUB IMPLEMENTATIONS =====

    /**
     * Manual Stub untuk StudentRepository - SESUAI INTERFACE
     */
    static class ManualStudentRepositoryStub implements StudentRepository {
        private Map<String, Student> students = new HashMap<>();

        public void addStudent(Student student) {
            students.put(student.getStudentId(), student);
        }

        @Override
        public Student findById(String studentId) {
            return students.get(studentId);
        }

        @Override
        public void update(Student student) {
            students.put(student.getStudentId(), student);
        }

        @Override
        public java.util.List<Course> getCompletedCourses(String studentId) {
            return Arrays.asList(); // Return empty list untuk testing
        }
    }

    /**
     * Manual Stub untuk CourseRepository - SESUAI INTERFACE
     */
    static class ManualCourseRepositoryStub implements CourseRepository {
        private Map<String, Course> courses = new HashMap<>();
        private Map<String, Boolean> prerequisiteResults = new HashMap<>();

        public void addCourse(Course course) {
            courses.put(course.getCourseCode(), course);
        }

        public void setPrerequisiteResult(String studentId, String courseCode, boolean result) {
            prerequisiteResults.put(studentId + "_" + courseCode, result);
        }

        @Override
        public Course findByCourseCode(String courseCode) {
            return courses.get(courseCode);
        }

        @Override
        public void update(Course course) {
            courses.put(course.getCourseCode(), course);
        }

        @Override
        public boolean isPrerequisiteMet(String studentId, String courseCode) {
            return prerequisiteResults.getOrDefault(studentId + "_" + courseCode, false);
        }
    }

    // ===== HELPER METHODS =====

    private Student createStudent(String studentId, String name, String email, String major,
                                  int semester, double gpa, String academicStatus) {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name);
        student.setEmail(email);
        student.setMajor(major);
        student.setSemester(semester);
        student.setGpa(gpa);
        student.setAcademicStatus(academicStatus);
        return student;
    }

    private Course createCourse(String courseCode, String courseName, int credits,
                                int capacity, int enrolledCount, String lecturer) {
        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setCapacity(capacity);
        course.setEnrolledCount(enrolledCount);
        course.setLecturer(lecturer);
        return course;
    }

    // ===== TESTS DENGAN MOCK (Mockito) =====

    @Test
    void testEnrollCourse_StudentNotFound() {
        when(studentRepository.findById("999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class, () -> {
            enrollmentService.enrollCourse("999", "CS101");
        });

        verify(studentRepository, times(1)).findById("999");
        verify(courseRepository, never()).findByCourseCode(anyString());
    }

    @Test
    void testEnrollCourse_StudentSuspended() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 1.5, "SUSPENDED");

        when(studentRepository.findById("230209003")).thenReturn(student);

        assertThrows(EnrollmentException.class, () -> {
            enrollmentService.enrollCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, never()).findByCourseCode(anyString());
    }

    @Test
    void testEnrollCourse_CourseNotFound() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(null);

        assertThrows(CourseNotFoundException.class, () -> {
            enrollmentService.enrollCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
    }

    @Test
    void testEnrollCourse_CourseFull() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                30, "Dr. Smith"); // Full

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(course);

        assertThrows(CourseFullException.class, () -> {
            enrollmentService.enrollCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
        verify(courseRepository, never()).isPrerequisiteMet(anyString(), anyString());
    }

    @Test
    void testEnrollCourse_PrerequisiteNotMet() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                25, "Dr. Smith");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(course);
        when(courseRepository.isPrerequisiteMet("230209003", "KRIP101")).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class, () -> {
            enrollmentService.enrollCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
        verify(courseRepository, times(1)).isPrerequisiteMet("230209003", "KRIP101");
    }

    @Test
    void testEnrollCourse_Success() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                25, "Dr. Smith");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(course);
        when(courseRepository.isPrerequisiteMet("230209003", "KRIP101")).thenReturn(true);

        // Untuk void method update(), gunakan doNothing()
        doNothing().when(courseRepository).update(any(Course.class));

        Enrollment enrollment = enrollmentService.enrollCourse("230209003", "KRIP101");

        // Assertions
        assertNotNull(enrollment);
        assertEquals("230209003", enrollment.getStudentId());
        assertEquals("KRIP101", enrollment.getCourseCode());
        assertEquals("APPROVED", enrollment.getStatus());
        assertNotNull(enrollment.getEnrollmentDate());

        // Verifications
        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
        verify(courseRepository, times(1)).isPrerequisiteMet("230209003", "KRIP101");
        verify(courseRepository, times(1)).update(any(Course.class));
        verify(notificationService, times(1))
                .sendEmail("amandabunga@pnc.ac.id", "Enrollment Confirmation",
                        "You have been enrolled in: Kriptografi Lanjut");
    }

    @Test
    void testValidateCreditLimit_StudentNotFound() {
        when(studentRepository.findById("999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class, () -> {
            enrollmentService.validateCreditLimit("999", 20);
        });

        verify(studentRepository, times(1)).findById("999");
        verify(gradeCalculator, never()).calculateMaxCredits(anyDouble());
    }

    @Test
    void testValidateCreditLimit_WithinLimit() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(gradeCalculator.calculateMaxCredits(3.5)).thenReturn(24);

        boolean result = enrollmentService.validateCreditLimit("230209003", 20);

        assertTrue(result);
        verify(studentRepository, times(1)).findById("230209003");
        verify(gradeCalculator, times(1)).calculateMaxCredits(3.5);
    }

    @Test
    void testValidateCreditLimit_ExceedLimit() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 1.5, "ACTIVE");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(gradeCalculator.calculateMaxCredits(1.5)).thenReturn(15);

        boolean result = enrollmentService.validateCreditLimit("230209003", 20);

        assertFalse(result);
        verify(studentRepository, times(1)).findById("230209003");
        verify(gradeCalculator, times(1)).calculateMaxCredits(1.5);
    }

    @Test
    void testDropCourse_StudentNotFound() {
        when(studentRepository.findById("999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class, () -> {
            enrollmentService.dropCourse("999", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("999");
        verify(courseRepository, never()).findByCourseCode(anyString());
    }

    @Test
    void testDropCourse_CourseNotFound() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(null);

        assertThrows(CourseNotFoundException.class, () -> {
            enrollmentService.dropCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
    }

    @Test
    void testDropCourse_Success() {

        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                25, "Dr. Smith");

        when(studentRepository.findById("230209003")).thenReturn(student);
        when(courseRepository.findByCourseCode("KRIP101")).thenReturn(course);
        doNothing().when(courseRepository).update(any(Course.class));

        assertDoesNotThrow(() -> {
            enrollmentService.dropCourse("230209003", "KRIP101");
        });

        verify(studentRepository, times(1)).findById("230209003");
        verify(courseRepository, times(1)).findByCourseCode("KRIP101");
        verify(courseRepository, times(1)).update(any(Course.class));
        verify(notificationService, times(1))
                .sendEmail("amandabunga@pnc.ac.id", "Course Drop Confirmation",
                        "You have dropped: Kriptografi Lanjut");
    }

    // ===== TESTS DENGAN MANUAL STUB =====

    @Test
    void testEnrollCourse_WithManualStub_Success() {
        // Setup manual stubs
        ManualStudentRepositoryStub studentStub = new ManualStudentRepositoryStub();
        ManualCourseRepositoryStub courseStub = new ManualCourseRepositoryStub();


        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                25, "Dr. Smith");

        studentStub.addStudent(student);
        courseStub.addCourse(course);
        courseStub.setPrerequisiteResult("230209003", "KRIP101", true);

        // Mock untuk dependencies lain
        NotificationService mockNotification = mock(NotificationService.class);
        GradeCalculator mockGradeCalculator = mock(GradeCalculator.class);

        // Create service dengan manual stubs
        EnrollmentService manualService = new EnrollmentService(
                studentStub,          // Manual stub
                courseStub,           // Manual stub
                mockNotification,     // Mock
                mockGradeCalculator   // Mock
        );

        // Test
        Enrollment enrollment = manualService.enrollCourse("230209003", "KRIP101");

        // Verify
        assertNotNull(enrollment);
        assertEquals("230209003", enrollment.getStudentId());
        assertEquals("KRIP101", enrollment.getCourseCode());
        assertEquals("APPROVED", enrollment.getStatus());

        // Verify notification
        verify(mockNotification, times(1))
                .sendEmail("amandabunga@pnc.ac.id", "Enrollment Confirmation",
                        "You have been enrolled in: Kriptografi Lanjut");
    }

    @Test
    void testEnrollCourse_WithManualStub_StudentSuspended() {
        // Setup manual stubs
        ManualStudentRepositoryStub studentStub = new ManualStudentRepositoryStub();
        ManualCourseRepositoryStub courseStub = new ManualCourseRepositoryStub();

        //
        Student student = createStudent("230209003", "Amanda Bunga Lestari", "amandabunga@pnc.ac.id",
                "Rekayasa Keamanan Siber", 5, 1.8, "SUSPENDED");
        Course course = createCourse("KRIP101", "Kriptografi Lanjut", 3, 30,
                25, "Dr. Smith");

        studentStub.addStudent(student);
        courseStub.addCourse(course);

        // Mock dependencies
        NotificationService mockNotification = mock(NotificationService.class);
        GradeCalculator mockGradeCalculator = mock(GradeCalculator.class);

        EnrollmentService manualService = new EnrollmentService(
                studentStub, courseStub, mockNotification, mockGradeCalculator
        );

        // Test & Verify
        assertThrows(EnrollmentException.class, () -> {
            manualService.enrollCourse("230209003", "KRIP101");
        });

        // Verify no notification sent for suspended student
        verify(mockNotification, never()).sendEmail(anyString(), anyString(), anyString());
    }
}