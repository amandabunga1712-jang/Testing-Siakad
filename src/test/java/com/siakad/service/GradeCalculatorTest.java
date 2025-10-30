package com.siakad.service;

import com.siakad.model.CourseGrade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GradeCalculatorTest {

    private GradeCalculator gradeCalculator;

    @BeforeEach
    void setUp() {
        gradeCalculator = new GradeCalculator();
    }

    // ===== TESTS UNTUK calculateGPA() =====

    @Test
    void testCalculateGPA_ValidGrades() {
        // Test dengan nilai normal
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 4.0),  // 12 points
                new CourseGrade("KRIP102", 3, 3.0),  // 9 points
                new CourseGrade("KRIP103", 4, 4.0)   // 16 points
        );
        // Total: 37 points, 10 credits = 3.7 GPA

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
        // Test semua jenis nilai (A, B, C, D, E)
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 4.0),  // A = 12
                new CourseGrade("KRIP102", 3, 3.0),  // B = 9
                new CourseGrade("KRIP103", 3, 2.0),  // C = 6
                new CourseGrade("KRIP104", 3, 1.0),  // D = 3
                new CourseGrade("KRIP105", 3, 0.0)   // E = 0
        );
        // Total: 30 points, 15 credits = 2.0 GPA

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(2.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_ZeroCredits() {
        // Test dengan SKS = 0
        CourseGrade grade = new CourseGrade("KRIP101", 0, 4.0);
        List<CourseGrade> grades = Arrays.asList(grade);
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void testCalculateGPA_InvalidGradePoint() {
        // Test dengan grade point > 4.0
        CourseGrade invalidGrade = new CourseGrade("KRIP101", 3, 5.0);
        List<CourseGrade> grades = Arrays.asList(invalidGrade);

        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });
    }

    @Test
    void testCalculateGPA_NegativeGradePoint() {
        // Test dengan grade point negatif
        CourseGrade invalidGrade = new CourseGrade("KRIP101", 3, -1.0);
        List<CourseGrade> grades = Arrays.asList(invalidGrade);

        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });
    }

    @Test
    void testCalculateGPA_Precision() {
        // Test presisi perhitungan
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 3.33),  // 9.99
                new CourseGrade("KRIP102", 3, 3.67)   // 11.01
        );
        // Total: 21.0 points, 6 credits = 3.5 GPA

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(3.5, gpa, 0.01);
    }

    // ===== TESTS UNTUK determineAcademicStatus() =====

    @Test
    void testDetermineAcademicStatus_Semester1_2() {
        // Semester 1-2: IPK ≥ 2.0 → ACTIVE, < 2.0 → PROBATION
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.0, 1));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 2));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.9, 1));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.5, 2));
    }

    @Test
    void testDetermineAcademicStatus_Semester3_4() {
        // Semester 3-4: IPK ≥ 2.25 → ACTIVE, 2.0-2.24 → PROBATION, < 2.0 → SUSPENDED
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.25, 3));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.0, 4));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 3));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.24, 4));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.9, 3));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.5, 4));
    }

    @Test
    void testDetermineAcademicStatus_Semester5Plus() {
        // Semester 5+: IPK ≥ 2.5 → ACTIVE, 2.0-2.49 → PROBATION, < 2.0 → SUSPENDED
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 5));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 6));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 5));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.49, 7));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.9, 5));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.0, 8));
    }

    @Test
    void testDetermineAcademicStatus_BoundaryValues() {
        // Test nilai batas
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.0, 2));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.99, 2));

        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.25, 3));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.24, 3));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 3));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.99, 3));

        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 5));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.49, 5));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 5));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.99, 5));
    }

    @Test
    void testDetermineAcademicStatus_InvalidGPA() {
        // Test GPA invalid
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(-1.0, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(4.1, 3);
        });
    }

    @Test
    void testDetermineAcademicStatus_InvalidSemester() {
        // Test semester invalid
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, -1);
        });
    }

    // ===== TESTS UNTUK calculateMaxCredits() =====

    @Test
    void testCalculateMaxCredits() {
        // IPK ≥ 3.0: 24 SKS
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.0));
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.5));
        assertEquals(24, gradeCalculator.calculateMaxCredits(4.0));

        // IPK 2.5-2.99: 21 SKS
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.5));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.75));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.99));

        // IPK 2.0-2.49: 18 SKS
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.0));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.25));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.49));

        // IPK < 2.0: 15 SKS
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.9));
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.5));
        assertEquals(15, gradeCalculator.calculateMaxCredits(0.0));
    }

    @Test
    void testCalculateMaxCredits_BoundaryValues() {
        // Test nilai batas
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.0));   // Lower boundary 3.0
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.99));  // Upper boundary 2.99
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.5));   // Lower boundary 2.5
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.49));  // Upper boundary 2.49
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.0));   // Lower boundary 2.0
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.99));  // Upper boundary 1.99
    }

    @Test
    void testCalculateMaxCredits_InvalidGPA() {
        // Test GPA invalid
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(-1.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(4.1);
        });
    }

    // ===== COMPREHENSIVE WORKFLOW TEST =====

    @Test
    void testCompleteAcademicWorkflow() {
        // Test complete workflow dari grades → GPA → status → credits
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 3.5),  // 10.5
                new CourseGrade("KRIP102", 3, 4.0),  // 12
                new CourseGrade("KRIP103", 4, 3.0)   // 12
        );
        // Total: 34.5 points, 10 credits = 3.45 GPA

        // Step 1: Calculate GPA
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(3.45, gpa, 0.01);

        // Step 2: Determine academic status (semester 5)
        String status = gradeCalculator.determineAcademicStatus(gpa, 5);
        assertEquals("ACTIVE", status);  // 3.45 ≥ 2.5

        // Step 3: Calculate max credits
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);
        assertEquals(24, maxCredits);    // 3.45 ≥ 3.0
    }

    @Test
    void testStudentScenario_Amanda() {
        // Test scenario khusus untuk data Amanda
        List<CourseGrade> amandaGrades = Arrays.asList(
                new CourseGrade("KRIP101", 3, 3.7),
                new CourseGrade("KRIP102", 3, 3.3),
                new CourseGrade("KRIP201", 4, 4.0),
                new CourseGrade("MAT101", 3, 3.0)
        );
        // Total: (3*3.7) + (3*3.3) + (4*4.0) + (3*3.0) = 11.1 + 9.9 + 16 + 9 = 46
        // Total credits: 3 + 3 + 4 + 3 = 13
        // GPA: 46 / 13 = 3.538

        double gpa = gradeCalculator.calculateGPA(amandaGrades);
        assertEquals(3.54, gpa, 0.01);

        // Status untuk semester 5 dengan IPK 3.54
        String status = gradeCalculator.determineAcademicStatus(gpa, 5);
        assertEquals("ACTIVE", status);

        // Max credits untuk IPK 3.54
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);
        assertEquals(24, maxCredits);
    }
}