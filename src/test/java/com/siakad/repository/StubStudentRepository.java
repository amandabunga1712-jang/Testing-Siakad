package com.siakad.repository;

import com.siakad.model.Course;
import com.siakad.model.Student;
import java.util.*;

public class StubStudentRepository implements StudentRepository {
    private Map<String, Student> students = new HashMap<>();
    private Map<String, List<Course>> completedCourses = new HashMap<>();

    @Override
    public Student findById(String studentId) {
        return students.get(studentId);
    }

    @Override
    public void update(Student student) {
        students.put(student.getStudentId(), student);
    }

    @Override
    public List<Course> getCompletedCourses(String studentId) {
        return completedCourses.getOrDefault(studentId, new ArrayList<>());
    }

    // Helper methods
    public void addStudent(Student student) {
        students.put(student.getStudentId(), student);
    }

    public void addCompletedCourse(String studentId, Course course) {
        completedCourses.computeIfAbsent(studentId, k -> new ArrayList<>()).add(course);
    }
}