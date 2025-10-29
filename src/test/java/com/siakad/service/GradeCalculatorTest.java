package com.siakad.service;

import com.siakad.model.CourseGrade;
import com.siakad.model.Student;
import com.siakad.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GradeCalculatorTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GradeCalculator gradeCalculator;

    // ===== STUB IMPLEMENTATIONS =====

    /**
     * Manual Stub untuk menyediakan data grade mahasiswa
     */
    static class StubGradeProvider {
        private Map<String, List<CourseGrade>> studentGrades = new HashMap<>();

        // Hard-coded behavior untuk testing
        public List<CourseGrade> getGradesForStudent(String studentId) {
            return studentGrades.getOrDefault(studentId, createDefaultGrades());
        }

        public void setGradesForStudent(String studentId, List<CourseGrade> grades) {
            studentGrades.put(studentId, grades);
        }

        private List<CourseGrade> createDefaultGrades() {
            return Arrays.asList(
                    new CourseGrade("KRIP101", 3, 3.5),
                    new CourseGrade("MATH101", 4, 4.0),
                    new CourseGrade("FIS101", 3, 3.0)
            );
        }
    }

    /**
     * Manual Stub untuk academic status calculator
     */
    static class StubAcademicStatusCalculator {
        // Hard-coded rules untuk testing - SESUAI DENGAN SOAL
        public String calculateStatus(double gpa, int semester) {
            if (semester <= 2) {
                return gpa >= 2.0 ? "ACTIVE" : "PROBATION";
            } else if (semester <= 4) {
                if (gpa >= 2.25) return "ACTIVE";
                if (gpa >= 2.0) return "PROBATION";
                return "SUSPENDED";
            } else {
                if (gpa >= 2.5) return "ACTIVE";
                if (gpa >= 2.0) return "PROBATION";
                return "SUSPENDED";
            }
        }
    }

    // ===== TESTS DENGAN STUB =====

    @Test
    void testCalculateGPA_WithStub() {
        // Setup Stub
        StubGradeProvider stubGradeProvider = new StubGradeProvider();

        // Setup hard-coded test data
        List<CourseGrade> stubGrades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 4.0),
                new CourseGrade("KRIP102", 3, 3.0),
                new CourseGrade("KRIP103", 4, 4.0)
        );
        stubGradeProvider.setGradesForStudent("230209003", stubGrades);

        // Test menggunakan data dari stub
        List<CourseGrade> grades = stubGradeProvider.getGradesForStudent("230209003");
        double gpa = gradeCalculator.calculateGPA(grades);

        // Verify
        assertEquals(3.7, gpa, 0.01);
    }

    @Test
    void testCalculateStudentGPA_WithStub() {
        // Setup Stub dengan berbagai scenario
        StubGradeProvider stubProvider = new StubGradeProvider();

        // Scenario: Student dengan grades average
        List<CourseGrade> averageGrades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 3.0),  // 3*3.0 = 9
                new CourseGrade("KRIP102", 3, 2.5),  // 3*2.5 = 7.5
                new CourseGrade("MATH101", 4, 3.0) // 4*3.0 = 12
        );
        // Total: 9 + 7.5 + 12 = 28.5
        // Total credits: 3 + 3 + 4 = 10
        // GPA: 28.5 / 10 = 2.85
        stubProvider.setGradesForStudent("230209003", averageGrades);

        // Test menggunakan stub
        double averageGPA = gradeCalculator.calculateGPA(
                stubProvider.getGradesForStudent("230209003")
        );

        // Verify hasil yang benar
        assertEquals(2.85, averageGPA, 0.01);
    }

    @Test
    void testDetermineAcademicStatus_WithStub() {
        // Setup Stub
        StubAcademicStatusCalculator stubCalculator = new StubAcademicStatusCalculator();

        // Test case: GPA 2.2 di semester 3 = PROBATION
        // (karena 2.0-2.24 → PROBATION untuk semester 3-4)
        String status = stubCalculator.calculateStatus(2.2, 3);

        // Verify
        assertEquals("PROBATION", status);
    }

    // ===== TESTS DENGAN MOCK =====

    @Test
    void testCalculateStudentGPA_WithMockito() {
        // Arrange
        List<CourseGrade> mockGrades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 3.5),  // 3*3.5 = 10.5
                new CourseGrade("KRIP102", 3, 4.0),  // 3*4.0 = 12
                new CourseGrade("MATH101", 4, 3.0) // 4*3.0 = 12
        );
        // Total: 10.5 + 12 + 12 = 34.5
        // Total credits: 3 + 3 + 4 = 10
        // GPA: 34.5 / 10 = 3.45

        // Act - Test langsung method calculateGPA
        double gpa = gradeCalculator.calculateGPA(mockGrades);

        // Assert - Gunakan nilai yang benar
        assertEquals(3.45, gpa, 0.01);
    }

    @Test
    void testDetermineAcademicStatus_WithMockito() {
        // Arrange
        String studentId = "230209003";
        Student mockStudent = new Student();
        mockStudent.setStudentId(studentId);
        mockStudent.setName("Amanda Bunga Lestari");
        mockStudent.setEmail("amandabunga@pnc.ac.id");
        mockStudent.setMajor("Rekayasa Keamanan Siber");
        mockStudent.setSemester(5);
        mockStudent.setGpa(2.2);

        // Mock repository behavior - test bahwa kita bisa menggunakan mock
        when(studentRepository.findById(studentId)).thenReturn(mockStudent);

        // Act - Test bahwa mock bekerja
        Student student = studentRepository.findById(studentId);

        // Test GradeCalculator dengan data dari mock
        String status = gradeCalculator.determineAcademicStatus(student.getGpa(), student.getSemester());

        // Assert
        assertEquals("PROBATION", status); // 2.2 < 2.5 untuk semester 5
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testStudentIntegration_WithMockito() {
        // Arrange
        String studentId = "230209003";
        Student mockStudent = new Student();
        mockStudent.setStudentId(studentId);
        mockStudent.setName("Amanda Bunga Lestari");
        mockStudent.setEmail("amandabunga@pnc.ac.id");
        mockStudent.setMajor("Rekayasa Keamanan Siber");
        mockStudent.setGpa(3.2);

        // Mock behavior
        when(studentRepository.findById(studentId)).thenReturn(mockStudent);

        // Act - Test integrasi
        Student student = studentRepository.findById(studentId);
        int maxCredits = gradeCalculator.calculateMaxCredits(student.getGpa());

        // Assert
        assertEquals(24, maxCredits); // 3.2 >= 3.0 → 24 SKS
        verify(studentRepository, times(1)).findById(studentId);
    }

    // ===== EXISTING TESTS (Tetap dipertahankan) =====
    @BeforeEach
    void setUp() {
        // Setup sudah dilakukan oleh @InjectMocks
    }

    @Test
    void testCalculateGPA_ValidGrades() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 4.0),
                new CourseGrade("KRIP102", 3, 3.0),
                new CourseGrade("KRIP103", 4, 4.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(3.7, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_EmptyList() {
        List<CourseGrade> grades = new ArrayList<>();
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_NullList() {
        double gpa = gradeCalculator.calculateGPA(null);
        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_AllGradeTypes() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 4.0),
                new CourseGrade("KRIP102", 3, 3.0),
                new CourseGrade("KRIP103", 3, 2.0),
                new CourseGrade("KRIP104", 3, 1.0),
                new CourseGrade("KRIP105", 3, 0.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(2.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_ZeroCredits() {
        CourseGrade grade = new CourseGrade("KRIP101", 0, 4.0);
        List<CourseGrade> grades = Arrays.asList(grade);
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_InvalidGradePoint() {
        CourseGrade invalidGrade = new CourseGrade("KRIP101", 3, 5.0);
        List<CourseGrade> grades = Arrays.asList(invalidGrade);
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });
    }

    @Test
    void testCalculateGPA_NegativeGradePoint() {
        CourseGrade invalidGrade = new CourseGrade("KRIP101", 3, -1.0);
        List<CourseGrade> grades = Arrays.asList(invalidGrade);
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });
    }

    @Test
    void testDetermineAcademicStatus_Semester1_2() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.0, 1));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 2));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.9, 1));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.5, 2));
    }

    @Test
    void testDetermineAcademicStatus_Semester3_4() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.25, 3));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.0, 4));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 3));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.24, 4));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.9, 3));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.5, 4));
    }

    @Test
    void testDetermineAcademicStatus_Semester5Plus() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 5));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 6));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 5));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.49, 7));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.9, 5));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.0, 8));
    }

    @Test
    void testDetermineAcademicStatus_InvalidGPA() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(-1.0, 3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(4.1, 3);
        });
    }

    @Test
    void testDetermineAcademicStatus_InvalidSemester() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, -1);
        });
    }

    @Test
    void testCalculateMaxCredits() {
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.0));
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.5));
        assertEquals(24, gradeCalculator.calculateMaxCredits(4.0));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.5));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.75));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.99));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.0));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.25));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.49));
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.9));
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.5));
        assertEquals(15, gradeCalculator.calculateMaxCredits(0.0));
    }

    @Test
    void testCalculateMaxCredits_InvalidGPA() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(-1.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(4.1);
        });
    }
}